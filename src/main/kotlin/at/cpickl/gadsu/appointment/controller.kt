package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.appointment.gcal.GCalException
import at.cpickl.gadsu.appointment.view.AppointmentWindow
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.clearMinutes
import at.cpickl.gadsu.service.formatDateTimeTalkative
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

interface AppointmentController {

}

@Logged
open class AppointmentControllerImpl @Inject constructor(
        private val window: AppointmentWindow,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val service: AppointmentService,
        private val dialogs: Dialogs
) : AppointmentController {

    @Subscribe open fun onCreateAppointmentEvent(event: CreateAppointmentEvent) {
        showAppointment(Appointment.insertPrototype(currentClient.data.id!!, clock.now().plusDays(1).withHourOfDay(12).clearMinutes()))
    }

    @Subscribe open fun onSaveAppointment(event: SaveAppointment) {
        try {
            service.insertOrUpdate(event.appointment)
            window.hideWindow()
        } catch (e: GCalException) {
            dialogs.show(
                    title = "GCal Verbindungsfehler",
                    message = "Die Kommunikation mit GCal ist fehlgeschlagen. " +
                            "Probier doch mal den datastore zu löschen. (Status Code: ${e.statusCode})",
                    type = DialogType.ERROR
            )
        }
    }

    @Subscribe open fun onAbortAppointmentDialog(event: AbortAppointmentDialogEvent) {
        window.hideWindow()
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        window.close()
    }

    @Subscribe open fun onOpenAppointmentEvent(event: OpenAppointmentEvent) {
        showAppointment(event.appointment)
    }

    @Subscribe open fun onDeleteAppointmentEvent(event: DeleteAppointmentEvent) {
        dialogs.confirmedDelete("den Termin am ${event.appointment.start.formatDateTimeTalkative()}", {
            service.delete(event.appointment)
            if (window.isShowing(event.appointment)) {
                window.hideWindow()
            }
        })
    }

    private fun showAppointment(appointment: Appointment) {
        currentClient.data.ensurePersisted()
        window.changeCurrent(appointment)
        window.showWindow()
    }

}
