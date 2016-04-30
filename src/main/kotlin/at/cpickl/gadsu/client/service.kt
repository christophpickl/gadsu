package at.cpickl.gadsu.client

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.props.PropsService
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

    fun insertOrUpdate(client: Client): Client // return type needed for development reset data (only?!)

    fun savePicture(client: Client)

    fun delete(client: Client)

    fun deleteImage(client: Client)

}


class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val propsService: PropsService,
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

    override fun insertOrUpdate(client: Client): Client {
        log.info("insertOrUpdate(client={})", client)

        if (client.yetPersisted) {
            jdbcx.transactionSafe {
                clientRepo.updateWithoutPicture(client)
                // TODO the propsService call is duplicate from insert
                propsService.update(client)
            }

            val dispatchClient: Client
            if (client.picture.isUnsavedDefaultPicture) {
                // if showing the default picture, check the gender which might have been updated and set new default image
                dispatchClient = client.copy(picture = client.gender.defaultImage)
            } else {
                dispatchClient = client
            }
            bus.post(ClientUpdatedEvent(dispatchClient))
            return dispatchClient
        }

        return insertClient(client)
    }

    private fun insertClient(client: Client): Client {
        val toBeInserted = client.copy(created = clock.now())

        log.trace("Going to insert: {}", toBeInserted)

        val savedClient = jdbcx.transactionSafeAndReturn {
            val tmpClient = clientRepo.insertWithoutPicture(toBeInserted)
            propsService.update(tmpClient)
            tmpClient
        }

        @Suppress("SENSELESS_COMPARISON")
        if (savedClient === null) throw GadsuException("Impossible state most likely due to wrong test mock setup! Inserted to repo: $toBeInserted")

        log.trace("Dispatching ClientCreatedEvent: {}", savedClient)
        bus.post(ClientCreatedEvent(savedClient))
        return savedClient
    }

    override fun savePicture(client: Client) {
        clientRepo.changePicture(client)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)

        jdbcx.transactionSafe {
            // cascade delete
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

}
