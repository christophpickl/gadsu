package at.cpickl.gadsu.client.xprops

import at.cpickl.gadsu.persistence.Jdbcx
import com.google.common.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject

data class XPropSql(val key: String, val value: String)

data class XPropsSql(val items: List<XPropSql>)

interface XPropsSqlRepository {
    fun reset(clientId: String, props: XPropsSql)
    fun readAllFor(clientId: String): XPropsSql
}

class XPropsSqlJdbcRepository @Inject constructor(
        private val jdbc: Jdbcx
) : XPropsSqlRepository {

    companion object {
        val TABLE = "xprops"

        @VisibleForTesting
        val ROW_MAPPER: RowMapper<XPropSql>
            get() = RowMapper { rs, rowNum ->
                val sqlKey = rs.getString("key")
                val sqlValue = rs.getString("val")
                XPropSql(sqlKey, sqlValue)
            }


    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun readAllFor(clientId: String): XPropsSql {
        return XPropsSql(jdbc.query("SELECT * FROM $TABLE WHERE id_client = ?", arrayOf(clientId), ROW_MAPPER))
    }

    override fun reset(clientId: String, props: XPropsSql) {
        val countDeleted = jdbc.update("DELETE FROM $TABLE WHERE id_client = ?", clientId)
        log.trace("deleted {} props from table. going to insert {} new props.", countDeleted, props.items.size)

        props.items.forEach {
            jdbc.update("INSERT INTO $TABLE (id_client, key, val) VALUES (?, ?, ?)",
                    clientId, it.key, it.value)
        }
    }

}

