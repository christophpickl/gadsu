package at.cpickl.gadsu.appointment

import com.google.api.services.calendar.model.Event
import com.thoughtworks.xstream.XStream
import org.testng.annotations.Test


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

