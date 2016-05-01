package at.cpickl.gadsu.client.xprops

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.client.xprops.SProp
import at.cpickl.gadsu.client.xprops.XPropsSqlJdbcRepository
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("hsqldb"))
class XPropsSqlJdbcRepositoryTest : HsqldbTest() {

    private val table = XPropsSqlJdbcRepository.Companion.TABLE
    private val mapper = XPropsSqlJdbcRepository.ROW_MAPPER
    private val testProp = SProp("testKey", "testVal")

    private lateinit var testee: XPropsSqlJdbcRepository
    private lateinit var client: Client


    @BeforeMethod
    fun initState() {
        testee = XPropsSqlJdbcRepository(jdbcx)
        // default a client is inserted for all tests
        client = insertClientViaRepo(Client.unsavedValidInstance())
    }

    fun `insert, single prop inserted`() {
        testee.insert(client, listOf(testProp))
        assertRows(table, mapper, testProp)

    }

    fun `select, single prop inserted, returns 1`() {
        testee.insert(client, listOf(testProp))
        assertThat(testee.select(client), contains(testProp))
    }

    fun `delete, single prop inserted`() {
        testee.insert(client, listOf(testProp))
        testee.delete(client)
        assertEmptyRows(table, mapper)
    }

}
/*

@Test(groups = arrayOf("hsqldb"))
class ClientPropsSpringJdbcRepositoryTest : HsqldbTest() {

    private lateinit var insertedClient: Client
    private lateinit var testee: ClientPropsRepository
    private lateinit var helper: TestPropHelper



    @BeforeMethod
    fun initState() {
        testee = XPropsJdbcRepository(jdbcx)
        // default a client is inserted for all tests
        insertedClient = insertClientViaRepo(Client.unsavedValidInstance())
        helper = TestPropHelper(jdbcx)
    }

    fun `reset, string type`() {
        reset(sqlPropString1)
        helper.assertRows(PropSqlRow(insertedClient.id!!, sqlPropString1.first, sqlPropString1.second.toSqlValue()))
    }

    fun `reset, multi-enum type`() {
        reset(sqlPropMultiEnum1)
        helper.assertRows(PropSqlRow(insertedClient.id!!, sqlPropMultiEnum1.first, sqlPropMultiEnum1.second.toSqlValue()))
    }

    fun `read, string sunshine`() {
        reset(sqlPropString1)
        helper.assertPropsEqual(testee.readAllFor(insertedClient), sqlPropString1)
    }

    fun `read, multi-enum sunshine`() {
        reset(sqlPropMultiEnum1)
        helper.assertPropsEqual(testee.readAllFor(insertedClient), sqlPropMultiEnum1)
    }

//    private fun assertResetAndRead() {
//
//    }

    private fun reset(vararg entries: Pair<String, SqlPropType>) {
        val data = HashMap<String, SqlPropType>()
        entries.forEach { data.put(it.first, it.second) }
        testee.reset(insertedClient.id!!, SqlProps(data))
    }

}

class TestPropHelper(val jdbc: Jdbcx) {
    companion object {
        val sqlPropString1 = Pair(Props.Strings.MoodOfToday.key, SqlPropStringType("my mood of today"))
        val sqlPropMultiEnum1 = Pair(Props.Enums.SleepEnum.key, SqlPropMultiEnumType(listOf(
                Props.Enums.SleepEnum.ProblemsFallAsleep.key,
                Props.Enums.SleepEnum.TiredInTheMorning.key)))
    }

    fun assertPropsEqual(actual: SqlProps, vararg pairs: Pair<String, SqlPropType>) {
        assertThat(actual, equalTo(SqlProps(mapOf(*pairs))))
    }

    fun assertRows(vararg expected: PropSqlRow) {
        val rawRows = jdbc.jdbc.query("SELECT * FROM ${XPropsJdbcRepository.TABLE}", PropSqlRow.ROW_MAPPER)
        if (expected.isEmpty()) {
            assertThat(rawRows, emptyIterable())
        } else {
            assertThat(rawRows, hasSize(expected.size))
            assertThat(rawRows, contains(*expected))
        }
    }
}


 */
