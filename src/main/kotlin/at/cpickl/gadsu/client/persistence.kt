package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.image.toSqlBlob
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.persistence.toBufferedImage
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.Blob

private val log = LoggerFactory.getLogger("at.cpickl.gadsu.client.persistence")


interface ClientRepository {

    fun findAll(): List<Client>

    /**
     * @param client its ID must be null
     * @return new client instance with a non-null ID
     */
    fun insertWithoutPicture(client: Client): Client

    fun updateWithoutPicture(client: Client)

    fun changePicture(client: Client)

    /**
     * Will NOT trigger cascade delete! Use ClientService instead.
     */
    fun delete(client: Client)

}

class ClientSpringJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
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

    override fun insertWithoutPicture(client: Client): Client {
        log.debug("insert(client={})", client)
        client.ensureNotPersisted()

        val newId = idGenerator.generate()
        val sqlInsert = """
        INSERT INTO $TABLE (
            id, firstName, lastName, created, birthday,
            gender_enum, countryOfOrigin, mail, phone, street,
            zipCode, city, relationship_enum, job, children,
            note
        ) VALUES (
        ?, ?, ?, ?, ?,
        ?, ?, ?, ?, ?,
        ?, ?, ?, ?, ?,
        ?)"""
        jdbcx.update(sqlInsert,
                newId, client.firstName, client.lastName, client.created.toSqlTimestamp(), client.birthday?.toSqlTimestamp(),
                client.gender.sqlCode, client.countryOfOrigin, client.contact.mail, client.contact.phone, client.contact.street,
                client.contact.zipCode, client.contact.city, client.relationship.sqlCode, client.job, client.children,
                client.note)
        return client.copy(
                id = newId,
                // MINOR add a 3rd default pic for unknown gender
                picture = if (client.gender == Gender.FEMALE) MyImage.DEFAULT_PROFILE_WOMAN else MyImage.DEFAULT_PROFILE_MAN
        )
    }

    override fun updateWithoutPicture(client: Client) {
        log.debug("update(client={})", client)
        client.ensurePersisted()

        jdbcx.updateSingle("UPDATE $TABLE SET firstName = ?, lastName = ? WHERE id = ?",
                client.firstName, client.lastName, client.id!!)
    }

    override fun changePicture(client: Client) {
        // TODO AOP logging for all service + repository classes
        client.ensurePersisted()

        jdbcx.updateSingle("UPDATE $TABLE SET picture = ? WHERE id = ?",
                client.picture.toSqlBlob(), client.id!!)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)
        client.ensurePersisted()

        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", client.id)
    }

}

fun Client.ensurePersisted() {
    if (!yetPersisted) {
        throw PersistenceException("Client must have set an ID! ($this)", PersistenceErrorCode.EXPECTED_YET_PERSISTED)
    }
}
fun Client.ensureNotPersisted() {
    if (yetPersisted) {
        throw PersistenceException("Client must not have set an ID! ($this)", PersistenceErrorCode.EXPECTED_NOT_YET_PERSISTED)
    }
}

@Suppress("UNUSED")
val Client.Companion.ROW_MAPPER: RowMapper<Client>
    get() = RowMapper { rs, rowNum ->
        log.trace("Transforming database row for client with first name: '{}'", rs.getString("firstName"))
        val gender = Gender.parseSqlCode(rs.getString("gender_enum"))
        Client(
                rs.getString("id"),
                DateTime(rs.getTimestamp("created")),
                rs.getString("firstName"),
                rs.getString("lastName"),
                Contact(
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getString("street"),
                        rs.getString("zipCode"),
                        rs.getString("city")
                ),
                if (rs.getTimestamp("birthday") == null) null
                else DateTime(rs.getTimestamp("birthday")),
                gender,
                rs.getString("countryOfOrigin"),
                Relationship.parseSqlCode(rs.getString("relationship_enum")),
                rs.getString("job"),
                rs.getString("children"),
                rs.getString("note"),

                readFromBlob(rs.getBlob("picture"), gender)

        )
    }


private fun readFromBlob(blob: Blob?, gender: Gender): MyImage {
    if (blob != null) {
        val buffered = blob.toBufferedImage()
        if (buffered != null) {
            log.trace("Loading image for client from database BLOB.")
            //    Files.write(blob.toByteArray(), File("readFromBlob.jpg"))
            return buffered.toMyImage()
        }
    }
    log.trace("No picture stored for client, returning default image based on gender.")
    return gender.defaultImage
}


