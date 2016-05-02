package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Provider
import org.slf4j.LoggerFactory
import javax.inject.Inject


class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
        bind(ProtocolGenerator::class.java).to(JasperProtocolGenerator::class.java)
        bind(JasperEngine::class.java).to(JasperEngineImpl::class.java)

    }
}

/**
 * User requested to generate a new protocol report.
 */
class CreateProtocolEvent() : UserEvent()


class ReportException(message: String, cause: Exception? = null) : GadsuException(message, cause)

@Logged
open class ReportController @Inject constructor(
        private val treatmentRepo: TreatmentRepository,
        private val protocolGenerator: ProtocolGenerator,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val preferences: Provider<PreferencesData>
        ) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onCreateProtocolEvent(event: CreateProtocolEvent) {
        val client = currentClient.data

        val treatments = treatmentRepo.findAllFor(client)

        val report = ProtocolReportData(
                author = preferences.get().username,
                printDate = clock.now(),
                client = ClientReportData(
                        fullName = client.fullName,
                        children = client.children,
                        job = client.job,
                        picture = client.picture.toReportRepresentation()
                ),
                rows = treatments.map {
                    TreatmentReportData(it.number, it.note, it.date)
                }.sortedBy { it.number } // we need it ascending (but internally set descendant for list view)
        )
        // TODO @REPORT VIEW - show progress dialog and run background swing worker

        protocolGenerator.view(report)

//        val target = File("")
        // check if target exists
//        protocolGenerator.savePdfTo(report, target, forceOverwrite = true)
    }
}
