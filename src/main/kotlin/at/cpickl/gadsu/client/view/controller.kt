package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientDeletedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.view.MainWindow
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.components.calculateInsertIndex
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import org.slf4j.LoggerFactory


class ClientViewController @Inject constructor(
        private val bus: EventBus,
        private val clock: Clock,
        private val view: ClientView,
        private val window: MainWindow,
        private val clientRepo: ClientRepository,
        private val clientService: ClientService,
        private val currentClient: CurrentClient,
        private val dialogs: Dialogs
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onAppStartupEvent(event: AppStartupEvent) {
        log.trace("onAppStartupEvent(event)")
        view.masterView.initClients(clientRepo.findAll())

        bus.post(ShowClientViewEvent())
    }

    @Subscribe fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        log.trace("onCreateNewClientEvent(event)")

        if (checkChanges() === ChangeBehaviour.ABORT) {
            return
        }

        // TODO right now we still need 'view.detailView.currentClient', will be changed in future
        if (view.detailView.currentClient.yetPersisted) {
            bus.post(ClientUnselectedEvent(view.detailView.currentClient))
        }

        view.masterView.selectClient(null)
        val newCreatingClient = Client.INSERT_PROTOTYPE

        view.detailView.currentClient = newCreatingClient
        currentClient.data = newCreatingClient // dispatches an event
    }


    @Subscribe fun onSaveClientEvent(event: SaveClientEvent) {
        log.trace("onSaveClientEvent(event)")
        val client = view.detailView.readClient()
        saveClient(client)
    }

    @Subscribe fun onClientCreatedEvent(event: ClientCreatedEvent) {
        log.trace("onClientCreatedEvent(event)")
        val index = view.masterView.model.calculateInsertIndex(event.client)
        view.masterView.insertClient(index, event.client)
        view.masterView.selectClient(event.client)

        view.detailView.currentClient = event.client
        currentClient.data = event.client
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

        if (checkChanges() === ChangeBehaviour.ABORT) {
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
        if (selected === null || selected.equals("Abbrechen")) {
            return
        }

        clientService.delete(event.client)

        if (event.client.id!!.equals(view.detailView.currentClient.id)) {
            bus.post(ClientUnselectedEvent(event.client))
        }
    }

    @Subscribe fun onClientDeletedEvent(event: ClientDeletedEvent) {
        log.trace("onClientDeletedEvent(event)")
        view.masterView.deleteClient(event.client)
        if (view.detailView.currentClient.equals(event.client)) {
            view.detailView.currentClient = Client.INSERT_PROTOTYPE
        }
    }

    @Subscribe fun onShowClientViewEvent(event: ShowClientViewEvent) {
        log.debug("onShowClientViewEvent(event={})", event)

        window.changeContent(view.asComponent())
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
            val toBeInserted = client.copy(created = clock.now())
            log.trace("Going to insert: {}", toBeInserted)
            val savedClient = clientRepo.insert(toBeInserted)
            log.trace("Dispatching ClientCreatedEvent: {}", savedClient)
            bus.post(ClientCreatedEvent(savedClient))
        }
    }


}

enum class ChangeBehaviour {
    CONTINUE,
    ABORT
}
