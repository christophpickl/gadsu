package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.appointments.AppointmentJdbcRepository
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.testng.annotations.Test

@Test class AppointmentJdbcRepositoryTest : HsqldbTest() {

    fun `insert sunshine`() {
        val testee = AppointmentJdbcRepository(jdbcx, idGenerator)

    }

}
