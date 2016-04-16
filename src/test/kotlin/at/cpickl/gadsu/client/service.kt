package at.cpickl.gadsu.client

import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentSpringJdbcRepository
import at.cpickl.gadsu.treatment.unsavedValidInstance
import com.google.common.eventbus.EventBus
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb", "integration"))
class ClientServiceImplIntegrationTest : HsqldbTest() {

    private val unsavedClient = Client.unsavedValidInstance()
    // TODO use guice support for integration tests instead

    private var clientRepo = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentRepo = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var testee: ClientServiceImpl? = null


    override fun resetTables() = arrayOf("treatment", "client")

    @BeforeMethod
    fun setUp() {
        idGenerator = mock(IdGenerator::class.java)
        clientRepo = ClientSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentRepo = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)
        testee = ClientServiceImpl(clientRepo, treatmentRepo, jdbcx(), EventBus())
    }

    fun deleteClientWithSomeTreatments_willSucceedAsInternallyDeletesAllTreatmentsFirst() {
        `when`(idGenerator.generate()).thenReturn("1").thenReturn("2")

        val savedClient = clientRepo.insert(unsavedClient)
        treatmentRepo.insert(Treatment.unsavedValidInstance(savedClient.id!!), savedClient)

        testee!!.delete(savedClient)

        assertEmptyTable("client")
        assertEmptyTable("treatment")
    }

}
