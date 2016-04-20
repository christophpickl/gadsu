package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.Clock
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface TreatmentService {

    fun insert(treatmentToSave: Treatment): Treatment

    fun update(treatment: Treatment)

    fun delete(treatment: Treatment)

    fun calculateNextNumber(client: Client): Int

}

class TreatmentServiceImpl @Inject constructor(
        private val repository: TreatmentRepository,
        private val clock: Clock
) : TreatmentService {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun insert(treatmentToSave: Treatment): Treatment {
        // i dont think number needs to be manipulated here, nope ...
        return repository.insert(treatmentToSave.copy(created = clock.now()))
    }

    override fun update(treatment: Treatment) {
        repository.update(treatment)
    }

    override fun delete(treatment: Treatment) {
        repository.delete(treatment)
        repository.recalculateNumbers(treatment)
    }

    override fun calculateNextNumber(client: Client): Int {
        return repository.countAllFor(client) + 1
    }

}