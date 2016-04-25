package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientProps
import at.cpickl.gadsu.client.MultiEnumProp
import at.cpickl.gadsu.client.Prop
import at.cpickl.gadsu.client.props.Props.Enums.SleepEnum
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.testinfra.HsqldbTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test(groups = arrayOf("hsqldb", "integration"))
class PropsServiceImplTest : HsqldbTest() {
    private var client = Client.unsavedValidInstance()
    private var repo: ClientPropsRepository = ClientPropsSpringJdbcRepository(nullJdbcx())
    private var testee: PropsService = PropsServiceImpl(repo)
    private var helper: TestPropHelper = TestPropHelper(nullJdbcx())

    private val testProp1 = Pair(Props.Enums.SleepEnum.key, MultiEnumProp(listOf(
            SleepEnum.ProblemsFallAsleep.key,
            SleepEnum.TiredInTheMorning.key
        )))
    private val testProps1Map = mapOf(testProp1)
    private val testProps1 = ClientProps(testProps1Map)

    @BeforeMethod
    fun initState() {
        client = insertClientViaRepo(Client.unsavedValidInstance())
        repo = ClientPropsSpringJdbcRepository(jdbcx())
        testee = PropsServiceImpl(repo)
        helper = TestPropHelper(jdbcx())
    }

    fun `update, multi enum property, sunshine`() {
        updateDefaultEnumProps()
        helper.assertRows(PropSqlRow(client.id!!, Props.Enums.SleepEnum.key, "ProblemsFallAsleep,TiredInTheMorning"))
    }

    @Test(dependsOnMethods = arrayOf("update, multi enum property, sunshine"))
    fun `load, multi enum property, sunshine`() {
        updateDefaultEnumProps()

        val actual = testee.load(client)
        println(actual)
        assertThat(actual.properties.size, equalTo(1))
        val entry = actual.properties.iterator().next()
        assertThat(entry.key, equalTo(testProp1.first))
        assertThat(entry.value, equalTo(testProp1.second as Prop))
    }

    private fun updateDefaultEnumProps(props: ClientProps = testProps1) {
        testee.update(client.copy(props = props))

    }

}
