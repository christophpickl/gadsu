package at.cpickl.gadsu.report

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.report.multiprotocol.*
import at.cpickl.gadsu.service.ChooseFile
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.nullIfEmpty
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import com.google.inject.Provider
import java.io.File
import javax.inject.Inject
import javax.swing.SwingUtilities


@Logged
open class ReportController @Inject constructor(
        private val clientService: ClientService,
        private val treatmentService: TreatmentService,
        private val treatmentRepository: TreatmentRepository,
        private val protocolGenerator: ProtocolGenerator,
        private val clock: Clock,
        private val currentClient: CurrentClient,
        private val preferences: Provider<PreferencesData>,
        private val dialogs: Dialogs,
        private val multiProtocolGenerator: MultiProtocolGenerator,
        private val multiProtocolRepository: MultiProtocolRepository,
        private val windowProvider: Provider<MultiProtocolWindow>,
        private val prefs: Prefs
) {
    private var recentWindow: MultiProtocolWindow? = null

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

    @Subscribe open fun onRequestCreateMultiProtocolEvent(event: RequestCreateMultiProtocolEvent) {
        val protocolizableTreatments = treatmentRepository.countAllNonProtocolized()
        val window = windowProvider.get()
        recentWindow = window
        SwingUtilities.invokeLater { window.start(protocolizableTreatments) }
    }

    @Subscribe open fun onReallyCreateMultiProtocolEvent(event: ReallyCreateMultiProtocolEvent) {
        createAndSave() {
            it, cover, protocols -> multiProtocolGenerator.generatePdfPersistAndDispatch(it, cover, protocols, event.description)
            recentWindow?.closeWindow()
        }
    }

    @Subscribe open fun onTestCreateMultiProtocolEvent(event: TestCreateMultiProtocolEvent) {
        createAndSave() {
            it, cover, protocols -> multiProtocolGenerator.generatePdf(it, cover, protocols)
        }
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        if (recentWindow != null) {
            recentWindow!!.closeWindow()
        }
    }

    private fun createAndSave(onSuccessCallback: (File, MultiProtocolCoverData, List<ProtocolReportData>) -> Unit) {
        val protocols = multiProtocolWizard()
        if (protocols.isEmpty()) {
            throw IllegalStateException("Expected protocols to have at least one treatment!")
        }

        val printDate = clock.now()
        val author = preferences.get().username
        val cover = MultiProtocolCoverData(printDate, author)

        ChooseFile.savePdf(
                fileTypeLabel = "Sammelprotokoll",
                currentDirectory = prefs.recentSaveMultiProtocolFolder,
                onSuccess = {
                    // TODO show progress bar
                    onSuccessCallback(it, cover, protocols)
                    prefs.recentSaveMultiProtocolFolder = it.parentFile
                    dialogs.show(
                            title = "Sammelprotokoll erstellt",
                            message = "Das Sammelprotokoll wurde erfolgreich gespichert als:\n${it.absolutePath}",
                            type = DialogType.INFO
                    )
                }
        )
    }

    private fun multiProtocolWizard(): List<ProtocolReportData> {
        // ... use wizard to select data ...
        return clientService.findAll().map {
            newProtocolReportData(it) // just select all ATM
        }.filter { it.rows.isNotEmpty() }.toList()
    }

    private fun newProtocolReportData(client: Client): ProtocolReportData {
        val rows = treatmentService.findAllFor(client).sortedBy { it.number }.filter { !multiProtocolRepository.hasBeenProtocolizedYet(it) }.map { it.toReportData() }
        val author = preferences.get().username
        val printDate = clock.now()
        return ProtocolReportData(author, printDate, client.toReportData(), rows)
    }
}

private fun Treatment.toReportData() = TreatmentReportData(id!!, number, note.nullIfEmpty(), date)

private fun Client.toReportData() = ClientReportData(fullName, children.nullIfEmpty(), job.nullIfEmpty(), picture.toReportRepresentation(), CPropsComposer.compose(this))
