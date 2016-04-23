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
                rows = treatments.map { TreatmentReportData(it.number, "some note", it.date) }
                /*listOf(TreatmentReportData(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque elementum eros luctus, sagittis tellus vel, vestibulum sem. Morbi semper sit amet risus vel tristique. Vestibulum eleifend ante est, sed luctus massa lobortis in. Integer iaculis neque in eros tempor, vitae efficitur quam elementum. Curabitur laoreet leo sed dui commodo blandit. Suspendisse ut dolor sollicitudin mi venenatis vulputate quis quis ipsum. Morbi nec consectetur justo. Sed luctus leo non felis suscipit venenatis. Proin molestie orci blandit, dapibus risus ac, facilisis sem. Nullam hendrerit lacus ut mi lobortis, at malesuada quam facilisis. Morbi at elit eu ex pellentesque commodo non sed augue. Aenean ultrices dui lacus, eget vestibulum turpis vestibulum non. Suspendisse nec egestas felis. Aliquam tristique tincidunt mauris quis elementum. Suspendisse potenti. Sed vulputate volutpat dictum.", DateTime.now()),
                        TreatmentReportData(2, "something boring", DateTime.now().plusDays(1)),
                        TreatmentReportData(3, "a little bit better", DateTime.now().plusDays(4)),
                        TreatmentReportData(4, "very goooood", DateTime.now().plusDays(42)),
                        TreatmentReportData(5, "not good", DateTime.now().plusDays(43)),
                        TreatmentReportData(6, "final one", DateTime.now().plusDays(45))
                )*/
        )
        // TODO @REPORT VIEW - show progress dialog and run background swing worker

        protocolGenerator.view(report)

//        val target = File("")
        // check if target exists
//        protocolGenerator.savePdfTo(report, target, forceOverwrite = true)
    }
}
