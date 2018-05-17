package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.service.toMinutes
import at.cpickl.gadsu.testinfra.newTestDataSource
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import gadsu.persistence.V6_1__xprop_update.Companion.hungryDigestParts
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.joda.time.Duration
import org.springframework.jdbc.core.RowMapper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.sql.ResultSet
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource

private data class XpropsDboV6(val idClient: String, val key: String, val rawVal: String, val note: String?)
private data class ClientV9(val nicknameInt: String, val nicknameExt: String, val knownBy: String, val yyTendency: String, val elementTendency: String)

private object XpropsDboV6Mapper : RowMapper<XpropsDboV6> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            XpropsDboV6(rs.getString("id_client"), rs.getString("key"), rs.getString("val"), rs.getString("note"))
}

data class TreatmentV9(
        val date: DateTime
)

private object TreatmentV9Mapper : RowMapper<TreatmentV9> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            TreatmentV9(rs.getDateTime("date"))
}

private object ClientV9Mapper : RowMapper<ClientV9> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            ClientV9(
                    nicknameInt = rs.getString("nicknameInt"),
                    nicknameExt = rs.getString("nicknameExt"),
                    knownBy = rs.getString("knownBy"),
                    yyTendency = rs.getString("yyTendency"),
                    elementTendency = rs.getString("elementTendency")
            )
}

/** Got no 'note' field yet. */
private data class SpropDboV5(val idClient: String, val key: String, val rawVal: String)

@Test
class FlywayMigrateTest {

    private lateinit var dataSource: DataSource
    private lateinit var jdbcx: Jdbcx
    private lateinit var flyway: Flyway

    private val clientId = "clientId"
    private val client = Client.unsavedValidInstance().copy(id = clientId)
    private var testCounter = AtomicInteger()
    private val date_14_00 = "1.2.2001 14:00:00".parseDateTime()
    private val date_14_15 = "1.2.2001 14:15:00".parseDateTime()
    private val date_14_30 = "1.2.2001 14:30:00".parseDateTime()
    private val date_14_45 = "1.2.2001 14:45:00".parseDateTime()

    @BeforeMethod
    fun `setup db stuff`() {
        dataSource = newTestDataSource(javaClass.simpleName + testCounter.incrementAndGet())
        jdbcx = SpringJdbcx(dataSource)
        flyway = FlywayDatabaseManager(dataSource).buildFlyway()
    }

