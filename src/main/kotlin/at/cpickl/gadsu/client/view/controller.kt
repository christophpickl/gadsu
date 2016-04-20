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
import at.cpickl.gadsu.image.ImageSelectedEvent
import at.cpickl.gadsu.image.readImageIcon
import at.cpickl.gadsu.image.size
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.preferences.Prefs
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


@Suppress("UNUSED_PARAMETER")
class ClientViewController @Inject constructor(
        private val bus: EventBus,
        private val clock: Clock,
        private val view: ClientView,
        private val window: MainWindow,
        private val clientRepo: ClientRepository,
        private val clientService: ClientService,
        private val currentClient: CurrentClient,
        private val dialogs: Dialogs,
        private val prefs: Prefs
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

        if (currentClient.data.yetPersisted) {
            // there was a client selected, and now we want to create a new client
            bus.post(ClientUnselectedEvent(currentClient.data))
        }

        view.masterView.selectClient(null)
        val newCreatingClient = Client.INSERT_PROTOTYPE

        view.detailView.writeClient(newCreatingClient)
        view.detailView.focusFirst()
        currentClient.data = newCreatingClient
    }


    @Subscribe fun onSaveClientEvent(event: SaveClientEvent) {
        log.trace("onSaveClientEvent(event)")
        val client = view.detailView.readClient()
        saveClient(client)
    }

    private fun saveClient(client: Client) {
        if (client.firstName.isEmpty() && client.lastName.isEmpty()) {
            dialogs.show(
                    title = "Speichern abgebrochen",
                    message = "Es muss zumindest entweder ein Vorname oder ein Nachname eingegeben werden.",
                    buttonLabels = arrayOf("Speichern Abbrechen"),
                    type = DialogType.WARN
            )
            return
        }

        if (client.yetPersisted) {
            clientRepo.update(client)
            bus.post(ClientUpdatedEvent(client))
            return
        }

        // insert new
        val toBeInserted = client.copy(created = clock.now())

        log.trace("Going to insert: {}", toBeInserted)
        val savedClient = clientRepo.insert(toBeInserted)
        log.trace("Dispatching ClientCreatedEvent: {}", savedClient)

        @Suppress("SENSELESS_COMPARISON")
        if (savedClient === null) throw GadsuException("Impossible state most likely due to wrong test mock setup! Inserted to repo: $toBeInserted")

        bus.post(ClientCreatedEvent(savedClient))
    }

    @Subscribe fun onClientCreatedEvent(event: ClientCreatedEvent) {
        log.trace("onClientCreatedEvent(event)")
        val index = view.masterView.model.calculateInsertIndex(event.client)
        view.masterView.insertClient(index, event.client)
        view.masterView.selectClient(event.client)

        view.detailView.writeClient(event.client)
        currentClient.data = event.client
    }

    @Subscribe fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        log.trace("onClientUpdatedEvent(event)")
        view.masterView.changeClient(event.client)
//        view.masterView.selectClient(event.client) ... nope, not needed

        view.detailView.writeClient(event.client)
        currentClient.data = event.client
    }

    @Subscribe fun onClientSelectedEvent(event: ClientSelectedEvent) {
        log.trace("onClientSelectedEvent(event)")

        if (checkChanges() === ChangeBehaviour.ABORT) {
            view.masterView.selectClient(event.previousSelected) // reset selection
            return
        }

        currentClient.data = event.client
        view.detailView.writeClient(event.client)
    }


    @Subscribe fun onDeleteClientEvent(event: DeleteClientEvent) {
        log.trace("onDeleteClientEvent(event)")

        dialogs.confirmedDelete("den Klienten '${event.client.fullName}'", {
            clientService.delete(event.client)

            if (event.client.id!!.equals(currentClient.data.id)) {
                bus.post(ClientUnselectedEvent(event.client))
            }
        })
    }

    @Subscribe fun onClientDeletedEvent(event: ClientDeletedEvent) {
        log.trace("onClientDeletedEvent(event)")
        view.masterView.deleteClient(event.client)

        if (currentClient.data.id!!.equals(event.client.id)) {
            val newInsert = Client.INSERT_PROTOTYPE
            view.detailView.writeClient(newInsert)
            currentClient.data = newInsert
        }
    }

    @Subscribe fun onShowClientViewEvent(event: ShowClientViewEvent) {
        log.debug("onShowClientViewEvent(event={})", event)

        window.changeContent(view.asComponent())
    }

    @Subscribe fun onImageSelectedEvent(event: ImageSelectedEvent) {
        log.debug("onImageSelectedEvent(event={})", event)
        if (!event.viewNamePrefix.equals(view.detailView.imageViewNamePrefix)) {
            log.debug("Aborting image selection, as was not dispatched for client view.")
            return
        }
        prefs.clientPictureDefaultFolder = event.imageFile.parentFile ?: event.imageFile

        val file = event.imageFile
        val icon = file.readImageIcon()
        val size = icon.size()
        if (size.width != size.height) {
            dialogs.show(
                    title = "Ung\u00fcltige Datei",
                    message = "Das ausgew\u00e4hlte Bild muss gleiche Seitenverh\u00e4ltnisse haben. Die Bildgr\u00f6\u00dfe betr\u00e4gt: ${size.width}x${size.height}",
                    buttonLabels = arrayOf("Okay"),
                    type = DialogType.WARN
            )
            return
        }

        view.detailView.changeImage(file.toMyImage())
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


}

enum class ChangeBehaviour {
    CONTINUE,
    ABORT
}
