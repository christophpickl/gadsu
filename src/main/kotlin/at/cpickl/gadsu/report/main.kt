package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject


class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
        bind(ProtocolGenerator::class.java).to(JasperProtocolGenerator::class.java)
        bind(JasperEngine::class.java).to(JasperEngineImpl::class.java)

    }
}

class ReportException(message: String, cause: Exception? = null) : GadsuException(message, cause)

class ReportController @Inject constructor(
        private val treatmentRepo: TreatmentRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onCreateProtocolEvent(event: CreateProtocolEvent) {
        log.debug("onCreateProtocolEvent(event.client={})", event.client)
        val client = event.client

        val treatments = treatmentRepo.findAllFor(client)
        println("XXXXX print protocolo for ${client.fullName} for following treatments: $treatments")
    }
}
