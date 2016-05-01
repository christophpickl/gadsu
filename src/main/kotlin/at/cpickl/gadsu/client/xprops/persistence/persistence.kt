package at.cpickl.gadsu.client.xprops.persistence

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ensurePersisted
import at.cpickl.gadsu.persistence.Jdbcx
import com.google.common.annotations.VisibleForTesting
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject


interface XPropsSqlRepository {
    fun select(client: Client): List<SProp>
    fun delete(client: Client)
    fun insert(client: Client, props: List<SProp>)
}


class XPropsSqlJdbcRepository @Inject constructor(
        private val jdbc: Jdbcx
) : XPropsSqlRepository {

    companion object {
        val TABLE = "xprops"

        @VisibleForTesting
        val ROW_MAPPER: RowMapper<SProp>
            get() = RowMapper { rs, rowNum ->
                val sqlKey = rs.getString("key")
                val sqlValue = rs.getString("val")
                SProp(sqlKey, sqlValue)
            }


    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun select(client: Client): List<SProp> {
        client.ensurePersisted()
        return jdbc.query("SELECT * FROM ${TABLE} WHERE id_client = ?", arrayOf(client.id!!), ROW_MAPPER)
    }

    override fun delete(client: Client) {
        client.ensurePersisted()
        val countDeleted = jdbc.update("DELETE FROM ${TABLE} WHERE id_client = ?", client.id!!)
        log.trace("deleted {} props from table.", countDeleted)
    }

    override fun insert(client: Client, props: List<SProp>) {
        client.ensurePersisted()
        log.trace("going to insert {} new props.", props.size)
        props.forEach {
            jdbc.update("INSERT INTO ${TABLE} (id_client, key, val) VALUES (?, ?, ?)",
                    client.id!!, it.key, it.value)
        }
    }

}

