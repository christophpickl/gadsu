package at.cpickl.gadsu.client.xprops

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb"))
class XPropsSqlJdbcRepositoryTest : HsqldbTest() {

    private lateinit var testee: XPropsSqlJdbcRepository
    private lateinit var client: Client

    private val testProp = XPropSql("testKey", "testVal")

    @BeforeMethod
    fun initState() {
        testee = XPropsSqlJdbcRepository(jdbcx)
        // default a client is inserted for all tests
        client = insertClientViaRepo(Client.unsavedValidInstance())
    }

    fun `reset, single prop inserted`() {
        testee.reset(client.id!!, XPropsSql(listOf(testProp)))
        assertRows(XPropsSqlJdbcRepository.TABLE, XPropsSqlJdbcRepository.ROW_MAPPER, testProp)

    }

    fun `readAll, single prop inserted, returns 1`() {
        testee.reset(client.id!!, XPropsSql(listOf(testProp)))

        assertThat(testee.readAllFor(client.id!!).items, contains(testProp))
    }

}
