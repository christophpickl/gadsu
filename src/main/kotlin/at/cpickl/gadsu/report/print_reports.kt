package at.cpickl.gadsu.report

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.ensureExtension
import at.cpickl.gadsu.service.formatDateTimeFile
import at.cpickl.gadsu.service.writeByClasspath
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.print.PrinterJob
import javax.inject.Inject
import javax.swing.JFileChooser

class PrintReportPrintEvent(val type: PrintReportType) : UserEvent()
class PrintReportSaveEvent(val type: PrintReportType) : UserEvent()

enum class PrintReportType(
        val label: String,
        val classpathFilename: String
) {
    ANAMNESE("Anamnesebogen", "Anamnesebogen.pdf"),
    TREATMENT("Behandlungsbogen", "Behandlungsbogen.pdf")
}

@Logged
open class PrintReportController @Inject constructor(
        private val dialogs: Dialogs,
        private val clock: Clock,
        private val prefs: Prefs
) {

    @Subscribe open fun onPrintReportPrintEvent(event: PrintReportPrintEvent) {
        // https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/printing/Printing.java?view=markup
        val job = PrinterJob.getPrinterJob()
//        val pf = job.defaultPage()
//        val paper = Paper()
//        paper.setSize(612.0, 832.0)
//        val margin = 10.0;
//        paper.setImageableArea(margin, margin, paper.width - margin, paper.height - margin);
//        pf.paper = paper
//        pf.orientation = PageFormat.LANDSCAPE

        val stream = javaClass.getResourceAsStream(event.type.fullPath) ?: throw IllegalArgumentException("Not existing classpath resource '${event.type.fullPath}'!")
        val document = PDDocument.load(stream)
        job.setPageable(PDFPageable(document))

        job.jobName = "${event.type.label}-${clock.now().formatDateTimeFile()}"
        if (job.printDialog()) {
            job.print()
        }
    }

    @Subscribe open fun onPrintReportSaveEvent(event: PrintReportSaveEvent) {
        val chooser = JFileChooser()

        chooser.currentDirectory = prefs.recentSaveReportFolder
        val retrival = chooser.showSaveDialog(null)
        if (retrival != JFileChooser.APPROVE_OPTION) {
            return
        }
        val pdfTarget = chooser.selectedFile.ensureExtension("pdf")

        prefs.recentSaveReportFolder = pdfTarget
        pdfTarget.writeByClasspath(event.type.fullPath)

        dialogs.show(
                title = "${event.type.label} gespeichert",
                message = "Der ${event.type.label} wurde erfolgreich gespeichert als:\n${pdfTarget.absolutePath}",
                type = DialogType.INFO
        )
    }

    private val PrintReportType.fullPath: String get() = "/gadsu/reports/print/$classpathFilename"
}
