package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.newTestDataSource
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import gadsu.persistence.V6_1__xprop_update.Companion.hungryDigestParts
import org.flywaydb.core.Flyway
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource


@Test
class FlywayMigrateTest {

    private lateinit var dataSource: DataSource
    private lateinit var jdbcx: Jdbcx
    private lateinit var testService: MigrateTestService
    private lateinit var flyway: Flyway

    private val clientId = "clientId"
    private val client = Client.unsavedValidInstance().copy(id = clientId)
    private var testCounter = AtomicInteger()

    @BeforeMethod
    fun `setup db stuff`() {
        dataSource = newTestDataSource(javaClass.simpleName + testCounter.incrementAndGet())
        jdbcx = SpringJdbcx(dataSource)
        flyway = FlywayDatabaseManager(dataSource).buildFlyway()
        testService = MigrateTestService(jdbcx, flyway)
    }

    fun `given taste xprop exists, when migrate, should be translated to hungry`() {
        testService.migrateDb("5", 5)
        testService.insertClient_V5(client)
        testService.insertXProp_V5(clientId, SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        testService.migrateDb("6.1", 2)

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    fun `given taste and hungry xprop exists, when migrate, taste and hungry should be merged`() {
        testService.migrateDb("5", 5)
        testService.insertClient_V5(client)
        testService.insertXProp_V5(clientId, SpropDboV5(clientId, "Hungry", "Hungry_BigHunger"))
        testService.insertXProp_V5(clientId, SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        testService.migrateDb("6.1", 2)

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_BigHunger,Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    private val hungryNonDigestParts = listOf("BigHunger", "LittleHunger")
    private val hungryAllParts = hungryNonDigestParts.plus(hungryDigestParts)

    fun `given hungry exists, when migrate, move new digestion parts`() {
        testService.migrateDb("5", 5)
        testService.insertClient_V5(client)
        testService.insertXProp_V5(clientId, SpropDboV5(clientId, "Hungry", hungryAllParts.map { "Hungry_$it" }.joinToString(",")))

        testService.migrateDb("6.1", 2)

        assertXPropsTableContent_V6(
                XpropsDboV6(clientId, "Digestion", hungryDigestParts.map { "Digestion_$it" }.joinToString(","), null),
                XpropsDboV6(clientId, "Hungry", hungryNonDigestParts.map { "Hungry_$it" }.joinToString(","), null)
        )
    }

    fun `upgrade to V9 adds new client fields`() {
        testService.migrateDb("8")
        val nickNameV8 = "testNickNameInt"
        testService.insertClient_V8(Client.REAL_DUMMY.copy(
                id = "id",
                nickNameInt = nickNameV8,
                nickNameExt = "IGNORED IN SQL"
        ))

        testService.migrateDb("9")

        assertThat(jdbcx.query("SELECT * FROM client", ClientV9Mapper), equalTo(listOf(ClientV9(
                nicknameInt = nickNameV8, // original nickname is altered to nickNameInt
                nicknameExt = "",
                knownBy = "",
                yyTendency = "?",
                elementTendency = "?"
        ))))
    }

    fun `upgrade to V10 changes treatment dates from zero to zero`() {
        assertV10TreatmentDateMigration(dateWithTime("14:00"), dateWithTime("14:00"))
    }

    fun `upgrade to V10 changes treatment dates from one to zero`() {
        assertV10TreatmentDateMigration(dateWithTime("14:01"), dateWithTime("14:00"))
    }

    fun `upgrade to V10 changes treatment dates from quarter to zero`() {
        assertV10TreatmentDateMigration(dateWithTime("14:15"), dateWithTime("14:00"))
    }

    fun `upgrade to V10 changes treatment dates from three quarter to half`() {
        assertV10TreatmentDateMigration(dateWithTime("14:45"), dateWithTime("14:30"))
    }

    private fun assertXPropsTableContent_V6(vararg expected: XpropsDboV6) {
        MatcherAssert.assertThat(jdbcx.query("SELECT * FROM xprops", XpropsDboV6Mapper), Matchers.equalTo(expected.toList()))
    }

    private fun assertV10TreatmentDateMigration(initDate: DateTime, expectedDate: DateTime) {
        testService.migrateDb("9")
        testService.insertClient_V9(clientId)
        testService.insertTreamtent_V9(initDate, clientId)

        testService.migrateDb("10_1")

        val actual = jdbcx.query("SELECT * FROM treatment", TreatmentV9Mapper)
        assertThat(actual, hasSize(1))
        assertThat(actual[0].date, equalTo(expectedDate))
    }

    private fun dateWithTime(time: String) = "1.1.2000 $time:00".parseDateTime()

}
