package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.image.toSqlBlob
import at.cpickl.gadsu.persistence.JdbcX
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.persistence.toBufferedImage
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.Blob

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
        log.debug("findAll()")

        val clients = jdbcx.query("SELECT * FROM $TABLE", Client.ROW_MAPPER)
        clients.sort()
        return clients
    }

    override fun insert(client: Client): Client {
        log.debug("insert(client={})", client)

        if (client.yetPersisted) {
            throw PersistenceException("Client must not have set an ID! ($client)")
        }

        val newId = idGenerator.generate()
        val sqlInsert = "INSERT INTO $TABLE (id, firstName, lastName, created, picture) VALUES (?, ?, ?, ?, ?)"
        jdbcx.update(sqlInsert, newId, client.firstName, client.lastName, client.created.toSqlTimestamp(), client.picture.toSqlBlob())
        return client.copy(id = newId)
    }

    override fun update(client: Client) {
        log.debug("update(client={})", client)

        if (!client.yetPersisted) {
            throw PersistenceException("Client must have set an ID! ($client)")
        }

        jdbcx.updateSingle("UPDATE $TABLE SET firstName = ?, lastName = ?, picture = ? WHERE id = ?",
                client.firstName, client.lastName, client.picture.toSqlBlob(), client.id)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)

        if (client.id === null) {
            throw PersistenceException("Client got no ID associated! $client")
        }

        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", client.id)
    }

}

@Suppress("UNUSED")
val Client.Companion.ROW_MAPPER: RowMapper<Client>
    get() = RowMapper { rs, rowNum ->
        log.trace("Transforming database row for client with first name: '{}'", rs.getString("firstName"))
        Client(
                rs.getString("id"),
                DateTime(rs.getTimestamp("created")),
                rs.getString("firstName"),
                rs.getString("lastName"),
                readFromBlob(rs.getBlob("picture"))

        )
    }

private val log = LoggerFactory.getLogger("at.cpickl.gadsu.client.persistence")

private fun readFromBlob(blob: Blob?): MyImage {
    if (blob == null) {
        log.trace("No picture stored for client.")
        // MINOR change depending on sex of client (if available, otherwise change to alien symbol ;))
        return Images.DEFAULT_PROFILE_MAN
    }

    log.trace("Loading image for client from database BLOB.")
//    Files.write(blob.toByteArray(), File("readFromBlob.jpg"))
    return blob.toBufferedImage().toMyImage()
}