    fun `given taste xprop exists, when migrate, should be translated to hungry`() {
        migrateDb("5", 5)
        insertClient_V5(client)
        insertXProp_V5(SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    fun `given taste and hungry xprop exists, when migrate, taste and hungry should be merged`() {
        migrateDb("5", 5)
        insertClient_V5(client)
        insertXProp_V5(SpropDboV5(clientId, "Hungry", "Hungry_BigHunger"))
        insertXProp_V5(SpropDboV5(clientId, "Taste", "Taste_Sweet,Taste_Hot"))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(XpropsDboV6(clientId, "Hungry", "Hungry_BigHunger,Hungry_TasteSweet,Hungry_TasteHot", null))
    }

    private val hungryNonDigestParts = listOf("BigHunger", "LittleHunger")
    private val hungryAllParts = hungryNonDigestParts.plus(hungryDigestParts)

    fun `given hungry exists, when migrate, move new digestion parts`() {
        migrateDb("5", 5)
        insertClient_V5(client)
        insertXProp_V5(SpropDboV5(clientId, "Hungry", hungryAllParts.map { "Hungry_$it" }.joinToString(",")))

        migrateToDbVersion6()

        assertXPropsTableContent_V6(
                XpropsDboV6(clientId, "Digestion", hungryDigestParts.map { "Digestion_$it" }.joinToString(","), null),
                XpropsDboV6(clientId, "Hungry", hungryNonDigestParts.map { "Hungry_$it" }.joinToString(","), null)
        )
    }

    fun `upgrade to V9 adds new client fields`() {
        migrateDb("8")
        val nickNameV8 = "testNickNameInt"
        insertClient_V8(Client.REAL_DUMMY.copy(
                id = "id",
                nickNameInt = nickNameV8,
                nickNameExt = "IGNORED IN SQL"
        ))

        migrateDb("9")

        assertThat(jdbcx.query("SELECT * FROM client", ClientV9Mapper), equalTo(listOf(ClientV9(
                nicknameInt = nickNameV8, // original nickname is altered to nickNameInt
                nicknameExt = "",
                knownBy = "",
                yyTendency = "?",
                elementTendency = "?"
        ))))
    }

    fun `upgrade to V10 changes treatment dates from quarter to zero`() {
        migrateDb("9")
        insertClient_V9(clientId)
        insertTreamtent_V9(date_14_15, clientId)

        migrateDb("10")

        val actual = jdbcx.query("SELECT * FROM treatment", TreatmentV9Mapper)
        assertThat(actual, hasSize(1))
        assertThat(actual[0].date, equalTo(date_14_00))
    }

    fun `upgrade to V10 changes treatment dates from three quarter to half`() {
        migrateDb("9")
        insertClient_V9(clientId)
        insertTreamtent_V9(date_14_45, clientId)

        migrateDb("10")

        val actual = jdbcx.query("SELECT * FROM treatment", TreatmentV9Mapper)
        assertThat(actual, hasSize(1))
        assertThat(actual[0].date, equalTo(date_14_30))
    }

    private fun assertXPropsTableContent_V6(vararg expected: XpropsDboV6) {
        assertThat(jdbcx.query("SELECT * FROM xprops", XpropsDboV6Mapper), equalTo(expected.toList()))
    }

    private fun migrateToDbVersion6() {
        migrateDb("6.1", 2)
    }

    private fun migrateDb(toVersion: String = "-1", expectedMigrations: Int? = null) {
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

    private fun insertClient_V8(client: Client) {
        val sqlInsert = """
        INSERT INTO ${ClientJdbcRepository.TABLE} (
            id, created, firstName, lastName, nickName,
            mail, phone, street, zipCode, city,
            wantReceiveMails, birthday, gender_enum, countryOfOrigin, origin,
            relationship_enum, job, children, hobbies, note,
            textImpression, textMedical, textComplaints, textPersonal, textObjective,
            mainObjective, symptoms, elements, syndrom, tcmNote,
            category, donation
        ) VALUES (
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?
        )"""
        jdbcx.update(sqlInsert,
                client.id, client.created.toSqlTimestamp(), client.firstName, client.lastName, client.nickNameInt, // only takes nickname internal as single global one
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city,
                client.wantReceiveMails, client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.textMainObjective, client.textSymptoms, client.textFiveElements, client.textSyndrom, client.tcmNote,
                client.category.sqlCode, client.donation.sqlCode
        )
    }

    private fun insertClient_V9(id: String) {
        val sqlInsert = """
        INSERT INTO ${ClientJdbcRepository.TABLE} (
            id, created, firstName, lastName, nickNameInt, nickNameExt,
            mail, phone, street, zipCode, city, knownBy,
            wantReceiveMails, birthday, gender_enum, countryOfOrigin, origin,
            relationship_enum, job, children, hobbies, note,
            yyTendency, elementTendency, textImpression, textMedical, textComplaints, textPersonal, textObjective,
            mainObjective, symptoms, elements, syndrom, tcmNote,
            category, donation
        ) VALUES (
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?
        )"""
        val now = DateTime.now()
        jdbcx.update(sqlInsert,
                id, now.toSqlTimestamp(), "firstName", "lastName", "nickNameInt", "nickNameExt",
                "mail", "phone", "street", "zipCode", "city", "knownBy",
                true, now.toSqlTimestamp(), "?", "countryOfOrigin", "origin",
                "UNKNOWN", "job", "children", "hobbies", "note",
                "?", "?", "textImpression", "textMedical", "textComplaints", "textPersonal", "textObjective",
                "textMainObjective", "textSymptoms", "textFiveElements", "textSyndrom", "tcmNote",
                "B", "UNKNOWN"
        )
    }

    private fun insertTreamtent_V9(date: DateTime, clientId: String) {
        val now = DateTime.now()
        jdbcx.update("INSERT INTO treatment (" +
                "id, id_client, created, number, date, durationInMin, " +
                "aboutDiscomfort, aboutDiagnosis, aboutContent, aboutFeedback, aboutHomework, aboutUpcoming, note) VALUES (" +
                "?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?)",
                "newId", clientId, now.toSqlTimestamp(), 1, date.toSqlTimestamp(), Duration(1_000L * 60 * 60).toMinutes(),
                "aboutDiscomfort", "aboutDiagnosis", "aboutContent", "aboutFeedback", "aboutHomework", "aboutUpcoming", "note")
    }

}
