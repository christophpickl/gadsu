package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.ensureNotPersisted
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolJdbcRepository
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.toMinutes
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject


interface TreatmentRepository {

    // ordered by number DESC
    fun findAllFor(client: Client): List<Treatment>

    fun findAllForRaw(clientId: String): List<Treatment>
    fun findFirstFor(clientId: String): Treatment?

    fun insert(treatment: Treatment): Treatment
    fun update(treatment: Treatment)

    fun delete(treatment: Treatment)
    fun countAllFor(client: Client): Int

    fun calculateMaxNumberUsed(client: Client): Int?

    // used for the treatment progress bar
    // !!! in future exclude those already used in protocol !!!
    fun countAllNonProtocolized(): Int

}

class TreatmentJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : TreatmentRepository {
    companion object {

        val TABLE = "treatment"

    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAllFor(client: Client): List<Treatment> {
        client.ensurePersisted()
        return findAllForRaw(client.id!!)
    }

    override fun findAllForRaw(clientId: String): List<Treatment> {
        // because of ORDER clause, not needed: treatments.sort()
        return jdbcx.query("SELECT * FROM $TABLE WHERE id_client = ? ORDER BY number DESC", arrayOf(clientId), Treatment.ROW_MAPPER)
    }

    override fun findFirstFor(clientId: String): Treatment? {
        return jdbcx.queryMaybeSingle(Treatment.ROW_MAPPER,
                "SELECT TOP 1 * FROM $TABLE WHERE id_client = ? ORDER BY date ASC", arrayOf(clientId))
    }

    override fun insert(treatment: Treatment): Treatment {
        log.debug("insert(treatment={})", treatment)

        treatment.ensureNotPersisted()

        val newId = idGenerator.generate()
        @Suppress("SENSELESS_COMPARISON") // yes, it can indeed be that way!
        if (newId === null) {
            throw GadsuException("IdGenerator did return null, although compile forbids. Are you testing and havent setup a proper mock maybe?! (idGenerator=$idGenerator)")
        }

        jdbcx.update("INSERT INTO $TABLE (" +
                "id, id_client, created, number, date, durationInMin, " +
                "aboutDiscomfort, aboutDiagnosis, aboutContent, aboutFeedback, aboutHomework, aboutUpcoming, note) VALUES (" +
                "?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?)",
                newId, treatment.clientId, treatment.created.toSqlTimestamp(), treatment.number, treatment.date.toSqlTimestamp(), treatment.duration.toMinutes(),
                treatment.aboutDiscomfort, treatment.aboutDiagnosis, treatment.aboutContent, treatment.aboutFeedback, treatment.aboutHomework, treatment.aboutUpcoming, treatment.note)
        return treatment.copy(id = newId)
    }

    override fun update(treatment: Treatment) {
        log.debug("update(treatment={})", treatment)
        treatment.ensurePersisted()

        jdbcx.updateSingle("UPDATE $TABLE SET number = ?, date = ?, durationInMin = ?, " +
                "aboutDiscomfort = ?, aboutDiagnosis = ?, aboutContent = ?, aboutFeedback = ?, aboutHomework = ?, aboutUpcoming = ?, note = ? WHERE id = ?",
                treatment.number, treatment.date.toSqlTimestamp(), treatment.duration.toMinutes(),
                treatment.aboutDiscomfort, treatment.aboutDiagnosis, treatment.aboutContent, treatment.aboutFeedback, treatment.aboutHomework, treatment.aboutUpcoming, treatment.note, treatment.id)
    }

    override fun delete(treatment: Treatment) {
        log.debug("delete(treatment={})", treatment)
        treatment.ensurePersisted()

        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", treatment.id)
    }

    override fun countAllFor(client: Client): Int {
        log.debug("countAllFor(client={})", client)
        client.ensurePersisted()

        val count = jdbcx.count(TABLE, arrayOf(client.id), "WHERE id_client = ?")
        log.trace("count result: {}", count)
        return count
    }

    override fun countAllNonProtocolized(): Int {
        log.debug("countAllNonProtocolized()")
//        val total = jdbcx.count(TABLE)
//        val protocolized = jdbcx.queryForObject<Int>("SELECT COUNT(*) FROM $TABLE JOIN ${MultiProtocolJdbcRepository.TABLE_KEYS} ON ($TABLE.id = ${MultiProtocolJdbcRepository.TABLE_KEYS}.id_treatment)",
//                emptyArray(), RowMapper { rs, i -> rs.getInt(1) })
//        return total - protocolized

        return jdbcx.queryForObject<Int>("SELECT COUNT(*) FROM $TABLE WHERE id NOT IN (SELECT DISTINCT(id_treatment) FROM ${MultiProtocolJdbcRepository.TABLE_KEYS})",
                emptyArray(), RowMapper { rs, i -> rs.getInt(1) })
    }

    override fun calculateMaxNumberUsed(client: Client): Int? {
        log.debug("calculateMaxNumberUsed(client)")

        client.ensurePersisted()
        val found = jdbcx.queryMaybeSingle(Treatment.ROW_MAPPER,
                "SELECT * FROM $TABLE WHERE id_client = ? ORDER BY number DESC LIMIT 1", arrayOf(client.id!!))
                ?: return null
        return found.number
    }

}

@Suppress("UNUSED")
val Treatment.Companion.ROW_MAPPER: RowMapper<Treatment>
    get() = RowMapper { rs, rowNum ->
        Treatment(
                rs.getString("id"),
                rs.getString("id_client"),
                DateTime(rs.getTimestamp("created")),
                rs.getInt("number"),
                DateTime(rs.getTimestamp("date")),
                minutes(rs.getInt("durationInMin")),
                rs.getString("aboutDiscomfort"),
                rs.getString("aboutDiagnosis"),
                rs.getString("aboutContent"),
                rs.getString("aboutFeedback"),
                rs.getString("aboutHomework"),
                rs.getString("aboutUpcoming"),
                rs.getString("note"),
                mutableListOf() // FIXME #17 implement me
        )
    }
