package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.ensureNotPersisted
import at.cpickl.gadsu.persistence.toSqlTimestamp
import at.cpickl.gadsu.service.IdGenerator
import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import com.google.inject.Inject
import org.slf4j.LoggerFactory

interface MultiProtocolRepository {

    fun insert(protocol: MultiProtocol): MultiProtocol

    fun deleteAllTreatmentRefsFor(client: Client)

}


class MultiProtocolJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : MultiProtocolRepository {
    companion object {

        val TABLE = "multiprotocol"

        val TABLE_KEYS = "multiprotocol2treatment"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun insert(protocol: MultiProtocol): MultiProtocol {
        log.trace("save(protocol={})", protocol)
        protocol.ensureNotPersisted()

        val idProtocol = idGenerator.generate()
        jdbcx.transactionSafe {
            jdbcx.update("INSERT INTO $TABLE (id, created, description) VALUES (?, ?, ?)", idProtocol, protocol.created.toSqlTimestamp(), protocol.description)
            protocol.treatmentIds.forEach {
                jdbcx.update("INSERT INTO $TABLE_KEYS (id_multiprotocol, id_treatment) VALUES (?, ?)", idProtocol, it)
            }
        }

        return protocol.copy(id = idProtocol)
    }

    override fun deleteAllTreatmentRefsFor(client: Client) {
        val deleted = jdbcx.update("DELETE FROM $TABLE_KEYS WHERE $TABLE_KEYS.id_treatment IN (SELECT ${TreatmentJdbcRepository.TABLE}.id FROM ${TreatmentJdbcRepository.TABLE} WHERE ${TreatmentJdbcRepository.TABLE}.id_client = ?)", client.id!!)
        log.trace("Deleted $deleted treatment references for given client.")
    }

}
