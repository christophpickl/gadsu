package at.cpickl.gadsu.appointments

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

    fun insert(appointment: Appointment): Appointment

    fun findAllFor(client: Client): List<Appointment>

}


class AppointmentJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : AppointmentRepository {

    companion object {
        val TABLE = "appointment"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun insert(appointment: Appointment): Appointment {
        log.debug("insert(appointment={})", appointment)
        appointment.ensureNotPersisted()

        val newId = idGenerator.generate()
        jdbcx.update("INSERT INTO $TABLE (id, id_client, created, startDate, endDate) VALUES (?, ?, ?, ?)",
                newId, appointment.clientId, appointment.created.toSqlTimestamp(), appointment.start.toSqlTimestamp(), appointment.end.toSqlTimestamp())

        return appointment.copy(id = newId)

    }

    override fun findAllFor(client: Client): List<Appointment> {
        log.debug("findAllFor(client={})", client)
        client.ensurePersisted()

        val appointments = jdbcx.query("SELECT * FROM $TABLE WHERE id_client = ?", arrayOf(client.id!!), Appointment.ROW_MAPPER)
        appointments.sort()
        return appointments
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
            DateTime(rs.getTimestamp("endDate"))
        )
    }
