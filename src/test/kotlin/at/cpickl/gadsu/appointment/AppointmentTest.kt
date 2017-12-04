package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.testinfra.savedValidInstance
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.testng.annotations.Test

@Test
class AppointmentTest {

    private val anyClientId = "anyClientId"

    fun `compareTo - with nulls`() {
        val appIdNull = anyAppointment().copy(id = null)
        val appIdSet = anyAppointment().copy(id = "testId")

        assertThat(appIdNull.compareTo(appIdSet), equalTo(-1))
        assertThat(appIdSet.compareTo(appIdNull), equalTo(1))
    }

    private fun anyAppointment() = Appointment.savedValidInstance(anyClientId)

}
