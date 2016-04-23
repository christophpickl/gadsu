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
import at.cpickl.gadsu.view.components.calculateInsertIndex
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject


@Logged
@Suppress("UNUSED_PARAMETER")
open class ClientViewController @Inject constructor(
        private val bus: EventBus,
        private val clock: Clock,
        private val view: ClientView,
        private val clientRepo: ClientRepository,
        private val clientService: ClientService,
        private val currentClient: CurrentClient,
        private val dialogs: Dialogs
) {

    private val log = LOG(javaClass)

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        view.masterView.initClients(clientRepo.findAll())
        bus.post(ChangeMainContentEvent(view))
        bus.post(CreateNewClientEvent()) // show initial client view for insert prototype (update ui fields)
    }

    @Subscribe open fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        if (checkChanges() === ChangeBehaviour.ABORT) {
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
        if (checkChanges() === ChangeBehaviour.ABORT) {
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
        clientService.deleteImage(event.client)
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

}

enum class ChangeBehaviour {
    CONTINUE,
    ABORT
}
