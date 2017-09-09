package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.appointment.AppointmentChangedEvent
import at.cpickl.gadsu.appointment.AppointmentDeletedEvent
import at.cpickl.gadsu.appointment.AppointmentSavedEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientChangeCategory
import at.cpickl.gadsu.client.ClientChangeDonation
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientDeletedEvent
import at.cpickl.gadsu.client.ClientNavigateDownEvent
import at.cpickl.gadsu.client.ClientNavigateUpEvent
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.ClientUnselectedEvent
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.client.DeleteCurrentClientEvent
import at.cpickl.gadsu.client.InvalidMailException
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.client.ShowClientViewEvent
import at.cpickl.gadsu.client.ShowInClientsListEvent
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.client.view.detail.ClientTabSelected
import at.cpickl.gadsu.client.view.detail.ClientTabType
import at.cpickl.gadsu.client.view.detail.SelectClientTab
import at.cpickl.gadsu.global.AppStartupEvent
import at.cpickl.gadsu.image.DeleteImageEvent
import at.cpickl.gadsu.image.RequestClientPictureSaveEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentPropertiesChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.differenceDaysTo
import at.cpickl.gadsu.treatment.TreatmentChangedEvent
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.view.ChangeMainContentEvent
import at.cpickl.gadsu.view.MainContentChangedEvent
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.language.Labels
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
        private val treatmentRepo: TreatmentRepository,
        private val currentClient: CurrentClient,
        private val appointmentService: AppointmentService,
        private val dialogs: Dialogs
) {

    private val log = LOG(javaClass)

    private val changesChecker = ChangesChecker(dialogs, object : ChangesCheckerCallback {
        override fun isModified() = view.detailView.isModified()
        override fun save() = saveClient(view.detailView.readClient())
    })

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        reinitClients(showInactives = false)
        bus.post(ChangeMainContentEvent(view))
        bus.post(CreateNewClientEvent()) // show initial client view for insert prototype (update ui fields)
    }

    private fun reinitClients(showInactives: Boolean) {
        val clients = clientService
                .findAll(filterState = if (showInactives) null else ClientState.ACTIVE)
                .map { extendClient(it) }
                .sortedBy { it.client.preferredName }
        view.masterView.initClients(clients)

//        view.masterView.initClients(clientService.findAll(ClientState.ACTIVE).map({ extendClient(it) })) // initially only display actives
    }

    @Subscribe open fun onCreateNewClientEvent(event: CreateNewClientEvent) {
        if (changesChecker.checkChanges() === ChangeBehaviour.ABORT) {
            return
        }

        if (currentClient.data.yetPersisted) {
            // there was a client selected, and now we want to create a new client
            bus.post(ClientUnselectedEvent(currentClient.data))
        }

        view.detailView.changeTab(ClientTabType.MAIN)
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
        val xclient = extendClient(event.client)
        val index = view.masterView.model.calculateInsertIndex(xclient)
        currentClient.data = event.client

        view.masterView.insertClient(index, xclient)
        view.masterView.selectClient(event.client)
    }

    @Subscribe open fun onClientUpdatedEvent(event: ClientUpdatedEvent) {
        view.masterView.changeClient(event.client)
//        view.masterView.selectClient(event.client) ... nope, not needed

        currentClient.data = event.client
    }

    @Subscribe open fun onClientSelectedEvent(event: ClientSelectedEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.ABORT) {
            view.masterView.selectClient(event.previousSelected) // reset selection
            return
        }
        currentClient.data = event.client
        view.closePreparations()
    }

    @Subscribe open fun onDeleteCurrentClientEvent(event: DeleteCurrentClientEvent) {
        doDeleteClient(currentClient.data)
    }

    @Subscribe open fun onDeleteClientEvent(event: DeleteClientEvent) {
        doDeleteClient(event.client)
    }

    @Subscribe open fun onRequestClientPictureSaveEvent(event: RequestClientPictureSaveEvent) {
        if (changesChecker.checkChanges() == ChangeBehaviour.CONTINUE) {
            clientService.savePicture(event.client)
            currentClient.data = event.client
        }
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
        event.forClient { if (it != null && it.yetPersisted) view.masterView.changeClient(it) }
    }

    @Subscribe open fun onShowClientViewEvent(event: ShowClientViewEvent) {
        bus.post(ChangeMainContentEvent(view))
    }

    @Subscribe open fun onMainContentChangedEvent(event: MainContentChangedEvent) {
        if (event.oldContent === view) { // navigate away
            view.closePreparations()
        }
    }

    @Subscribe open fun onClientTabSelected(event: ClientTabSelected) {
        view.detailView.closePreparations()
    }

    @Subscribe open fun onSelectClientTab(event: SelectClientTab) {
        view.detailView.changeTab(event.tab)
    }

    @Subscribe open fun onShowInClientsListEvent(event: ShowInClientsListEvent) {
        reinitClients(event.showInactives)
    }

    @Subscribe open fun onClientNavigateUpEvent(event: ClientNavigateUpEvent) {
        view.masterView.selectPrevious()
    }

    @Subscribe open fun onClientNavigateDownEvent(event: ClientNavigateDownEvent) {
        view.masterView.selectNext()
    }

    @Subscribe open fun onTreatmentCreatedEvent(event: TreatmentCreatedEvent) {
        view.masterView.treatmentCountIncrease(event.treatment.clientId)
        recalcRecentTreatmentCount(event.treatment.clientId)
    }

    @Subscribe open fun onTreatmentChangedEvent(event: TreatmentChangedEvent) {
        recalcRecentTreatmentCount(event.treatment.clientId)
    }

    @Subscribe open fun onTreatmentDeletedEvent(event: TreatmentDeletedEvent) {
        view.masterView.treatmentCountDecrease(event.treatment.clientId)
        recalcRecentTreatmentCount(event.treatment.clientId)
    }

    @Subscribe open fun onAppointmentSavedEvent(event: AppointmentSavedEvent) {
        recalcUpcomingAppointmentForExtendedClient(event.appointment.clientId)
    }

    @Subscribe open fun onAppointmentDeletedEvent(event: AppointmentDeletedEvent) {
        recalcUpcomingAppointmentForExtendedClient(event.appointment.clientId)
    }

    @Subscribe open fun onAppointmentChangedEvent(event: AppointmentChangedEvent) {
        recalcUpcomingAppointmentForExtendedClient(event.appointment.clientId)
    }

    @Subscribe open fun onClientChangeDonation(event: ClientChangeDonation) {
        saveClient(view.detailView.readClient().copy(donation = event.newDonation))
    }

    @Subscribe open fun onClientChangeCategory(event: ClientChangeCategory) {
        saveClient(view.detailView.readClient().copy(category = event.newCategory))
    }

    private fun doDeleteClient(client: Client) {
        dialogs.confirmedDelete("den Klienten '${client.fullName}'", {
            clientService.delete(client)
            // MINOR should we set currentClient to null?!
            if (client.id!! == currentClient.data.id) {
                bus.post(ClientUnselectedEvent(client))
            }
        })
    }

    private fun extendClient(client: Client): ExtendedClient {
        return ExtendedClient(client, treatmentRepo.countAllFor(client), appointmentService.upcomingAppointmentFor(client)?.start, calcDifferenceDaysToRecentTreatment(client.id!!))
    }

    private fun calcDifferenceDaysToRecentTreatment(clientId: String): Int? {
        return treatmentRepo.findLastFor(clientId)?.date?.differenceDaysTo(clock.now())
    }

    private fun recalcUpcomingAppointmentForExtendedClient(clientId: String) {
        view.masterView.changeUpcomingAppointment(clientId, appointmentService.upcomingAppointmentFor(clientId)?.start)
    }

    private fun recalcRecentTreatmentCount(clientId: String) {
        view.masterView.changeDifferenceDaysToRecentTreatment(clientId, calcDifferenceDaysToRecentTreatment(clientId))
    }

    private fun saveClient(client: Client) {
        log.trace("saveClient(client={})", client)

        if (client.firstName.isEmpty() && client.lastName.isEmpty()) {
            dialogs.show(
                    title = Labels.ClientSaveDialog.title,
                    message = "Es muss zumindest entweder ein Vorname oder ein Nachname eingegeben werden.",
                    buttonLabels = arrayOf(Labels.ClientSaveDialog.button),
                    type = DialogType.WARN
            )
            return
        }

        try {
            clientService.insertOrUpdate(client)
        } catch(e: InvalidMailException) {
            dialogs.show(
                    title = "Speichern fehlgeschlagen",
                    message = "Die angegebene Email Adresse ist ungültig: ${client.contact.mail}",
                    type = DialogType.WARN
            )
        }
    }

    fun checkChanges(): ChangeBehaviour {
        return changesChecker.checkChanges()
    }
}
