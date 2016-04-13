package at.cpickl.gadsu.client

import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.SQL_TIMESTAMP
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.IdGenerator
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper

interface ClientRepository {

    fun findAll(): List<Client>

    /**
     * @param client its ID must be null
     * @return new client instance with a non-null ID
     */
    fun insert(client: Client): Client

}

class ClientSpringJdbcRepository @Inject constructor(
        private val jdbc: JdbcTemplate,
        private val idGenerator: IdGenerator
) : ClientRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll(): List<Client> {
        return jdbc.query("SELECT * FROM client", Client.ROW_MAPPER)
    }

    override fun insert(client: Client): Client {
        log.debug("save(client={})", client)
        if (client.id != null) {
            throw PersistenceException("Client must not have set the ID! ($client)")
        }
        val newId = idGenerator.generate()
        jdbc.execute("CALL insert_client('$newId', '${client.firstName}', '${client.lastName}', TIMESTAMP '${DateFormats.SQL_TIMESTAMP.print(client.created)}');")
        return client.withId(newId)
    }

}

@Suppress("UNUSED")
val Client.Companion.ROW_MAPPER: RowMapper<Client>
    get() = RowMapper<Client> { rs, rowNum ->
        Client(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                DateTime(rs.getTimestamp("created"))
        )
    }
