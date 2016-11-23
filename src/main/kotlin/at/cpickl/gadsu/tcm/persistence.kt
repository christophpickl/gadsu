package at.cpickl.gadsu.tcm

import at.cpickl.gadsu.tcm.model.Meridian
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet


class MeridianRowMapper(private val sqlColumnName: String = "meridian") : RowMapper<Meridian> {
    override fun mapRow(rs: ResultSet, rowNum: Int) =
            Meridian.bySqlCode(rs.getString(sqlColumnName))
}
