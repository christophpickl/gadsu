package at.cpickl.gadsu.persistence

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientJdbcRepository
import at.cpickl.gadsu.service.toMinutes
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationVersion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.joda.time.Duration
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class MigrateTestService(
        private val jdbcx: Jdbcx,
        private val flyway: Flyway
) {

    fun migrateDb(toVersion: String, expectedMigrations: Int? = null) {
        flyway.target = MigrationVersion.fromVersion(toVersion)

        val executedMigrations = flyway.migrate()
        if (expectedMigrations != null) {
            assertThat(executedMigrations, equalTo(expectedMigrations))
        }
    }

    // with V5 compared to V6 got no field: note
    fun insertXProp_V5(clientId: String, vararg sprops: SpropDboV5) {
        sprops.forEach {
            jdbcx.update("INSERT INTO xprops (id_client, key, val) VALUES (?, ?, ?)",
                    clientId, it.key, it.rawVal)
        }
    }

    // with V5 compared to V6 got no field: mainObject, ... syndrom
    fun insertClient_V5(client: Client) {
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

    fun insertClient_V8(client: Client) {
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

    fun insertClient_V9(id: String) {
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

    fun insertTreamtent_V9(date: DateTime, clientId: String) {
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

data class XpropsDboV6(val idClient: String, val key: String, val rawVal: String, val note: String?)

data class ClientV9(val nicknameInt: String, val nicknameExt: String, val knownBy: String, val yyTendency: String, val elementTendency: String)

object XpropsDboV6Mapper : RowMapper<XpropsDboV6> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            XpropsDboV6(rs.getString("id_client"), rs.getString("key"), rs.getString("val"), rs.getString("note"))
}

data class TreatmentV9(
        val date: DateTime
)

object TreatmentV9Mapper : RowMapper<TreatmentV9> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            TreatmentV9(rs.getDateTime("date"))
}

object ClientV9Mapper : RowMapper<ClientV9> {
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
data class SpropDboV5(val idClient: String, val key: String, val rawVal: String)
