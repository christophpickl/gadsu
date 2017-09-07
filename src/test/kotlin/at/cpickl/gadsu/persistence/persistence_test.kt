package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.testinfra.newTestDataSource
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import gadsu.persistence.V6_1__xprop_update.Companion.hungryDigestParts
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.springframework.jdbc.core.RowMapper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.sql.ResultSet
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

private data class XpropsDboV6(val idClient: String, val key: String, val rawVal: String, val note: String?)

private object XpropsDboV6Mapper : RowMapper<XpropsDboV6> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            XpropsDboV6(rs.getString("id_client"), rs.getString("key"), rs.getString("val"), rs.getString("note"))
}

/** Got no 'note' field yet. */
private data class SpropDboV5(val idClient: String, val key: String, val rawVal: String)

@Test class FlywayMigrateTest {

    private lateinit var dataSource: DataSource
    private lateinit var jdbcx: Jdbcx
    private lateinit var flyway: Flyway

    private val clientId = "clientId"
    private val client = Client.unsavedValidInstance().copy(id = clientId)
    private var testCounter = AtomicInteger()

    @BeforeMethod fun `setup db stuff`() {
        dataSource = newTestDataSource(javaClass.simpleName + testCounter.incrementAndGet())
        jdbcx = SpringJdbcx(dataSource)
        flyway = FlywayDatabaseManager(dataSource).buildFlyway()

        _migrate("5", 5)
        insertClient_V5(client)
    }


    fun `given taste xprop exists, when migrate, should be translated to hungry`() {
        insertXProp_V5(SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    fun `given taste and hungry xprop exists, when migrate, taste and hungry should be merged`() {
        insertXProp_V5(SpropDboV5(clientId, "Hungry", "Hungry_BigHunger"))
        insertXProp_V5(SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_BigHunger,Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    private val hungryNonDigestParts = listOf("BigHunger", "LittleHunger")
    private val hungryAllParts = hungryNonDigestParts.plus(hungryDigestParts)

    fun `given hungry exists, when migrate, move new digestion parts`() {
        insertXProp_V5(SpropDboV5(clientId, "Hungry", hungryAllParts.map { "Hungry_$it" }.joinToString(",")))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(
                XpropsDboV6(clientId, "Digestion", hungryDigestParts.map { "Digestion_$it" }.joinToString(","), null),
                XpropsDboV6(clientId, "Hungry", hungryNonDigestParts.map { "Hungry_$it" }.joinToString(","), null)
        )
    }


    private fun assertXPropsTableContent_V6(vararg expected: XpropsDboV6) {
        assertThat(jdbcx.query("SELECT * FROM xprops", XpropsDboV6Mapper), equalTo(expected.toList()))
    }

    private fun migrateToDbVersion6() {
        _migrate("6.1", 2)
    }

    private fun _migrate(toVersion: String = "-1", expectedMigrations: Int? = null) {
        flyway.target = MigrationVersion.fromVersion(toVersion)

        val executedMigrations = flyway.migrate()
        if (expectedMigrations != null) {
            assertThat(executedMigrations, equalTo(expectedMigrations))
        }
    }

    // with V5 compared to V6 got no field: note
    private fun insertXProp_V5(vararg sprops: SpropDboV5) {
        sprops.forEach {
            jdbcx.update("INSERT INTO xprops (id_client, key, val) VALUES (?, ?, ?)",
                    clientId, it.key, it.rawVal)
        }
    }

    // with V5 compared to V6 got no field: mainObject, ... syndrom
    private fun insertClient_V5(client: Client) {
        val sqlInsert = """
        INSERT INTO ${ClientJdbcRepository.TABLE} (
            id, created, firstName, lastName, nickName,
            mail, phone, street, zipCode, city,
            wantReceiveDoodleMails, birthday, gender_enum, countryOfOrigin, origin,
            relationship_enum, job, children, hobbies, note,
            textImpression, textMedical, textComplaints, textPersonal, textObjective,
            tcmNote
        ) VALUES (
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?
        )"""
        jdbcx.update(sqlInsert,
                client.id, client.created.toSqlTimestamp(), client.firstName, client.lastName, client.nickNameInt,
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city,
                client.wantReceiveMails, client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.tcmNote
        )
    }

}
