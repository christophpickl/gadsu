package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.ensureNotPersisted
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper


interface AppointmentRepository {

    fun findAllFor(client: Client): List<Appointment>
    fun findAllStartAfter(startPivot: DateTime, client: Client): List<Appointment>
    fun insert(appointment: Appointment): Appointment
    fun update(appointment: Appointment)
    fun delete(appointment: Appointment)
    fun deleteAll(client: Client)

}


class AppointmentJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : AppointmentRepository {
    companion object {

        val TABLE = "appointment"

    }

    private val log = LoggerFactory.getLogger(javaClass)
    override fun findAllFor(client: Client): List<Appointment> {
        return findFor(client, "")
    }
    override fun findAllStartAfter(startPivot: DateTime, client: Client): List<Appointment> {
        return findFor(client, "AND startDate > ?", startPivot.toSqlTimestamp())
    }

    private fun findFor(client: Client, whereClause: String, vararg args: Any): List<Appointment> {
        log.debug("findXXX(whereClause={})", whereClause)
        client.ensurePersisted()

        val argsMerged = args.toMutableList()
        argsMerged.add(0, client.id!!)
        val appointments = jdbcx.query("SELECT * FROM $TABLE WHERE id_client = ? $whereClause ORDER BY startDate",
                argsMerged.toTypedArray(), Appointment.ROW_MAPPER)
        appointments.sort()
        return appointments
    }

    override fun insert(appointment: Appointment): Appointment {
        log.debug("insert(appointment={})", appointment)
        appointment.ensureNotPersisted()

        val newId = idGenerator.generate()
        jdbcx.update(
            """INSERT INTO $TABLE (
                id, id_client, created, startDate, endDate,
                note, gcal_id, gcal_url
            ) VALUES (
                ?, ?, ?, ?, ?,
                ?, ?, ?
            )""",
            newId, appointment.clientId, appointment.created.toSqlTimestamp(), appointment.start.toSqlTimestamp(), appointment.end.toSqlTimestamp(),
            appointment.note, appointment.gcalId, appointment.gcalUrl)

        return appointment.copy(id = newId)

    }

    override fun update(appointment: Appointment) {
        log.debug("update(appointment={})", appointment)
        appointment.ensurePersisted()

        jdbcx.update("""
                UPDATE $TABLE SET
                    startDate = ?,
                    endDate = ?,
                    note = ?
                WHERE
                    id = ?
        """, appointment.start.toSqlTimestamp(), appointment.end.toSqlTimestamp(), appointment.note,
                appointment.id!!)
    }

    override fun delete(appointment: Appointment) {
        log.debug("delete(appointment={})", appointment)
        appointment.ensurePersisted()
        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", appointment.id!!)
    }

    override fun deleteAll(client: Client) {
        client.ensurePersisted()
        val deleted = jdbcx.update("DELETE FROM $TABLE where id_client = ?", client.id)
        log.trace("Deleted {} appointments for client: {}", deleted, client)
    }

}

@Suppress("UNUSED")
val Appointment.Companion.ROW_MAPPER: RowMapper<Appointment>
    get() = RowMapper { rs, rowNum ->
        Appointment(
                rs.getString("id"),
                rs.getString("id_client"),
                DateTime(rs.getTimestamp("created")),
                DateTime(rs.getTimestamp("startDate")),
                DateTime(rs.getTimestamp("endDate")),
                rs.getString("note"),
                rs.getString("gcal_id"),
                rs.getString("gcal_url")
        )
    }
