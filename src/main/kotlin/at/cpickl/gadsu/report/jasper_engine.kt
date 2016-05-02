package at.cpickl.gadsu.report

import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.xml.JRXmlLoader
import net.sf.jasperreports.view.JasperViewer
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File


interface JasperEngine {

    /**
     * @param target Invoker has to take care target does not exist and parent is an existing, writable folder.
     */
    fun savePdfTo(config: ReportConfig, target: File)
    fun view(config: ReportConfig)
    fun toByteStream(config: ReportConfig): ByteArrayOutputStream

    // also supports exporting as HTML :)
}

class JasperEngineImpl : JasperEngine {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun savePdfTo(config: ReportConfig, target: File) {
        log.debug("savePdfTo(config={}, target={})", config, target.absolutePath)
        val jasperPrint = generatePrintArtifact(config)
        // first generate report, then validate (and implicitly delete file when overwrite is enabled

        //        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream)

        JasperExportManager.exportReportToPdfFile(jasperPrint, target.absolutePath)
        log.info("Successfully saved PDF file to: {}", target.absolutePath)
    }

    override fun toByteStream(config: ReportConfig): ByteArrayOutputStream {
        log.debug("toByteArray(config)")

        val jasperPrint = generatePrintArtifact(config)
        val output = ByteArrayOutputStream()
        JasperExportManager.exportReportToPdfStream(jasperPrint, output)
        return output
    }

    override fun view(config: ReportConfig) {
        log.debug("view(config={})", config)
        JasperViewer.viewReport(generatePrintArtifact(config), false) // disable exit on close
    }

    //    }

    private fun generatePrintArtifact(config: ReportConfig): JasperPrint {
        val templateStream = javaClass.getResourceAsStream(config.jrxmlClasspath) ?:
                throw ReportException("Not found jasper report in classpath: '$config.jrxmlClasspath'!")

        val templateDesign = JRXmlLoader.load(templateStream)
        val jasperReport = JasperCompileManager.compileReport(templateDesign)
        return JasperFillManager.fillReport(jasperReport, config.parameters, JRBeanCollectionDataSource(config.rows))
    }

    // TODO @REPORT - validate save target file in service layer
    //    private fun validateSaveTarget(target: File, forceOverwrite: Boolean) {
    //        if (!target.exists()) {
    //            return
    //        }
    //        if (!forceOverwrite) {
    //            throw ReportTargetFileInvalidUserException("Target already exists: ${target.absolutePath}!",
    //                    target, TargetInvalidReason.ALREADY_EXISTS)
    //        }
    //        // force overwrite enabled, but ...
    //        if (target.isDirectory) {
    //            throw ReportTargetFileInvalidUserException("Target is an already existing directory: ${target.absolutePath}!",
    //                    target, TargetInvalidReason.IS_A_DIRECTORY)
    //        }
    //        val wasDeleted = target.delete()
    //        if (!wasDeleted) {
    //            throw ReportException("Could not delete target file: ${target.absolutePath}!")
    //        }

}

