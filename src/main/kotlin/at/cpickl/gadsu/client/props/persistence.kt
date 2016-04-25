package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject

data class SqlProps(val data: Map<String, out SqlPropType>)

interface SqlPropType {
    fun toSqlValue(): String
}
data class SqlPropStringType(val value: String) : SqlPropType {
    override fun toSqlValue() = value
}
// Boolean specifies if selected or not
data class SqlPropMultiEnumType(val values: List<String>) : SqlPropType {
    override fun toSqlValue() =  values/*.filterValues { it == true }.keys*/.joinToString(",")
}


interface ClientPropsRepository {
    fun reset(clientId: String, props: SqlProps)

    fun readAllFor(client: Client): SqlProps
}

class ClientPropsSpringJdbcRepository @Inject constructor(
        private val jdbc: Jdbcx
        ) :
        ClientPropsRepository {
    companion object {

        val TABLE = "client_props"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun reset(clientId: String, props: SqlProps) {
        val countDeleted = jdbc.update("DELETE FROM $TABLE WHERE id_client = ?", clientId)
        log.trace("deleted {} props from table.", countDeleted)

        props.data.forEach {
            jdbc.update("INSERT INTO $TABLE (id_client, key, val) VALUES (?, ?, ?)",
                    clientId, it.key, it.value.toSqlValue())
        }
    }

    override fun readAllFor(client: Client): SqlProps {
        val rawRows = jdbc.query("SELECT * FROM $TABLE WHERE id_client = ?", arrayOf(client.id!!), PropSqlRow.ROW_MAPPER)

        val props = java.util.HashMap<String, SqlPropType>()

        rawRows.forEach {

            val sqlProp: SqlPropType = onPropType(it.sqlKey, it, SqlPropTypeCallback)

            props.put(it.sqlKey, sqlProp)
            /*
            if (allStrings.contains(it.sqlKey)) {
                props.put(it.sqlKey, SqlPropStringType(it.sqlValue))
            } else if (allMultiEnums.contains(it.sqlKey)) {
                // needed when uplifting to higher abstraction level
//                val enumKlass = allEnums[it.sqlKey]
//                enumKlass!!.java.declaredFields.filter { it.isEnumConstant }.forEach {
//                    println(it)
//                    it as HasKey
//                }
                val enumValues = it.sqlValue.split(",")
                props.put(it.sqlKey, SqlPropMultiEnumType(enumValues))
            } else {
                throw PersistenceException("Invalid property key for data row: '${it}'!", PersistenceErrorCode.PROPS_INVALID_KEY)
            }
            */
        }

        return SqlProps(props)
    }

}

data class PropSqlRow(val clientId: String, val sqlKey: String, val sqlValue: String) {
    companion object {} // for extension methods
}


val PropSqlRow.Companion.ROW_MAPPER: RowMapper<PropSqlRow>
    get() = RowMapper { rs, rowNum ->
        val clientId = rs.getString("id_client")
        val sqlKey = rs.getString("key")
        val sqlValue = rs.getString("val")
        PropSqlRow(clientId, sqlKey, sqlValue)
    }
