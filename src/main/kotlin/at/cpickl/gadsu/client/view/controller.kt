package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientDeletedEvent
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.image.DeleteImageEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.CurrentPropertiesChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import at.cpickl.gadsu.view.ChangeMainContentEvent
import at.cpickl.gadsu.view.MainContentChangedEvent
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.logic.ChangeBehaviour
import at.cpickl.gadsu.view.logic.ChangesChecker
import at.cpickl.gadsu.view.logic.ChangesCheckerCallback
import at.cpickl.gadsu.view.logic.calculateInsertIndex
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject


@Logged
@Suppress("UNUSED_PARAMETER")
open class ClientViewController @Inject constructor(
        private val bus: EventBus,
        private val clock: Clock,
        private val view: ClientView,
        private val clientService: ClientService,
        private val currentClient: CurrentClient,
        private val dialogs: Dialogs
) {

    private val log = LOG(javaClass)

    private val changesChecker = ChangesChecker(dialogs, object : ChangesCheckerCallback {
        override fun isModified() = view.detailView.isModified()
        override fun save() = saveClient(view.detailView.readClient())
    })

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        view.masterView.initClients(clientService.findAll())
        bus.post(ChangeMainContentEvent(view))
        bus.post(CreateNewClientEvent()) // show initial client view for insert prototype (update ui fields)
    }

    @Subscribe open fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        if (changesChecker.checkChanges() === ChangeBehaviour.ABORT) {
            return
        }

        if (currentClient.data.yetPersisted) {
            // there was a client selected, and now we want to create a new client
            bus.post(ClientUnselectedEvent(currentClient.data))
        }

        view.masterView.selectClient(null)
        val newCreatingClient = Client.INSERT_PROTOTYPE.copy(created = clock.now())

        currentClient.data = newCreatingClient
        view.detailView.focusFirst()
    }


    @Subscribe open fun onSaveClientEvent(event: SaveClientEvent) {
        val client = view.detailView.readClient()
        saveClient(client)
    }

    @Subscribe open fun onClientCreatedEvent(event: ClientCreatedEvent) {
        val index = view.masterView.model.calculateInsertIndex(event.client)
        view.masterView.insertClient(index, event.client)
        view.masterView.selectClient(event.client)

        currentClient.data = event.client
    }

    @Subscribe open fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        view.masterView.changeClient(event.client)
//        view.masterView.selectClient(event.client) ... nope, not needed

        currentClient.data = event.client
    }

    @Subscribe open fun onClientSelectedEvent(event: ClientSelectedEvent) {
        if (changesChecker.checkChanges() === ChangeBehaviour.ABORT) {
            view.masterView.selectClient(event.previousSelected) // reset selection
            return
        }

        currentClient.data = event.client
    }

    @Subscribe open fun onDeleteClientEvent(event: DeleteClientEvent) {
        dialogs.confirmedDelete("den Klienten '${event.client.fullName}'", {
            clientService.delete(event.client)

            if (event.client.id!!.equals(currentClient.data.id)) {
                bus.post(ClientUnselectedEvent(event.client))
            }
        })
    }

    @Subscribe open fun onDeleteImageEvent(event: DeleteImageEvent) {
        clientService.deletePicture(event.client)
    }

    @Subscribe open fun onClientDeletedEvent(event: ClientDeletedEvent) {
        view.masterView.deleteClient(event.client)

        if (currentClient.data.id != null && currentClient.data.id.equals(event.client.id)) {
            val newInsert = Client.INSERT_PROTOTYPE
            currentClient.data = newInsert
        }
    }

    @Subscribe open fun onCurrentPropertiesChangedEvent(event: CurrentPropertiesChangedEvent) {
        event.forClient { if (it.yetPersisted) view.masterView.changeClient(it) }
    }

    @Subscribe open fun onShowClientViewEvent(event: ShowClientViewEvent) {
        bus.post(ChangeMainContentEvent(view))
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        if (event.oldContent === view) {
            view.closePreparations()
        }
    }


    private fun saveClient(client: Client) {
        log.trace("saveClient(client={})", client)

        if (client.firstName.isEmpty() && client.lastName.isEmpty()) {
            dialogs.show(
                    title = "Speichern abgebrochen",
                    message = "Es muss zumindest entweder ein Vorname oder ein Nachname eingegeben werden.",
                    buttonLabels = arrayOf("Speichern Abbrechen"),
                    type = DialogType.WARN
            )
            return
        }

        clientService.insertOrUpdate(client)
    }

    fun checkChanges(): ChangeBehaviour {
        return changesChecker.checkChanges()
    }
}
