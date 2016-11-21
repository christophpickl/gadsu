package at.cpickl.gadsu.treatment.dyn.treats

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.Test


@Test class PulsePropertyTest {

    fun `check all capital case`() {
        PulseProperty.values().forEach {
            assertThat(it.sqlCode, equalTo(it.sqlCode.toUpperCase()))
        }
    }

    fun `check for duplicate SQL codes`() {
        val checked = mutableListOf<String>()
        PulseProperty.values().forEach {
            assertThat("Duplicate entry found: '${it.sqlCode}'!", checked, not(hasItem(it.sqlCode)))
            checked.add(it.sqlCode)
        }
    }

}
