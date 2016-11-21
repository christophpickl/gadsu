package at.cpickl.gadsu.appointment.gcal

import com.google.api.services.calendar.model.Event
import com.thoughtworks.xstream.XStream
import org.testng.annotations.Test


@Test
class GCalTest {

    @Suppress("UNCHECKED_CAST")
    private val events = XStream().fromXML(javaClass.getResourceAsStream("/gadsu_test/gcal_events.xml")) as List<Event>

    fun foobar() {
        events
                    .forEach {
                        println("Summary: ${it.summary}\n" +
                                "Start: ${it.start.dateTime ?: it.start.date}\n" +
                                "End: ${it.end.dateTime ?: it.end.date}\n" +
                                "Updated: ${it.updated}\n" +
                                "Description: ${it.description}\n" +
                                "Location: ${it.location}\n" +
                                "ID: ${it.id}\n" +
                                "HTML Link: ${it.htmlLink}\n" +
                                "Reminders: ${it.reminders}\n" +
                                "MYappointmentId: ${it["MYappointmentId"]}\n" +
                                "unknownKeys: ${it.unknownKeys}\n"
                        )
                    }
    }

}

