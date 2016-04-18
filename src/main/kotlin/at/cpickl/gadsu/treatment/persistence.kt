package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.JdbcX
import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.toSqlTimestamp
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject

interface TreatmentRepository {

    fun findAllFor(client: Client): List<Treatment>
    fun insert(treatment: Treatment, client: Client): Treatment
    fun update(treatment: Treatment)
    fun delete(treatment: Treatment)

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
        if (client.yetPersisted === false) {
            throw PersistenceException("Client was not yet persisted! ($client)")
        }
        val treatments = jdbcx.query("SELECT * FROM $TABLE WHERE id_client = ?", arrayOf(client.id), Treatment.ROW_MAPPER)
        treatments.sort()
        return treatments
    }

    override fun insert(treatment: Treatment, client: Client): Treatment {
        log.debug("insert(treatment={}, client={})", treatment, client)

        if (client.yetPersisted === false) {
            throw PersistenceException("Client was not yet persisted! ($treatment, $client)")
        }
        if (treatment.yetPersisted === true) {
            throw PersistenceException("Treatment was yet persisted! ($treatment, $client)")
        }

        val newId = idGenerator.generate()
        @Suppress("SENSELESS_COMPARISON") // yes, it can indeed be that way!
        if (newId === null) {
            throw GadsuException("IdGenerator did return null, although compile forbids. Are you testing and havent setup a proper mock maybe?! (idGenerator=$idGenerator)")
        }

        jdbcx.update("INSERT INTO $TABLE (id, id_client, created, number, date, note) VALUES(?, ?, ?, ?, ?, ?)",
                newId, client.id, treatment.created.toSqlTimestamp(), treatment.number, treatment.date.toSqlTimestamp(), treatment.note)
        return treatment.copy(id = newId)
    }

    override fun update(treatment: Treatment) {
        log.debug("update(treatment={})", treatment)
        if (!treatment.yetPersisted) {
            throw PersistenceException("Treatment must have set an ID! ($treatment)")
        }
        jdbcx.updateSingle("UPDATE ${TABLE} SET number = ?, date = ?, note = ? WHERE id = ?",
                treatment.number, treatment.date.toSqlTimestamp(), treatment.note, treatment.id)
    }

    override fun delete(treatment: Treatment) {
        log.debug("delete(treatment={})", treatment)
        if (treatment.yetPersisted === false) {
            throw PersistenceException("Treatment was not yet persisted! ($treatment)")
        }
        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", treatment.id)
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
