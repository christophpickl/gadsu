package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
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
    private lateinit var repo: ClientPropsRepository
    private lateinit var testee: PropsService
    private lateinit var helper: TestPropHelper

    private val testProp1 = Pair(Props.Enums.SleepEnum.key, MultiEnumProp(listOf(
            SleepEnum.ProblemsFallAsleep.key,
            SleepEnum.TiredInTheMorning.key
        )))
    private val testProps1Map = mapOf(testProp1)
    private val testProps1 = ClientProps(testProps1Map)

    @BeforeMethod
    fun initState() {
        client = insertClientViaRepo(Client.unsavedValidInstance())
        repo = XPropsJdbcRepository(jdbcx)
        testee = PropsServiceImpl(repo)
        helper = TestPropHelper(jdbcx)
    }

    fun `update, multi enum property, sunshine`() {
        updateDefaultEnumProps()
        helper.assertRows(PropSqlRow(client.id!!, Props.Enums.SleepEnum.key, "ProblemsFallAsleep,TiredInTheMorning"))
    }

    @Test(dependsOnMethods = arrayOf("update, multi enum property, sunshine"))
    fun `load, multi enum property, sunshine`() {
        updateDefaultEnumProps()

        val actual = testee.load(client)
        assertThat(actual.properties.size, equalTo(1))
        val entry = actual.properties.iterator().next()
        assertThat(entry.key, equalTo(testProp1.first))
        assertThat(entry.value, equalTo(testProp1.second as Prop))
    }

    private fun updateDefaultEnumProps(props: ClientProps = testProps1) {
        //testee.update(client.copy(props = props))

    }

}


//@Test(
//        groups = arrayOf("hsqldb", "integration")
//)
/*
class ClientServiceImplForPropsTest : HsqldbTest() {

    // MINOR TEST this needs some refactoring (reuse it!), or just startup guice context and inject faked datasource
    private var clientRepo: ClientRepository = ClientSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var propsRepo: ClientPropsRepository = ClientPropsSpringJdbcRepository(nullJdbcx())
    private propsSerice
    private var treatmentRepo: TreatmentRepository = TreatmentSpringJdbcRepository(nullJdbcx(), idGenerator)
    private var treatmentService: TreatmentService = TreatmentServiceImpl(treatmentRepo, nullJdbcx(), bus, clock)
    private var testee: ClientService = ClientServiceImpl(clientRepo, propsRepo, treatmentService, nullJdbcx(), bus, clock, currentClient)

    private var helper = TestPropHelper(nullJdbcx())

    private var client = Client.unsavedValidInstance()

    @BeforeMethod
    fun initState() {
        idGenerator = SequencedTestableIdGenerator()
        clientRepo = ClientSpringJdbcRepository(jdbcx(), idGenerator)
        propsRepo = ClientPropsSpringJdbcRepository(jdbcx())
        treatmentRepo = TreatmentSpringJdbcRepository(jdbcx(), idGenerator)
        treatmentService = TreatmentServiceImpl(treatmentRepo, jdbcx(), bus, clock)
        testee = ClientServiceImpl(clientRepo, propsRepo, treatmentService, jdbcx(), bus, clock, currentClient)
        helper = TestPropHelper(jdbcx())
    }

    fun `insert client with enum prop set, should insert one sql row in table`() {
        val givenProps = ClientProps(mapOf(
                Pair(Props.Enums.SleepEnum.key, MultiEnumProp(listOf(Props.Enums.SleepEnum.ProblemsFallAsleep.key)))
        ))
//        ==> addProp(Props.Sleep.key).withValue(Props.Sleep.ProblemsFallAsleep, ...)
        val insertClient = client.copy(props = givenProps)
        testee.insertOrUpdate(insertClient)
        helper.assertRows(PropSqlRow(insertClient.id!!, Props.Enums.SleepEnum.key, Props.Enums.SleepEnum.ProblemsFallAsleep.key))
    }

}
*/
