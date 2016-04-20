package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ensurePersisted
import at.cpickl.gadsu.persistence.JdbcX
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject

interface TreatmentRepository {

    fun findAllFor(client: Client): List<Treatment>
    fun insert(treatment: Treatment): Treatment
    fun update(treatment: Treatment)
    fun delete(treatment: Treatment)

    fun countAllFor(client: Client): Int
    fun recalculateNumbers(pivot: Treatment)

}

class TreatmentSpringJdbcRepository @Inject constructor(
        private val jdbcx: JdbcX,
        private val idGenerator: IdGenerator
        ) : TreatmentRepository {
    companion object {

        val TABLE = "treatment"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAllFor(client: Client): List<Treatment> {
        client.ensurePersisted()

        val treatments = jdbcx.query("SELECT * FROM $TABLE WHERE id_client = ? ORDER BY number", arrayOf(client.id), Treatment.ROW_MAPPER)
        // because of ORDER clause, not needed: treatments.sort()
        return treatments
    }

    override fun insert(treatment: Treatment): Treatment {
        log.debug("insert(treatment={})", treatment)

        treatment.ensureNotPersisted()

        val newId = idGenerator.generate()
        @Suppress("SENSELESS_COMPARISON") // yes, it can indeed be that way!
        if (newId === null) {
            throw GadsuException("IdGenerator did return null, although compile forbids. Are you testing and havent setup a proper mock maybe?! (idGenerator=$idGenerator)")
        }

        jdbcx.update("INSERT INTO $TABLE (id, id_client, created, number, date, note) VALUES(?, ?, ?, ?, ?, ?)",
                newId, treatment.clientId, treatment.created.toSqlTimestamp(), treatment.number, treatment.date.toSqlTimestamp(), treatment.note)
        return treatment.copy(id = newId)
    }

    override fun update(treatment: Treatment) {
        log.debug("update(treatment={})", treatment)
        treatment.ensurePersisted()

        jdbcx.updateSingle("UPDATE ${TABLE} SET number = ?, date = ?, note = ? WHERE id = ?",
                treatment.number, treatment.date.toSqlTimestamp(), treatment.note, treatment.id)
    }

    override fun delete(treatment: Treatment) {
        log.debug("delete(treatment={})", treatment)
        treatment.ensurePersisted()

        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", treatment.id)
    }

    override fun countAllFor(client: Client): Int {
        log.debug("countAllFor(client={})", client)
        client.ensurePersisted()

        return jdbcx.count(TABLE, arrayOf(client.id), "WHERE id_client = ?")
    }

    override fun recalculateNumbers(pivot: Treatment) {
        log.debug("recalculateNumbers(pivot={})", pivot)

        val updatedRows = jdbcx.update("UPDATE $TABLE SET number = number - 1 WHERE id_client = ? AND number > ?",
                pivot.clientId, pivot.number)
        log.trace("Updated rows for number recalculation: {}", updatedRows)
    }

}

fun Treatment.ensurePersisted() {
    if (!yetPersisted) {
        throw PersistenceException("Treatment must have set an ID! ($this)", PersistenceErrorCode.EXPECTED_YET_PERSISTED)
    }
}

fun Treatment.ensureNotPersisted() {
    if (yetPersisted) {
        throw PersistenceException("Treatment must not have set an ID! ($this)", PersistenceErrorCode.EXPECTED_NOT_YET_PERSISTED)
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
                rs.getString("note")
        )
    }
