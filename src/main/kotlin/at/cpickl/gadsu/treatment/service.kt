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
        _delete(treatment, true)
    }

    override fun deleteAllFor(client: Client) {
        log.debug("deleteAllFor(client={})", client)

        jdbcx.transactionSafe {
            findAllFor(client).forEach {
                _delete(it, false) // no need for recalculation of numbers as all of them get deleted anyway
            }
        }
    }

    override fun calculateNextNumber(client: Client): Int {
        return repository.countAllFor(client) + 1
    }

    private fun _delete(treatment: Treatment, numberRecalculationEnabled: Boolean) {
        repository.delete(treatment)
        if (numberRecalculationEnabled) {
            repository.recalculateNumbers(treatment)
        }
        bus.post(TreatmentDeletedEvent(treatment))
    }

}
