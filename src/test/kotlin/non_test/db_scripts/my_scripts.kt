package non_test.db_scripts

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.ROW_MAPPER
import org.hsqldb.jdbc.JDBCDataSource

fun main(args: Array<String>) {
    val dataSource = JDBCDataSource()
    dataSource.url = "jdbc:hsqldb:file:/Users/wu/.gadsu_dev/database/database"
    dataSource.user = "SA"
    val stmt = dataSource.connection.prepareStatement("SELECT * FROM ${BloodPressureJdbcRepository.TABLE}")

    val rs = stmt.executeQuery()
    while (rs.next()) {
        println(BloodPressure.ROW_MAPPER.mapRow(rs, -1))
    }
    stmt.close()
    dataSource.connection.close()
}
