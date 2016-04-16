package at.cpickl.gadsu.report

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory
import javax.inject.Inject

class CreateProtocolEvent(val client: Client) : UserEvent()

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

class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
    }
}
