package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.parseDateTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class GCalDateExtensionTest {

    fun `convert to GEventDateTime and back returns itself again`() {
        val date = "01.01.2000 12:23:24".parseDateTime()

        assertThat(date.toGEventDateTime().toDateTime(), equalTo(date))
    }

//        date.toGDateTime()

}
