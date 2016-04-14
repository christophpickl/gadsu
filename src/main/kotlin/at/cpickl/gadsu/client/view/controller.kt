package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.client.*
import at.cpickl.gadsu.service.Clock
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
        private val clientRepo: ClientRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onAppStartupEvent(event: AppStartupEvent) {
        log.trace("onAppStartupEvent(event)")
        view.masterView.initClients(clientRepo.findAll())
    }

    @Subscribe fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        log.trace("onCreateNewClientEvent(event)")
        // TODO check for unsaved changes
        view.detailView.changeClient(Client.INSERT_PROTOTYPE)
    }

    @Subscribe fun onSaveClientEvent(event: SaveClientEvent) {
        log.trace("onSaveClientEvent(event)")
        val client = view.detailView.readClient()
        if (client.yetPersisted) {
            // FIXME update client logic
            println("not yet implemented")
        } else {
            val savedClient = clientRepo.insert(client.withCreated(clock.now()))
            bus.post(ClientCreatedEvent(savedClient))
        }
    }

    @Subscribe fun onClientCreatedEvent(event: ClientCreatedEvent) {
        log.trace("onClientCreatedEvent(event)")
        val index = calculateIndex(view.masterView.model, event.client)
        view.masterView.insertClient(index, event.client)
        view.masterView.selectClient(event.client)
        view.detailView.changeClient(event.client)
    }

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        log.trace("onClientSelectedEvent(event)")
        view.detailView.changeClient(event.client)
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
