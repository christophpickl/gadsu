package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface TreatmentService {

    fun insert(treatmentToSave: Treatment, client: Client): Treatment

}

class TreatmentServiceImpl @Inject constructor(
        private val repository: TreatmentRepository
) : TreatmentService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun insert(treatmentToSave: Treatment, client: Client): Treatment {
        return repository.insert(treatmentToSave, client)
    }

}