package gadsu.persistence

import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.toDateTime
import org.flywaydb.core.api.migration.jdbc.JdbcMigration
import java.sql.Connection


class V10__treatment_dates : JdbcMigration {

    private val log = LOG(javaClass)

    override fun migrate(connection: Connection) {
        log.info("migrate")

        val statement = connection.prepareStatement("SELECT id, date FROM treatment")
        try {
            val rs = statement.executeQuery()
            while (rs.next()) {
                val id = rs.getString("id")
                val date = rs.getTimestamp("date").toDateTime()
                val minute = date.minuteOfHour().get()
                val newDate = when(minute) {
                    15 -> date.withMinuteOfHour(0)
                    45 -> date.withMinuteOfHour(30)
                    0, 30 -> date
                    else -> {
                        log.warn("Invalid date $date for treatment with ID: $id (reset minutes to 0)")
                        date.withMinuteOfHour(0)
                    }
                }
                if (newDate == date) {
                    continue
                }
                log.trace("Updating treatment with id [$id] from date $date to new date: $newDate")
                val ps = connection.prepareStatement("UPDATE treatment SET date = ? WHERE id = ?")
                ps.setTimestamp(1, newDate.toSqlTimestamp())
                ps.setString(2, id)
                ps.executeUpdate()
                connection.commit()
                ps.close()
            }
        } finally {
            statement.close()
        }
    }

}
