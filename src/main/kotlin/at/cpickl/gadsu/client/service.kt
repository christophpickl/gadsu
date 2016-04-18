package at.cpickl.gadsu.client

import at.cpickl.gadsu.persistence.JdbcX
import at.cpickl.gadsu.treatment.TreatmentDeletedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject


interface ClientService {
    fun delete(client: Client)
}

class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val treatmentRepo: TreatmentRepository,
        private val jdbcx: JdbcX,
        private val bus: EventBus

) : ClientService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun delete(client: Client) {
        jdbcx.transactionSafe {

            treatmentRepo.findAllFor(client).forEach {
                treatmentRepo.delete(it)
                bus.post(TreatmentDeletedEvent(it))
            }

            clientRepo.delete(client)
            bus.post(ClientDeletedEvent(client))
        }
    }

}
