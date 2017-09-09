package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.global.DUMMY_CREATED
import at.cpickl.gadsu.service.toDateTime
import at.cpickl.gadsu.testinfra.*
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import at.cpickl.gadsu.treatment.TreatmentRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.springframework.jdbc.core.RowMapper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.sql.ResultSet


data class MultiProtocolRaw(
        val id: String,
        val created: DateTime,
        val description: String
)

object MultiProtocolRawMapper : RowMapper<MultiProtocolRaw> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = MultiProtocolRaw(rs.getString("id"), rs.getTimestamp("created").toDateTime(), rs.getString("description"))
}

data class MultiProtocol2Treatment(
        val idMultiprotocol: String,
        val idTreatment: String
)

object MultiProtocol2TreatmentMapper : RowMapper<MultiProtocol2Treatment> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = MultiProtocol2Treatment(rs.getString("id_multiprotocol"), rs.getString("id_treatment"))
}


@Test(groups = arrayOf("hsqldb"))
class MultiProtocolJdbcRepositoryTest : HsqldbTest() {

    private lateinit var testee: MultiProtocolRepository
    private lateinit var treatmentRepo: TreatmentRepository

    @BeforeMethod
    fun setUp() {
        testee = MultiProtocolJdbcRepository(jdbcx, idGenerator)
        treatmentRepo = TreatmentJdbcRepository(jdbcx, idGenerator)
    }

    fun insertMultiprotocol() {
        jdbcx.assertEmptyTable(TABLE_MULTIPROTOCOL)
        jdbcx.assertEmptyTable(TABLE_MULTIPROTOCOL_KEYS)

        val client = insertClientViaRepo()
        val treat1 = insertTreatment(Treatment.unsavedValidInstance(client), "ID_TREAT_1")
        val treat2 = insertTreatment(Treatment.unsavedValidInstance(client), "ID_TREAT_2")
        val treat1id = treat1.id!!
        val treat2id = treat2.id!!

        val goingToInsert = MultiProtocol(null, DUMMY_CREATED, "myDescription", listOf(treat1id, treat2id))
        val inserted = testee.insert(goingToInsert)
        val idMultiprotocol = inserted.id!!

        assertRows(TABLE_MULTIPROTOCOL, MultiProtocolRawMapper, MultiProtocolRaw(idMultiprotocol, goingToInsert.created, goingToInsert.description))
        assertRows(TABLE_MULTIPROTOCOL_KEYS, MultiProtocol2TreatmentMapper,
                MultiProtocol2Treatment(idMultiprotocol, treat1id),
                MultiProtocol2Treatment(idMultiprotocol, treat2id)
        )
    }

    fun deleteTreatmentRefs() {
        val client = insertClientViaRepo()
        val treat1 = insertTreatment(Treatment.unsavedValidInstance(client), "ID_TREAT_1")
        testee.insert(MultiProtocol(null, DUMMY_CREATED, "myDescription", listOf(treat1.id!!)))
        assertThat(jdbcx.countTableEntries(TABLE_MULTIPROTOCOL), equalTo(1))
        assertThat(jdbcx.countTableEntries(TABLE_MULTIPROTOCOL_KEYS), equalTo(1))

        testee.deleteTreatmentRefs(treat1)
        assertThat(jdbcx.countTableEntries(TABLE_MULTIPROTOCOL), equalTo(1))
        jdbcx.assertEmptyTable(TABLE_MULTIPROTOCOL_KEYS)
    }

    fun `hasBeenProtcolized, nope`() {
        assertThat(testee.hasBeenProtocolizedYet(Treatment.savedValidInstance("client1", "treatment1")), equalTo(false))
    }

    fun `hasBeenProtcolized, yep`() {
        val client = insertClientViaRepo()
        val treat1 = insertTreatment(Treatment.Companion.unsavedValidInstance(client), "ID_TREAT_1")
        testee.insert(MultiProtocol(null, DUMMY_CREATED, "myDescription", listOf(treat1.id!!)))
        assertThat(testee.hasBeenProtocolizedYet(treat1), equalTo(true))
    }

}
