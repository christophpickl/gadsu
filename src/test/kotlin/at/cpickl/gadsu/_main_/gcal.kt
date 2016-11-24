package at.cpickl.gadsu._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.RealGCalRepository
import at.cpickl.gadsu.appointment.gcal.transformCalendarNameToId
import at.cpickl.gadsu.service.GoogleConnectorImpl
import at.cpickl.gadsu.testinfra.readGapiCredentialsFromSysProps
import org.joda.time.DateTime


fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    val connector = GoogleConnectorImpl()
    val credentials = readGapiCredentialsFromSysProps()
    val calendar = connector.connectCalendar(credentials)
    val calendarId = transformCalendarNameToId(calendar, "gadsu_test")
    val gcal = RealGCalRepository(calendar, calendarId)

    gcal.createEvent(GCalEvent(
            null,
            "gadsuIddd",
            "my sum",
            "",
            DateTime.now(),
            DateTime.now().plusMinutes(30),
            null
    ))

    gcal.listEvents(
            start = DateTime.now().minusDays(14),
            end = DateTime.now().plusDays(14)
    ).forEach(::println)

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
