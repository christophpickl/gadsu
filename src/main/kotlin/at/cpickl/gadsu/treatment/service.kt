package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.persistence.Jdbcx
import at.cpickl.gadsu.persistence.ensurePersisted
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.treatment.dyn.DynTreatmentService
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import javax.inject.Inject

interface TreatmentService {

    fun findAllFor(client: Client): List<Treatment>

    fun findFirstFor(client: Client): Treatment?

    fun insert(treatmentToSave: Treatment): Treatment

    fun update(treatment: Treatment)

    fun delete(treatment: Treatment)

    fun deleteAll(client: Client)

    fun calculateNextNumber(client: Client): Int

    fun prevAndNext(pivot: Treatment): Pair<Treatment?, Treatment?>

    fun countAllFor(client: Client): Int // delegate to repo
}

class TreatmentServiceImpl @Inject constructor(
        private val treatmentRepository: TreatmentRepository,
        private val dynTreatmentService: DynTreatmentService,
        private val meridiansService: TreatmentMeridiansService,
        private val multiProtocolRepository: MultiProtocolRepository,
        private val jdbcx: Jdbcx,
        private val bus: EventBus,
        private val clock: Clock
) : TreatmentService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun findAllFor(client: Client): List<Treatment> {
        return treatmentRepository.findAllFor(client).map { enrichTreatment(it)!! }
    }


    override fun findFirstFor(client: Client): Treatment? {
        return enrichTreatment(treatmentRepository.findFirstFor(client.id!!))
    }

    override fun prevAndNext(pivot: Treatment): Pair<Treatment?, Treatment?> {
        pivot.ensurePersisted()

        val treatments = treatmentRepository.findAllForRaw(pivot.clientId).sortedBy { it.number }
        val index = treatments.indexOfFirst { it.id == pivot.id }
        if (index == -1) throw IllegalStateException("Pivot treatment expected to be persisted yet! $pivot")

        val first = enrichTreatment(if (index == 0) null else treatments[index - 1])
        val second = enrichTreatment(if (index == treatments.size - 1) null else treatments[index + 1])
        return Pair(first, second)
    }

    override fun insert(treatmentToSave: Treatment): Treatment {
        // i dont think number needs to be manipulated here, nope ...
        val savedTreatment = treatmentRepository.insert(treatmentToSave.copy(created = clock.now()))
        dynTreatmentService.update(savedTreatment)
        meridiansService.update(savedTreatment)
        return enrichTreatment(savedTreatment)!!
    }

    override fun update(treatment: Treatment) {
        dynTreatmentService.update(treatment)
        meridiansService.update(treatment)

        treatmentRepository.update(treatment)
    }

    override fun delete(treatment: Treatment) {
        _delete(treatment)
    }

    override fun deleteAll(client: Client) {
        log.debug("delete(client={})", client)

        jdbcx.transactionSafe {
            findAllFor(client).forEach {
                _delete(it)
            }
        }
    }

    override fun countAllFor(client: Client) = treatmentRepository.countAllFor(client)

    override fun calculateNextNumber(client: Client): Int {
        val maxNumber = treatmentRepository.calculateMaxNumberUsed(client) ?: return 1
        return maxNumber + 1
    }

    private fun enrichTreatment(treatment: Treatment?): Treatment? {
        if (treatment == null) {
            return null
        }
        val treatId = treatment.id!!
        val dynTreats = dynTreatmentService.find(treatId)
        val treatedMeridians = meridiansService.find(treatId)
        return treatment.copy(dynTreatments = dynTreats, treatedMeridians = treatedMeridians)
    }

    private fun _delete(treatment: Treatment) {
        log.debug("_delete(treatment)")
        val beenProtocolized = multiProtocolRepository.hasBeenProtocolizedYet(treatment) // has to happen before multiProtocolRepository.delete(treat)

        multiProtocolRepository.deleteTreatmentRefs(treatment)
        dynTreatmentService.delete(treatment)
        meridiansService.delete(treatment)
        treatmentRepository.delete(treatment)

        bus.post(TreatmentDeletedEvent(treatment, beenProtocolized))
    }

}

interface TreatmentMeridiansService {

    fun find(treatmentId: String): List<Meridian>
    fun update(treatment: Treatment)
    fun delete(treatment: Treatment)

}

class TreatmentMeridiansServiceImpl @Inject constructor(
        private val meridiansRepository: TreatmentMeridiansRepository
) : TreatmentMeridiansService {

    override fun find(treatmentId: String) = meridiansRepository.find(treatmentId)

    override fun update(treatment: Treatment) {
        delete(treatment)
        meridiansRepository.insert(treatment.id!!, treatment.treatedMeridians)
    }

    override fun delete(treatment: Treatment) {
        meridiansRepository.delete(treatment.id!!)
    }

}
