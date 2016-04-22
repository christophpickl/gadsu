package at.cpickl.gadsu.client

import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.treatment.TreatmentService
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject


interface ClientService {

    fun findAll(): List<Client>

    fun savePicture(client: Client)

    fun delete(client: Client)

    fun deleteImage(client: Client)

}


class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val treatmentService: TreatmentService,
        private val jdbcx: Jdbcx,
        private val bus: EventBus,
        private val currentClient: CurrentClient

) : ClientService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll() = clientRepo.findAll()

    override fun savePicture(client: Client) {
        clientRepo.changePicture(client)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)

        jdbcx.transactionSafe {
            treatmentService.deleteAllFor(client)

            clientRepo.delete(client)
            bus.post(ClientDeletedEvent(client))
        }
    }

    override fun deleteImage(client: Client) {
        // the picture will not be stored anyway, but for dispatching useful
        val changedClient = client.copy(picture = client.defaultPictureBasedOnGender())
        clientRepo.changePicture(changedClient)
        currentClient.data = changedClient
    }

}
