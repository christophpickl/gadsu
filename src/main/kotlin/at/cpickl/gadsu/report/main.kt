package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CPropTypeCallback
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.TreatmentService
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

class CreateMultiProtocolEvent() : UserEvent()



class ReportException(message: String, cause: Exception? = null) : GadsuException(message, cause)

@Logged
open class ReportController @Inject constructor(
        private val clientService: ClientService,
        private val treatmentService: TreatmentService,
        private val protocolGenerator: ProtocolGenerator,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val preferences: Provider<PreferencesData>
        ) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onCreateProtocolEvent(event: CreateProtocolEvent) {
        val client = currentClient.data

        val treatments = treatmentService.findAllFor(client)

        val report = ProtocolReportData(
                author = preferences.get().username,
                printDate = clock.now(),
                client = ClientReportData(
                        fullName = client.fullName,
                        children = client.children,
                        job = client.job,
                        picture = client.picture.toReportRepresentation(),
                        cprops = CPropsComposer.compose(client)
                ),
                rows = treatments.map {
                    TreatmentReportData(it.number, it.note, it.date)
                }.sortedBy { it.number } // we need it ascending (but internally set descendant for list view)
        )

        protocolGenerator.view(report)

//        val target = File("")
        // check if target exists
//        protocolGenerator.savePdfTo(report, target, forceOverwrite = true)
    }

    @Subscribe open fun onCreateMultiProtocolEvent(event: CreateMultiProtocolEvent) {
        val author = preferences.get().username
        val printDate = clock.now()
        val cover = MultiProtocolCoverData(printDate, author)

        val protocols = clientService.findAll().map {
            val picture = it.picture.toReportRepresentation()
            val clientData = ClientReportData(it.fullName, it.children.nullIfEmpty(), it.job.nullIfEmpty(), picture, CPropsComposer.compose(it))
            val rows = treatmentService.findAllFor(it).map {
                TreatmentReportData(it.number, it.note.nullIfEmpty(), it.date)
            }
            ProtocolReportData(author, printDate, clientData, rows)
        }.filter { it.rows.isNotEmpty() }.toList()

        MultiProtocolGeneratorImpl().generate("myTarget.pdf", cover, protocols)
        println("saved to: myTarget.pdf")
    }
}

object CPropsComposer {
    fun compose(client: Client) : String? {
        if (client.cprops.isEmpty()) {
            return null
        }
        return client.cprops.map { it.onType(object : CPropTypeCallback<String>{
            override fun onEnum(cprop: CPropEnum): String {
                return "Sein ${cprop.label} ist verbunden mit ${cprop.clientValue.map { it.label }.joinToString(", ")}."
            }

        } ) }.joinToString(" ")
    }
}
