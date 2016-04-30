package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.props.ClientPropsRepository
import at.cpickl.gadsu.client.props.PropsService
import at.cpickl.gadsu.client.props.PropsServiceImpl
import at.cpickl.gadsu.client.props.XPropsJdbcRepository
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.TreatmentServiceImpl
import at.cpickl.gadsu.treatment.unsavedValidInstance
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb", "integration"))
class ClientServiceImplIntegrationTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()

    private lateinit var clientRepo: ClientRepository
    private lateinit var propsRepo: ClientPropsRepository
    private lateinit var propsService: PropsService
    private lateinit var treatmentRepo: TreatmentRepository
    private lateinit var treatmentService: TreatmentService

    @BeforeMethod
    fun setUp() {
        clientRepo = ClientJdbcRepository(jdbcx, idGenerator)
        propsRepo = XPropsJdbcRepository(jdbcx)
        propsService = PropsServiceImpl(propsRepo)
        treatmentRepo = TreatmentJdbcRepository(jdbcx, idGenerator)
        treatmentService = TreatmentServiceImpl(treatmentRepo, jdbcx, bus, clock)
    }

    fun deleteClientWithSomeTreatments_willSucceedAsInternallyDeletesAllTreatmentsFirst() {
        val savedClient = clientRepo.insertWithoutPicture(unsavedClient)
        treatmentRepo.insert(Treatment.unsavedValidInstance(savedClient.id!!))

        testee().delete(savedClient)

        assertEmptyTable("client")
        assertEmptyTable("treatment")
    }

    private fun testee() = ClientServiceImpl(clientRepo, propsService, treatmentService, jdbcx, bus, clock, currentClient)

}
