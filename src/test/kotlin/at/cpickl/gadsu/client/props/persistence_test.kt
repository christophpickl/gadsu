package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.props.TestPropHelper.Companion.sqlPropMultiEnum1
import at.cpickl.gadsu.client.props.TestPropHelper.Companion.sqlPropString1
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.HashMap

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

