package at.cpickl.gadsu.client

import at.cpickl.gadsu.JdbcX
import at.cpickl.gadsu.PersistenceException
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.toSqlTimestamp
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper

interface ClientRepository {

    fun findAll(): List<Client>

    /**
     * @param client its ID must be null
     * @return new client instance with a non-null ID
     */
    fun insert(client: Client): Client

    fun update(client: Client)

    /**
     * Will NOT trigger cascade delete! Use ClientService instead.
     */
    fun delete(client: Client)

}

class ClientSpringJdbcRepository @Inject constructor(
        private val jdbcx: JdbcX,
        private val idGenerator: IdGenerator
) : ClientRepository {

    companion object {
        val TABLE = "client"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll(): List<Client> {
        val clients = jdbcx.query("SELECT * FROM $TABLE", Client.ROW_MAPPER)
        clients.sort()
        return clients
    }

    override fun insert(client: Client): Client {
        log.debug("insert(client={})", client)
        if (client.id != null) {
            throw PersistenceException("Client must not have set the ID! ($client)")
        }
        val newId = idGenerator.generate()
        jdbcx.update("INSERT INTO $TABLE (id, firstName, lastName, created) VALUES (?, ?, ?, ?)",
                newId, client.firstName, client.lastName, client.created.toSqlTimestamp())
        return client.copy(id = newId)
    }

    override fun update(client: Client) {
        log.debug("update(client={})", client)
        val affectedRows = jdbcx.update("UPDATE $TABLE SET firstName = ?, lastName = ? WHERE id = ?",
                client.firstName, client.lastName, client.id)
        if (affectedRows != 1) {
            throw PersistenceException("Exepcted exactly one row to be updated, but was: $affectedRows ($client)")
        }
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)
        if (client.id == null) {
            throw PersistenceException("Client got no ID associated! $client")
        }
        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", client.id)
    }

}

@Suppress("UNUSED")
val Client.Companion.ROW_MAPPER: RowMapper<Client>
    get() = RowMapper { rs, rowNum ->
        Client(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                DateTime(rs.getTimestamp("created"))
        )
    }
