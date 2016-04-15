package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.client.*
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
        // TODO check for unsaved changes
        view.detailView.currentClient = Client.INSERT_PROTOTYPE
    }

    @Subscribe fun onSaveClientEvent(event: SaveClientEvent) {
        log.trace("onSaveClientEvent(event)")
        val client = view.detailView.readClient()
        if (client.yetPersisted) {
            clientRepo.update(client)
            bus.post(ClientUpdatedEvent(client))
        } else {
            val savedClient = clientRepo.insert(client.copy(created = clock.now()))
            bus.post(ClientCreatedEvent(savedClient))
        }
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
    }

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        log.trace("onClientSelectedEvent(event)")
        view.detailView.currentClient = event.client
    }

    @Subscribe fun onDeleteClientEvent(event: DeleteClientEvent) {
        log.trace("onDeleteClientEvent(event)")

        val selected = dialogs.show(
                title = "Best\u00e4tigung",
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
