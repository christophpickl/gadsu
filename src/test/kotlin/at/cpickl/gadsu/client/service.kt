package at.cpickl.gadsu.client

import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentServiceImpl
import at.cpickl.gadsu.treatment.TreatmentSpringJdbcRepository
import at.cpickl.gadsu.treatment.unsavedValidInstance
import com.google.common.eventbus.EventBus
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb", "integration"))
class ClientServiceImplIntegrationTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    // TODO use guice support for integration tests instead

    private var bus = EventBus()
    private var clock = SimpleTestableClock()
    private var clientRepo = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentRepo = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentService = TreatmentServiceImpl(treatmentRepo, nullJdbcx(), EventBus(), RealClock())

    @BeforeMethod
    fun setUp() {
        bus = EventBus()
        clock = SimpleTestableClock(TEST_DATE)
        idGenerator = SequencedTestableIdGenerator()
        clientRepo = ClientSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentRepo = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentService = TreatmentServiceImpl(treatmentRepo, jdbcx(), bus, clock)
    }

    fun deleteClientWithSomeTreatments_willSucceedAsInternallyDeletesAllTreatmentsFirst() {

        val savedClient = clientRepo.insert(unsavedClient)
        treatmentRepo.insert(Treatment.unsavedValidInstance(savedClient.id!!))

        testee().delete(savedClient)

        assertEmptyTable("client")
        assertEmptyTable("treatment")
    }

    private fun testee() = ClientServiceImpl(clientRepo, treatmentService, jdbcx(), bus)

}
