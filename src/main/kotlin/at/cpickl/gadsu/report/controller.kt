package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolCoverData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGenerator
import at.cpickl.gadsu.service.ChooseFile
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.nullIfEmpty
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import com.google.inject.Provider
import org.slf4j.LoggerFactory
import javax.inject.Inject


@Logged
open class ReportController @Inject constructor(
        private val clientService: ClientService,
        private val treatmentService: TreatmentService,
        private val protocolGenerator: ProtocolGenerator,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val preferences: Provider<PreferencesData>,
        private val dialogs: Dialogs,
        private val multiProtocolGenerator: MultiProtocolGenerator
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe open fun onCreateProtocolEvent(event: CreateProtocolEvent) {
        val client = currentClient.data
        val treatments = treatmentService.findAllFor(client)

        if (treatments.isEmpty()) {
            dialogs.show(
                    title = "Sammelprotokoll",
                    message = "Es konnte kein Sammelprotokoll erstellt werden, da der Klient keine Behandlungen hat.",
                    type = DialogType.WARN
            )
            return
        }

        val report = newProtocolReportData(client)
        protocolGenerator.view(report)
    }

    @Subscribe open fun onCreateMultiProtocolEvent(event: CreateMultiProtocolEvent) {
        val author = preferences.get().username
        val printDate = clock.now()
        val cover = MultiProtocolCoverData(printDate, author)

        val protocols = multiProtocolWizard()

        // TODO use preferences for recent save multi protocol report path
        ChooseFile.savePdf(
            fileTypeLabel = "Sammelprotokoll",
            onSuccess = {
                // TODO show progress bar
                multiProtocolGenerator.generate(it, cover, protocols)
                dialogs.show(
                    title = "Sammelprotokoll erstellt",
                    message = "Das Sammelprotokoll wurde erfolgreich gespichert als:\n${it.absolutePath}",
                    type = DialogType.INFO
                )
            }
        )
    }

    private fun multiProtocolWizard(): List<ProtocolReportData>{
        // ... use wizard to select data ...
        return clientService.findAll().map {
            newProtocolReportData(it) // just select all ATM
        }.filter { it.rows.isNotEmpty() }.toList()
    }

    private fun newProtocolReportData(client: Client): ProtocolReportData {
        val rows = treatmentService.findAllFor(client).sortedBy { it.number }.map { it.toReportData() }
        val author = preferences.get().username
        val printDate = clock.now()
        return ProtocolReportData(author, printDate, client.toReportData(), rows)
    }
}

private fun Treatment.toReportData() = TreatmentReportData(number, note.nullIfEmpty(), date)

private fun Client.toReportData() = ClientReportData(fullName, children.nullIfEmpty(), job.nullIfEmpty(), picture.toReportRepresentation(), CPropsComposer.compose(this))
