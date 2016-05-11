package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.service.saveToFile
import com.google.api.services.calendar.model.Event
import com.thoughtworks.xstream.XStream
import org.joda.time.DateTime
import org.testng.annotations.Test
import java.io.File


fun main(args: Array<String>) {
    // FIXME let user change proxy settings in preferences window

    System.setProperty("http.proxyHost", "proxy-sd.s-mxs.net")
    System.setProperty("http.proxyPort", "8080")
    System.setProperty("https.proxyHost", "proxy-sd.s-mxs.net")
    System.setProperty("https.proxyPort", "8080")

    GCalPlayground()
//        .listEventsAndSaveToFile()
//        .createNewEvent()
//        .deleteEvent("hgeotfcs657bpl08thllithrvk")
        .updateEvent("aukh7uq8fhf61nguibvc3nv9ls")

    println("done")
}

private class GCalPlayground {
    private val gcal = GCalService()
    private val calendarId = gcal.transformCalendarNameToId("gadsu")

    fun listEventsAndSaveToFile() {
        val events = gcal.readFutureEvents(calendarId)
        //            .forEach {
        //                val startDate = it.start.dateTime ?: it.start.date
        //                println("${it.summary} - $startDate")
        //            }
        XStream().toXML(events).saveToFile(File("gcal_events.xml"))
    }

    fun createNewEvent() {
        gcal.createEvent(calendarId, "foobar", "hihi descr", DateTime.now(), 60)
    }

    fun updateEvent(eventId: String) {
        gcal.updateEvent(calendarId, eventId, "new suuuumary")
    }

    fun deleteEvent(eventId: String) {
        gcal.deleteEvent(calendarId, eventId)
    }
}


@Test
class GCalTest {

    private val events = XStream().fromXML(javaClass.getResourceAsStream("/gadsu_test/gcal_events.xml")) as List<Event>

    fun foobar() {
        events
                    .forEach {
                        val startDate = it.start.dateTime ?: it.start.date
                        println("${it.summary} - $startDate")
                    }
    }

}

