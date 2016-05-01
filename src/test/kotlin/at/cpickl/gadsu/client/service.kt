package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.xprops.XPropsService
import at.cpickl.gadsu.client.xprops.XPropsServiceImpl
import at.cpickl.gadsu.client.xprops.persistence.XPropsSqlJdbcRepository
import at.cpickl.gadsu.client.xprops.persistence.XPropsSqlRepository
import at.cpickl.gadsu.testinfra.HsqldbTest
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
    private lateinit var propsRepo: XPropsSqlRepository
    private lateinit var propsService: XPropsService
    private lateinit var treatmentRepo: TreatmentRepository
    private lateinit var treatmentService: TreatmentService

    @BeforeMethod
    fun setUp() {
        clientRepo = ClientJdbcRepository(jdbcx, idGenerator)
        propsRepo = XPropsSqlJdbcRepository(jdbcx)
        propsService = XPropsServiceImpl(propsRepo)
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
