package at.cpickl.gadsu.client

import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.xprops.XPropsService
import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.isNotValidMail
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.TreatmentService
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


interface ClientService {


    fun findAll(filterState: ClientState? = null): List<Client>

    /**
     * throws InvalidMailException
     */
    fun insertOrUpdate(client: Client): Client// return type needed for development reset data (only?!)

    fun delete(client: Client)

    fun savePicture(client: Client)
    fun deletePicture(client: Client)

    fun findById(id: String): Client
}

class InvalidMailException(message: String) : RuntimeException(message)

@Logged
open class ClientServiceImpl @Inject constructor(
        private val clientRepo: ClientRepository,
        private val xpropsService: XPropsService,
        private val treatmentService: TreatmentService,
        private val appointmentService: AppointmentService,
        private val jdbcx: Jdbcx,
        private val bus: EventBus,
        private val clock: Clock,
        private val currentClient: CurrentClient

) : ClientService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAll(filterState: ClientState?): List<Client> {
        return clientRepo.findAll(filterState).map {
            // MINOR performance improvement: dont ask DB for each client, but rather exec a bulk operation
            it.copy(cprops = xpropsService.read(it))
        }
    }

    override fun findById(id: String): Client {
        return clientRepo.findById(id)
    }

    override fun insertOrUpdate(client: Client): Client {
        log.info("insertOrUpdate(client={})", client)
        validateClient(client)
        if (!client.yetPersisted) {
            return insertClient(client)
        }
        return updateClient(client)
    }

    private fun validateClient(client: Client) {
        if (client.contact.mail.isNotBlank() && client.contact.mail.isNotValidMail()) {
            throw InvalidMailException("Invalid email address: '${client.contact.mail}'!")
        }
    }

    private fun insertClient(client: Client): Client {
        val toBeInserted = client.copy(created = clock.now())

        log.trace("Going to insert: {}", toBeInserted)

        val savedClient = jdbcx.transactionSafeAndReturn {
            val tmpClient = clientRepo.insertWithoutPicture(toBeInserted)
            xpropsService.update(tmpClient)
            tmpClient
        }

        @Suppress("SENSELESS_COMPARISON")
        if (savedClient === null) throw GadsuException("Impossible state most likely due to wrong test mock setup! Inserted to repo: $toBeInserted")

        log.trace("Dispatching ClientCreatedEvent: {}", savedClient)
        bus.post(ClientCreatedEvent(savedClient))
        return savedClient
    }


    @Subscribe open fun onClientChangeState(event: ClientChangeStateEvent) {
        val client = currentClient.data
        if (client.state == event.newState) {
            throw IllegalArgumentException("Client's state is already set to '${event.newState}' for client: $client")
        }
        updateClient(client.copy(state = event.newState))
    }

    private fun updateClient(client: Client): Client {
        jdbcx.transactionSafe {
            clientRepo.updateWithoutPicture(client)
            xpropsService.update(client)
        }

        val dispatchClient: Client = if (client.picture.isUnsavedDefaultPicture) {
            // if showing the default picture, check the gender which might have been updated and set new default image
            client.copy(picture = client.gender.defaultImage)
        } else {
            client
        }
        bus.post(ClientUpdatedEvent(dispatchClient))
        return dispatchClient
    }

    override fun savePicture(client: Client) {
        clientRepo.changePicture(client)
    }

    override fun delete(client: Client) {
        log.debug("delete(client={})", client)

        jdbcx.transactionSafe {
            // cascade delete
            xpropsService.deleteAll(client)
            treatmentService.deleteAll(client)
            appointmentService.deleteAll(client)

            clientRepo.delete(client)
            bus.post(ClientDeletedEvent(client))
        }
    }

    override fun deletePicture(client: Client) {
        // the picture will not be stored anyway, but for dispatching useful
        val changedClient = client.copy(picture = client.gender.defaultImage)
        clientRepo.changePicture(changedClient)
        currentClient.data = changedClient
    }

}
