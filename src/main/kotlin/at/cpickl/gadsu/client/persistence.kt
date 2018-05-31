package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.global.GadsuException
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
import com.google.inject.Inject
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import java.sql.Blob

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
            id, created, firstName, lastName, nickNameInt, nickNameExt,
            mail, phone, street, zipCode, city, knownBy,
            dsgvoAccepted, wantReceiveMails, birthday, gender_enum, countryOfOrigin, origin,
            relationship_enum, job, children, hobbies, note,
            textImpression, textMedical, textComplaints, textPersonal, textObjective,
            mainObjective, symptoms, syndrom, tcmNote,
            category, donation,
            yyTendency, textYinYang, elementTendency, elements, textWood, textFire, textEarth, textMetal, textWater
        ) VALUES (
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?, ?,
            ?, ?, ?, ?,
            ?, ?,
            ?, ?, ?, ?, ?, ?, ?, ?, ?
        )"""
        jdbcx.update(sqlInsert,
                newId, client.created.toSqlTimestamp(), client.firstName, client.lastName, client.nickNameInt, client.nickNameExt,
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city, client.knownBy,
                client.dsgvoAccepted, client.wantReceiveMails, client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.textMainObjective, client.textSymptoms, client.textSyndrom, client.tcmNote,
                client.category.sqlCode, client.donation.sqlCode,
                client.yyTendency.sqlCode, client.textYinYang, client.elementTendency.sqlCode, client.textFiveElements, client.textWood, client.textFire, client.textEarth, client.textMetal, client.textWater
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
                    state = ?, firstName = ?, lastName = ?, nickNameInt = ?, nickNameExt = ?,
                    mail = ?, phone = ?, street = ?, zipCode = ?, city = ?, knownBy = ?,
                    dsgvoAccepted = ?, wantReceiveMails = ?, birthday = ?, gender_enum = ?, countryOfOrigin = ?, origin = ?,
                    relationship_enum = ?, job = ?, children = ?, hobbies = ?, note = ?,
                    textImpression = ?, textMedical = ?, textComplaints = ?, textPersonal = ?, textObjective = ?,
                    mainObjective = ?, symptoms = ?, syndrom = ?, tcmNote = ?,
                    category = ?, donation = ?,
                    yyTendency = ?, textYinYang = ?, elementTendency = ?, elements = ?, textWood = ?, textFire = ?, textEarth = ?, textMetal = ?, textWater = ?
                WHERE id = ?""",
                client.state.sqlCode, client.firstName, client.lastName, client.nickNameInt, client.nickNameExt,
                client.contact.mail, client.contact.phone, client.contact.street, client.contact.zipCode, client.contact.city, client.knownBy,
                client.dsgvoAccepted, client.wantReceiveMails,client.birthday?.toSqlTimestamp(), client.gender.sqlCode, client.countryOfOrigin, client.origin,
                client.relationship.sqlCode, client.job, client.children, client.hobbies, client.note,
                client.textImpression, client.textMedical, client.textComplaints, client.textPersonal, client.textObjective,
                client.textMainObjective, client.textSymptoms, client.textSyndrom, client.tcmNote,
                client.category.sqlCode, client.donation.sqlCode,
                client.yyTendency.sqlCode, client.textYinYang, client.elementTendency.sqlCode, client.textFiveElements, client.textWood, client.textFire, client.textEarth, client.textMetal, client.textWater,
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
    get() = RowMapper { rs, _ ->
        log.trace("Transforming database row for client with first name: '{}'", rs.getString("firstName"))
        val gender = Gender.Enum.parseSqlCode(rs.getString("gender_enum"))
        Client(
                id = rs.getString("id"),
                created = DateTime(rs.getTimestamp("created")),
                state = ClientState.parseSqlCode(rs.getString("state")),
                firstName = rs.getString("firstName"),
                lastName = rs.getString("lastName"),
                nickNameExt = rs.getString("nickNameExt"),
                nickNameInt = rs.getString("nickNameInt"),
                contact = Contact(
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getString("street"),
                        rs.getString("zipCode"),
                        rs.getString("city")
                ),
                knownBy = rs.getString("knownBy"),
                wantReceiveMails = rs.getBoolean("wantReceiveMails"),
                dsgvoAccepted = rs.getBoolean("dsgvoAccepted"),
                birthday = rs.getTimestamp("birthday")?.run { DateTime(this) },
                gender = gender,
                countryOfOrigin = rs.getString("countryOfOrigin"),
                origin = rs.getString("origin"),
                relationship = Relationship.Enum.parseSqlCode(rs.getString("relationship_enum")),
                job = rs.getString("job"),
                children = rs.getString("children"),
                hobbies = rs.getString("hobbies"),
                note = rs.getString("note"),

                textImpression = rs.getString("textImpression"),
                textMedical = rs.getString("textMedical"),
                textComplaints = rs.getString("textComplaints"),
                textPersonal = rs.getString("textPersonal"),
                textObjective = rs.getString("textObjective"),

                textMainObjective = rs.getString("mainObjective"),
                textSymptoms = rs.getString("symptoms"),
                textSyndrom = rs.getString("syndrom"),

                yyTendency = YinYangMaybe.Enum.parseSqlCode(rs.getString("yyTendency")),
                textYinYang = rs.getString("textYinYang"),
                elementTendency = ElementMaybe.Enum.parseSqlCode(rs.getString("elementTendency")),
                textFiveElements = rs.getString("elements"),
                textWood = rs.getString("textWood"),
                textFire = rs.getString("textFire"),
                textEarth = rs.getString("textEarth"),
                textMetal = rs.getString("textMetal"),
                textWater = rs.getString("textWater"),

                category = ClientCategory.Enum.parseSqlCode(rs.getString("category")),
                donation = ClientDonation.Enum.parseSqlCode(rs.getString("donation")),

                tcmNote = rs.getString("tcmNote"),
                picture = readFromBlob(rs.getBlob("picture"), gender),
                cprops = CProps.empty // will be loaded by higher-leveled service layer, who combines this with other repo's result
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


