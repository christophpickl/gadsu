package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface TreatmentService {

    fun findAllFor(client: Client): List<Treatment>

    fun insert(treatmentToSave: Treatment): Treatment

    fun update(treatment: Treatment)

    fun delete(treatment: Treatment)

    fun deleteAllFor(client: Client)

    fun calculateNextNumber(client: Client): Int

}

class TreatmentServiceImpl @Inject constructor(
        private val repository: TreatmentRepository,
        private val jdbcx: Jdbcx,
        private val bus: EventBus,
        private val clock: Clock
) : TreatmentService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAllFor(client: Client): List<Treatment> {
        return repository.findAllFor(client)
    }

    override fun insert(treatmentToSave: Treatment): Treatment {
        // i dont think number needs to be manipulated here, nope ...
        return repository.insert(treatmentToSave.copy(created = clock.now()))
    }

    override fun update(treatment: Treatment) {
        repository.update(treatment)
    }

    override fun delete(treatment: Treatment) {
        _delete(treatment)
    }

    override fun deleteAllFor(client: Client) {
        log.debug("deleteAllFor(client={})", client)

        jdbcx.transactionSafe {
            findAllFor(client).forEach {
                _delete(it)
            }
        }
    }

    override fun calculateNextNumber(client: Client): Int {
        val maxNumber = repository.calculateMaxNumberUsed(client) ?: return 1
        return maxNumber + 1
    }

    private fun _delete(treatment: Treatment) {
        repository.delete(treatment)
        bus.post(TreatmentDeletedEvent(treatment))
    }

}
