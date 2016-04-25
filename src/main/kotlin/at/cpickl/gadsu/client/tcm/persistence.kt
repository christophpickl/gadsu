package at.cpickl.gadsu.client.tcm

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.IdGenerator
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface ClientTcmDataRepository {

    fun insert(data: TcmData)

}

class ClientTcmDataSpringJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : ClientTcmDataRepository {
    companion object {
        val TABLE = "client_tcm_data"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun insert(data: TcmData) {
        log.debug("insert(data={})", data)

        jdbcx.transactionSafe {
            data.entries.entries.forEach {
                val newId = idGenerator.generate()
                jdbcx.update("INSERT INTO ${TABLE} (id, id_client, key, val) VALUES(?, ?, ?, ?)",
                    newId, data.clientId, it.key.sqlValue, it.toSqlValue())
            }
        }
    }

    fun findAll() {

//        jdbcx.query()
    }
}
