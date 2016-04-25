package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import javax.inject.Inject
import kotlin.reflect.KClass

interface HasSqlRepresentation {
    val sqlRepresentation: String
}

enum class SleepEnum(override val sqlRepresentation: String) : HasSqlRepresentation {
    ProblemsFallAsleep("ProblemsFallAsleep"),
    ProblemsWakeUp("ProblemsWakeUp"),
    TiredInTheMorning("TiredInTheMorning"),
    TiredInTheEvening("TiredInTheEvening");
    companion object {
        val key = "Sleep"
    }
}

enum class StringProps(val key: String) {
    MoodOfToday("MoodOfToday")
}

private val allStrings: List<String> = StringProps.values().map { it.key }
private val allEnums: Map<String, KClass<Enum<*>>> = mapOf(
        Pair(SleepEnum.key, SleepEnum::class as KClass<Enum<*>>)
)

data class Props(val data: Map<String, out PropType>)

interface PropType {
    fun toSqlValue(): String
}
data class PropStringType(val value: String) : PropType {
    override fun toSqlValue() = value
}
// Boolean specifies if selected or not
data class PropMultiEnumType(val values: List<String>) : PropType {
    override fun toSqlValue() =  values/*.filterValues { it == true }.keys*/.joinToString(",")
}



interface ClientPropsRepository {
    fun reset(clientId: String, props: Props)

    fun readAllFor(client: Client): Props
}

class ClientPropsSpringJdbcRepository @Inject constructor(
        private val jdbc: Jdbcx
        ) :
        ClientPropsRepository {
    companion object {

        val TABLE = "client_props"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun reset(clientId: String, props: Props) {
        val countDeleted = jdbc.update("DELETE FROM $TABLE WHERE id_client = ?", clientId)
        log.trace("deleted {} props from table.", countDeleted)

        props.data.forEach {
            jdbc.update("INSERT INTO $TABLE (id_client, key, val) VALUES (?, ?, ?)",
                    clientId, it.key, it.value.toSqlValue())
        }
    }

    override fun readAllFor(client: Client): Props {
        val rawRows = jdbc.query("SELECT * FROM $TABLE WHERE id_client = ?", arrayOf(client.id!!), PropRawRow.ROW_MAPPER)

        val props = java.util.HashMap<String, PropType>()

        rawRows.forEach {
            if (allStrings.contains(it.sqlKey)) {
                props.put(it.sqlKey, PropStringType(it.sqlValue))
            } else if (allEnums.contains(it.sqlKey)) {
                // needed when uplifting to higher abstraction level
//                val enumKlass = allEnums[it.sqlKey]
//                enumKlass!!.java.declaredFields.filter { it.isEnumConstant }.forEach {
//                    println(it)
//                    it as HasSqlRepresentation
//                }
                val enumValues = it.sqlValue.split(",")
                props.put(it.sqlKey, PropMultiEnumType(enumValues))
            } else {
                throw PersistenceException("Invalid property key for data row: '${it}'!", PersistenceErrorCode.PROPS_INVALID_KEY)
            }
        }

        return Props(props)
    }

}

data class PropRawRow(val clientId: String, val sqlKey: String, val sqlValue: String) {
    companion object {} // for extension methods
}


val PropRawRow.Companion.ROW_MAPPER: RowMapper<PropRawRow>
    get() = RowMapper { rs, rowNum ->
        val clientId = rs.getString("id_client")
        val sqlKey = rs.getString("key")
        val sqlValue = rs.getString("val")
        PropRawRow(clientId, sqlKey, sqlValue)
    }
