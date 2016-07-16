package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.IdGenerator
import com.google.inject.Inject
import org.slf4j.LoggerFactory

interface MultiProtocolRepository {

}


class MultiProtocolJdbcRepository @Inject constructor(
        private val jdbcx: Jdbcx,
        private val idGenerator: IdGenerator
) : MultiProtocolRepository {

    companion object {
        val TABLE = "multiprotocol"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    fun findAll() {

    }
}
