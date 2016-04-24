package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.SQL_TIMESTAMP
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.testinfra.TEST_DATETIME1
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class DateFormatsPersistenceTest {

    fun formatTimestamp() {
        assertThat(DateFormats.SQL_TIMESTAMP.print(TEST_DATETIME1), equalTo("TIMESTAMP '2000-01-01 00:10:20'"))
    }

}
