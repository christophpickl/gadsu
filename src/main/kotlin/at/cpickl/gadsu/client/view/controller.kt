package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientDeletedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import javax.swing.ListModel


@Suppress("UNUSED_PARAMETER")
class ClientViewController @Inject constructor(
        private val bus: EventBus,
        private val clock: Clock,
        private val view: ClientView,
        private val clientRepo: ClientRepository,
        private val dialogs: Dialogs
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onAppStartupEvent(event: AppStartupEvent) {
        log.trace("onAppStartupEvent(event)")
        view.masterView.initClients(clientRepo.findAll())
    }

    @Subscribe fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        log.trace("onCreateNewClientEvent(event)")

        if (checkChanges() == ChangeBehaviour.ABORT) {
            return
        }
        view.masterView.selectClient(null)
        view.detailView.currentClient = Client.INSERT_PROTOTYPE
    }

    @Subscribe fun onSaveClientEvent(event: SaveClientEvent) {
        log.trace("onSaveClientEvent(event)")
        val client = view.detailView.readClient()
        saveClient(client)
    }

    @Subscribe fun onClientCreatedEvent(event: ClientCreatedEvent) {
        log.trace("onClientCreatedEvent(event)")
        val index = calculateIndex(view.masterView.model, event.client)
        view.masterView.insertClient(index, event.client)
        view.masterView.selectClient(event.client)
        view.detailView.currentClient = event.client
    }

    @Subscribe fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        log.trace("onClientUpdatedEvent(event)")
        view.masterView.changeClient(event.client)
//        view.masterView.selectClient(event.client) ... nope, not needed

        view.detailView.currentClient = event.client
        view.detailView.updateModifiedStateIndicator()
    }

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        log.trace("onClientSelectedEvent(event)")

        if (checkChanges() == ChangeBehaviour.ABORT) {
            view.masterView.selectClient(event.previousSelected) // reset selection
            return
        }
        view.detailView.currentClient = event.client
    }

    @Subscribe fun onDeleteClientEvent(event: DeleteClientEvent) {
        log.trace("onDeleteClientEvent(event)")
        val selected = dialogs.show(
                title = "Klient l\u00f6schen",
                message = "Willst du den Klienten '${event.client.fullName}' wirklich l\u00f6schen?",
                type = DialogType.QUESTION,
                buttonLabels = arrayOf("L\u00f6schen", "Abbrechen")
        )
        if (selected == null || selected.equals("Abbrechen")) {
            return
        }

        clientRepo.delete(event.client)
        bus.post(ClientDeletedEvent(event.client))
    }

    @Subscribe fun onClientDeletedEvent(event: ClientDeletedEvent) {
        log.trace("onClientDeletedEvent(event)")
        view.masterView.deleteClient(event.client)
        if (view.detailView.currentClient.equals(event.client)) {
            view.detailView.currentClient = Client.INSERT_PROTOTYPE
        }
    }

    fun checkChanges(): ChangeBehaviour {
        if (!view.detailView.isModified()) {
            return ChangeBehaviour.CONTINUE
        }
        log.debug("Changes detected.")

        val result = dialogs.show("Ungespeicherte \u00c4nderungen", "Es existieren ungespeicherte \u00c4nderungen. Wie w\u00fcnscht du mit diesen umzugehen?",
                arrayOf("Speichern", "\u00c4nderungen verwerfen", "Abbrechen"), type = DialogType.WARN)

        when (result) {
            "Speichern" -> {
                saveClient(view.detailView.readClient())
                // it would be nicer to continue after saving, but this is somehow complicated because of the EventBus which works asynchronously
                return ChangeBehaviour.ABORT
            }
            "\u00c4nderungen verwerfen" -> {
                return ChangeBehaviour.CONTINUE
            }
            "Abbrechen", null -> {
                return ChangeBehaviour.ABORT
            }
            else -> throw GadsuException("Unhandled dialog option: '$result'")
        }
    }

    private fun saveClient(client: Client) {
        if (client.yetPersisted) {
            clientRepo.update(client)
            bus.post(ClientUpdatedEvent(client))
        } else {
            val savedClient = clientRepo.insert(client.copy(created = clock.now()))
            bus.post(ClientCreatedEvent(savedClient))
        }
    }

    @VisibleForTesting fun calculateIndex(model: ListModel<Client>, client: Client): Int {
        var index = 0
        for (i in 0.rangeTo(model.size - 1)) {
            val c = model.getElementAt(i)
            if (client.compareTo(c) > 0) {
                index++
            } else {
                break
            }
        }
        return index
    }

}

enum class ChangeBehaviour {
    CONTINUE,
    ABORT
}
