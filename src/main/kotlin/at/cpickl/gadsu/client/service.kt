package at.cpickl.gadsu.client

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.props.ClientPropsRepository
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.treatment.TreatmentService
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject


interface ClientService {

    fun findAll(): List<Client>

    fun insertOrUpdate(client: Client)// needed??? : Client

    fun savePicture(client: Client)

    fun delete(client: Client)

    fun deleteImage(client: Client)

}


class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val propsRepo: ClientPropsRepository,
        private val treatmentService: TreatmentService,
        private val jdbcx: Jdbcx,
        private val bus: EventBus,
        private val clock: Clock,
        private val currentClient: CurrentClient

) : ClientService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll(): List<Client> {
        // FIXME enhance with client props data
        return clientRepo.findAll()
    }

    override fun savePicture(client: Client) {
        clientRepo.changePicture(client)
    }

    override fun insertOrUpdate(client: Client) {
        log.info("insertOrUpdate(client={})", client)

        if (client.yetPersisted) {
            clientRepo.updateWithoutPicture(client)

            val dispatchClient: Client
            if (client.picture.isUnsavedDefaultPicture) {
                // if showing the default picture, check the gender which might have been updated and set new default image
                dispatchClient = client.copy(picture = client.gender.defaultImage)
            } else {
                dispatchClient = client
            }
            bus.post(ClientUpdatedEvent(dispatchClient))
            return
        }

        insertClient(client)
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
        val changedClient = client.copy(picture = client.gender.defaultImage)
        clientRepo.changePicture(changedClient)
        currentClient.data = changedClient
    }

    private fun insertClient(client: Client) {
        val toBeInserted = client.copy(created = clock.now())

        log.trace("Going to insert: {}", toBeInserted)
        val savedClient = clientRepo.insertWithoutPicture(toBeInserted)
        // FIXME also save client props
        log.trace("Dispatching ClientCreatedEvent: {}", savedClient)

        @Suppress("SENSELESS_COMPARISON")
        if (savedClient === null) throw GadsuException("Impossible state most likely due to wrong test mock setup! Inserted to repo: $toBeInserted")

        bus.post(ClientCreatedEvent(savedClient))
    }

}
