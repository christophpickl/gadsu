package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import com.google.inject.Provider
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject
import javax.swing.JFileChooser


@Logged
open class ReportController @Inject constructor(
        private val clientService: ClientService,
        private val treatmentService: TreatmentService,
        private val protocolGenerator: ProtocolGenerator,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val preferences: Provider<PreferencesData>,
        private val dialogs: Dialogs
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
        val chooser = JFileChooser()
        chooser.currentDirectory = File(".") // TODO use preferences for recent save multi protocol report path
        val retrival = chooser.showSaveDialog(null)
        if (retrival != JFileChooser.APPROVE_OPTION) {
            return
        }

        // FIXME show progress bar
        MultiProtocolGeneratorImpl().generate(chooser.selectedFile, cover, protocols)
        dialogs.show(
                title = "Sammelprotokoll erstellt",
                message = "Das Sammelprotokoll wurde erfolgreich gespichert als:\n${chooser.selectedFile.absolutePath}",
                type = DialogType.INFO
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
