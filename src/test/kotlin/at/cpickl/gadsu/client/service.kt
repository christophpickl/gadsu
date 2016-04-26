package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.props.ClientPropsRepository
import at.cpickl.gadsu.client.props.ClientPropsSpringJdbcRepository
import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
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

    private var clientRepo = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var propsRepo: ClientPropsRepository = ClientPropsSpringJdbcRepository(nullJdbcx())
    private var treatmentRepo = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentService = TreatmentServiceImpl(treatmentRepo, nullJdbcx(), EventBus(), RealClock())

    @BeforeMethod
    fun setUp() {
        idGenerator = SequencedTestableIdGenerator()
        clientRepo = ClientSpringJdbcRepository(jdbcx(), idGenerator)
        propsRepo = ClientPropsSpringJdbcRepository(jdbcx())
        treatmentRepo = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentService = TreatmentServiceImpl(treatmentRepo, jdbcx(), bus, clock)
    }

    fun deleteClientWithSomeTreatments_willSucceedAsInternallyDeletesAllTreatmentsFirst() {
        val savedClient = clientRepo.insertWithoutPicture(unsavedClient)
        treatmentRepo.insert(Treatment.unsavedValidInstance(savedClient.id!!))

        testee().delete(savedClient)

        assertEmptyTable("client")
        assertEmptyTable("treatment")
    }

    private fun testee() = ClientServiceImpl(clientRepo, propsRepo, treatmentService, jdbcx(), bus, clock, currentClient)

}
