package at.cpickl.gadsu

import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.testinfra.TEST_DATE
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class DateFormatsPersistenceTest {

    fun formatTimestamp() {
        assertThat(DateFormats.SQL_TIMESTAMP.print(TEST_DATE), equalTo("TIMESTAMP '2001-12-01 12:59:59'"))
    }

}
