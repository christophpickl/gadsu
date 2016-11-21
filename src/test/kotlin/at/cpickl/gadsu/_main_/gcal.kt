package at.cpickl.gadsu._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.sync.GCalSyncerImpl
import at.cpickl.gadsu.appointment.gcal.InternetConnectionAwareGCalService
import at.cpickl.gadsu.appointment.gcal.sync.MatchClientsInDb
import at.cpickl.gadsu.appointment.gcal.RealGCalRepository
import at.cpickl.gadsu.appointment.gcal.transformCalendarNameToId
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.GoogleConnectorImpl
import com.google.common.eventbus.EventBus
import org.mockito.Mockito


fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    val connector = GoogleConnectorImpl()
    val calendar = connector.connectCalendar()
    val calendarId = transformCalendarNameToId(calendar, "gadsu_test")
    val gcal = RealGCalRepository(calendar, calendarId)

//    gcal.listEvents(
//            start = "21.11.2016".parseDate(),
//            end = "25.11.2016".parseDate()
//    ).forEach(::println)

    println("done")
}

//private class GCalPlayground {
//    fun listEventsAndSaveToFile() {
//        val now = DateTime.now()
//        val events = gcal.listEvents(now.minusDays(30), now.plusDays(30))
//        //            .forEach {
//        //                val startDate = it.start.dateTime ?: it.start.date
//        //                println("${it.summary} - $startDate")
//        //            }
//        XStream().toXML(events).saveToFile(File("gcal_events.xml").apply { println("Saving ${events.size} events to: $absolutePath") })
//    }
//
//    fun createNewEvent() {
//        gcal.createEvent(calendarId, "die steffi ist da", "das wird sicher sehr toll", DateTime.now(), 120)
//    }
//
//    fun updateEvent(eventId: String) {
//        gcal.updateEvent(calendarId, eventId, "new suuuumary")
//    }
//
//    fun deleteEvent(eventId: String) {
//        gcal.deleteEvent(calendarId, eventId)
//    }
//}
