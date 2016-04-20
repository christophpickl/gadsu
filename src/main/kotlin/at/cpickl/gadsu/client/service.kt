package at.cpickl.gadsu.client

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.treatment.TreatmentService
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject


interface ClientService {
    fun delete(client: Client)
}

class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val treatmentService: TreatmentService,
        private val jdbcx: Jdbcx,
        private val bus: EventBus

) : ClientService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)
        jdbcx.transactionSafe {
            treatmentService.deleteAllFor(client)

            clientRepo.delete(client)
            bus.post(ClientDeletedEvent(client))
        }
    }

}
