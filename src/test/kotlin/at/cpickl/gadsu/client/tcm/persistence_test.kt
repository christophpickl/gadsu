package at.cpickl.gadsu.client.tcm

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.persistence.PersistenceErrorCode
import at.cpickl.gadsu.persistence.PersistenceException
import at.cpickl.gadsu.testinfra.HsqldbTest
import at.cpickl.gadsu.testinfra.SequencedTestableIdGenerator
import org.springframework.jdbc.core.RowMapper
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.sql.ResultSet

@Test(groups = arrayOf("hsqldb"))
class ClientTcmDataRepositoryTest : HsqldbTest() {

    private var testee: ClientTcmDataRepository = ClientTcmDataSpringJdbcRepository(nullJdbcx(), idGenerator)

    @BeforeMethod
    fun initState() {
        idGenerator = SequencedTestableIdGenerator()
        testee = ClientTcmDataSpringJdbcRepository(jdbcx(), idGenerator)
    }

    fun `insert, should be persisted`() {
        val client = insertClient(Client.unsavedValidInstance())
        testee.insert(TcmData(
                clientId = client.id!!,
                entries = TcmDataEntries(
                        entries = listOf(
                                TcmDataStringEntry(null, TcmDataKey.Zufriedenheit_Leben, "testValueLife") as TcmDataEntry<Any>,
                                TcmDataStringEntry(null, TcmDataKey.Zufriedenheit_Sex, "testValueSex") as TcmDataEntry<Any>
                        )
                )
        ))

        val rawRows = jdbcx().jdbc.query("SELECT * FROM ${ClientTcmDataSpringJdbcRepository.TABLE}", object : RowMapper<TcmDataRow> {
            override fun mapRow(rs: ResultSet, rowNum: Int): TcmDataRow {
                val id = rs.getString("id")
                val clientId = rs.getString("id_client")
                val rawKey = rs.getString("key")
                val rawVal = rs.getString("val")
                val key = TcmDataKey.bySqlValue(rawKey) ?: throw PersistenceException("Unhandled key '$rawKey'!", PersistenceErrorCode.UNMAPPABLE_ENUM)
                //                val type = TcmDataType.bySqlValue(rawType)
                return TcmDataRow(id, clientId, key, rawVal)
            }

        })
        println("fuchur")

        rawRows.forEach {
            println(it)
            it.key.toTcmDataEntryBySqlValue(it)
        }
    }

}

private fun TcmDataKey.toTcmDataEntryBySqlValue(row: TcmDataRow): TcmDataEntry<Any> {

    val properTypedValue: Any
    properTypedValue = this.type.onCallback(object: TcmDataType.Callback<Any> {
        override fun onString(type: TcmDataType): Any {
            return row.rawSqlValue
        }
    })


}

private data class TcmDataRow(val id: String, val clientId: String, val key: TcmDataKey, val rawSqlValue: String)
