package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.appointment.gcal.sync.MatchClients
import at.cpickl.gadsu.appointment.gcal.sync.MatchClientsInDb
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb")) class MatchClientsInDbTest : HsqldbTest() {

    private lateinit var testee: MatchClients
    private lateinit var caroline: Client
    private lateinit var carolina: Client
    private lateinit var laura: Client
    private lateinit var ingrid: Client

    @BeforeMethod fun insertClients() {
        testee = MatchClientsInDb(ClientJdbcRepository(jdbcx, idGenerator))

        caroline = newClient("Caroline", "Caro", "Firefox")
        carolina = newClient("Carolina", "Caro", "Safari")
        laura = newClient("Laura", "", "Internet Chrome")
        ingrid = newClient("Ingrid", "Haudegen", "Internet Explorer")
    }

    private fun findMatchingClientsProvider(): List<Pair<String, List<Client>>> = listOf(
            // first name
            testCase("caro", caroline, carolina),
            testCase("caroline", caroline, carolina),
            testCase("carolina", carolina, caroline),

            // last name
            testCase("chrome", laura),
            testCase("inter", ingrid, laura),

            // nick name
            testCase("haudeg", ingrid)
    )

    // using a dataprovider does not really work here, as we need a client which is generated at runtime
    fun `findMatchingClients by name _ should return clients _`() {
        findMatchingClientsProvider().forEach {
            assertThat("Search string: '${it.first}'", testee.findMatchingClients(it.first), contains(*it.second.toTypedArray()))
        }
    }

    private fun newClient(firstName: String, nickName: String, lastName: String) =
            insertClientViaRepo(Client.unsavedValidInstance().copy(firstName = firstName, nickNameInt = nickName, lastName = lastName))

    private fun testCase(searchName: String, vararg expectedClients: Client): Pair<String, List<Client>> {
        return Pair(searchName, expectedClients.toList())
    }

}
