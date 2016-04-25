package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.HashMap

@Test(groups = arrayOf("hsqldb"))
class ClientPropsSpringJdbcRepositoryTest : HsqldbTest() {

    private var testee: ClientPropsRepository = ClientPropsSpringJdbcRepository(nullJdbcx())
    private var client = Client.unsavedValidInstance()

    private val propString1 = Pair(StringProps.MoodOfToday.key, PropStringType("my mood of today"))
    private val propMultiEnum1 = Pair(SleepEnum.key, PropMultiEnumType(listOf(
            SleepEnum.ProblemsFallAsleep.sqlRepresentation,
            SleepEnum.TiredInTheMorning.sqlRepresentation)))


    @BeforeMethod
    fun initState() {
        testee = ClientPropsSpringJdbcRepository(jdbcx())

        // default a client is inserted for all tests
        client = insertClient(Client.unsavedValidInstance())
    }

    fun `reset, string type`() {
        reset(propString1)
        assertRows(PropRawRow(client.id!!, propString1.first, propString1.second.toSqlValue()))
    }

    fun `reset, multi-enum type`() {
        reset(propMultiEnum1)
        assertRows(PropRawRow(client.id!!, propMultiEnum1.first, propMultiEnum1.second.toSqlValue()))
    }

    fun `read, string sunshine`() {
        reset(propString1)
        assertPropsEqual(testee.readAllFor(client), propString1)
    }

    fun `read, multi-enum sunshine`() {
        reset(propMultiEnum1)
        assertPropsEqual(testee.readAllFor(client), propMultiEnum1)
    }

//    private fun assertResetAndRead() {
//
//    }

    private fun assertPropsEqual(actual: Props, vararg pairs: Pair<String, PropType>) {
        assertThat(actual, equalTo(Props(mapOf(*pairs))))
    }

    private fun reset(vararg entries: Pair<String, PropType>) {
        val data = HashMap<String, PropType>()
        entries.forEach { data.put(it.first, it.second) }
        testee.reset(client.id!!, Props(data))
    }


    private fun assertRows(vararg expected: PropRawRow) {
        val rawRows = jdbcx().jdbc.query("SELECT * FROM ${ClientPropsSpringJdbcRepository.TABLE}", PropRawRow.ROW_MAPPER)
        if (expected.isEmpty()) {
            assertThat(rawRows, emptyIterable())
        } else {
            assertThat(rawRows, contains(*expected))
        }
    }

}

