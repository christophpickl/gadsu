package at.cpickl.gadsu.client

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.image.toSqlBlob
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.ensureNotPersisted
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.persistence.toBufferedImage
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.service.nullOrWith2
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.Blob
import java.sql.Timestamp

private val log = LoggerFactory.getLogger("at.cpickl.gadsu.client.persistence")


interface ClientRepository {

    fun findAll(filterState: ClientState? = null): List<Client>

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

    fun findById(id: String): Client

}

class ClientJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : ClientRepository {

    companion object {
        val TABLE = "client"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll(filterState: ClientState?): List<Client> {
        log.debug("findAll(filterState={})", filterState)

        val clients = if (filterState == null)
            jdbcx.query("SELECT * FROM $TABLE", Client.ROW_MAPPER)
        else {
            jdbcx.query2("SELECT * FROM $TABLE WHERE state = ?", Client.ROW_MAPPER, filterState.sqlCode)
        }
        clients.sort()
        return clients
    }

    override fun findById(id: String): Client {
        return jdbcx.querySingle(Client.ROW_MAPPER, "SELECT * FROM $TABLE WHERE id = ?", id)
    }

    override fun insertWithoutPicture(client: Client): Client {
        log.debug("insert(client={})", client)
        client.ensureNotPersisted()

        val newId = idGenerator.generate()
        @Suppress("SENSELESS_COMPARISON") // yes, it can indeed be that way! because of stupid mocking.
        if (newId === null) {
            throw GadsuException("IdGenerator did return null, although compile forbids. Are you testing and havent setup a proper mock maybe?! (idGenerator=$idGenerator)")
        }
        val sqlInsert = """
        INSERT INTO $TABLE (
            id, created, firstName, lastName, nickName,
            mail, phone, street, zipCode, city,
            wantReceiveDoodleMails, birthday, gender_enum, countryOfOrigin, origin,
            relationship_enum, job, children, hobbies, note,
            textImpression, textMedical, textComplaints, textPersonal, textObjective,
            mainObjective, symptoms, elements, syndrom, tcmNote
        ) VALUES (
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?
        )"""
        jdbcx.update(sqlInsert,
                newId, client.created.toSqlTimestamp(), client.firstName, client.lastName, client.nickName,
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city,
                client.wantReceiveDoodleMails, client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.textMainObjective, client.textSymptoms, client.textFiveElements, client.textSyndrom, client.tcmNote
        )
        return client.copy(
                id = newId,
                picture = client.gender.defaultImage
        )
    }

    override fun updateWithoutPicture(client: Client) {
        log.debug("update(client={})", client)
        client.ensurePersisted()
        jdbcx.updateSingle("""
                UPDATE $TABLE SET
                    state = ?, firstName = ?, lastName = ?, nickName = ?,
                    mail = ?, phone = ?, street = ?, zipCode = ?, city = ?,
                    wantReceiveDoodleMails = ?, birthday = ?, gender_enum = ?, countryOfOrigin = ?, origin = ?,
                    relationship_enum = ?, job = ?, children = ?, hobbies = ?, note = ?,
                    textImpression = ?, textMedical = ?, textComplaints = ?, textPersonal = ?, textObjective = ?,
                    mainObjective = ?, symptoms = ?, elements = ?, syndrom = ?, tcmNote = ?
                WHERE id = ?""",
                client.state.sqlCode, client.firstName, client.lastName, client.nickName,
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city,
                client.wantReceiveDoodleMails,client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.textMainObjective, client.textSymptoms, client.textFiveElements, client.textSyndrom, client.tcmNote,
                // no picture or cprops
                client.id!!)
    }

    override fun changePicture(client: Client) {
        client.ensurePersisted()

        jdbcx.updateSingle("UPDATE $TABLE SET picture = ? WHERE id = ?",
                client.picture.toSqlBlob(), client.id!!)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)
        client.ensurePersisted()
        jdbcx.deleteSingle("DELETE FROM $TABLE WHERE id = ?", client.id!!)
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
                ClientState.parseSqlCode(rs.getString("state")),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("nickName"),
                Contact(
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getString("street"),
                        rs.getString("zipCode"),
                        rs.getString("city")
                ),
                rs.getBoolean("wantReceiveDoodleMails"),
                rs.getTimestamp("birthday").nullOrWith2<Timestamp?, DateTime?>(::DateTime),
                gender,
                rs.getString("countryOfOrigin"),
                rs.getString("origin"),
                Relationship.parseSqlCode(rs.getString("relationship_enum")),
                rs.getString("job"),
                rs.getString("children"),
                rs.getString("hobbies"),
                rs.getString("note"),

                rs.getString("textImpression"),
                rs.getString("textMedical"),
                rs.getString("textComplaints"),
                rs.getString("textPersonal"),
                rs.getString("textObjective"),

                rs.getString("mainObjective"),
                rs.getString("symptoms"),
                rs.getString("elements"),
                rs.getString("syndrom"),

                rs.getString("tcmNote"),
                readFromBlob(rs.getBlob("picture"), gender),
                CProps.empty // will be loaded by higher-leveled service layer, who combines this with other repo's result

        )
    }


private fun readFromBlob(blob: Blob?, gender: Gender): MyImage {
    val buffered = blob?.toBufferedImage()
    if (buffered != null) {
        log.trace("Loading image for client from database BLOB.")
        // Files.write(blob.toByteArray(), File("readFromBlob.jpg"))
        return buffered.toMyImage()
    }
    log.trace("No picture stored for client, returning default image based on gender.")
    return gender.defaultImage
}


