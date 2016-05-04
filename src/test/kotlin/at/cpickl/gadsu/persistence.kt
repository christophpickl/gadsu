package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.SQL_TIMESTAMP
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.parseDateTime
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class DateFormatsPersistenceTest {

    fun formatTimestamp() {
        assertThat(DateFormats.SQL_TIMESTAMP.print("01.01.2000 11:22:33".parseDateTime()), equalTo("TIMESTAMP '2000-01-01 11:22:33'"))
    }

}
