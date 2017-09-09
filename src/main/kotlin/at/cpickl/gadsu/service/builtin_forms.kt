package at.cpickl.gadsu.service

import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import java.awt.print.PrinterJob
import java.io.File
import javax.inject.Inject

class PrintFormEvent(val type: FormType) : UserEvent()
class FormSaveEvent(val type: FormType) : UserEvent()

enum class FormType(
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

    @Subscribe open fun onPrintFormEvent(event: PrintFormEvent) {
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

    @Subscribe open fun onFormSaveEvent(event: FormSaveEvent) {
        ChooseFile.savePdf(
            fileTypeLabel = event.type.label,
            currentDirectory = prefs.recentSaveReportFolder,
            onSuccess = { doSavePdf(it, event.type) }
        )
    }

    private fun doSavePdf(pdfTarget: File, form: FormType) {
        prefs.recentSaveReportFolder = pdfTarget
        pdfTarget.writeByClasspath(form.fullPath)

        dialogs.show(
                title = "${form.label} gespeichert",
                message = "Der ${form.label} wurde erfolgreich gespeichert als:\n${pdfTarget.absolutePath}",
                type = DialogType.INFO
        )
    }

    private val FormType.fullPath: String get() = "/gadsu/reports/form/$classpathFilename"
}
