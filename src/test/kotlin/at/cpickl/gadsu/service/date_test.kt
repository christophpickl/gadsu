package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.annotations.Test

@Test
class DateFormatsTest {

    private val date = DateTime.parse("2016-04-20T19:21:13.123")

    fun `formatDateTimeLong`() {
        assertThat(date.formatDateTimeLong(), equalTo("Mittwoch, 20.04.16, 19:21 Uhr"))
    }

}