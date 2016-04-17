package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.GadsuUserException
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.xml.JRXmlLoader
import net.sf.jasperreports.view.JasperViewer
import org.slf4j.LoggerFactory
import java.io.File
import java.util.HashMap


/*
JASPER GUIDE
=======================================================

* delete <<language="groovy">> in JRXML file when getting a class not found exception about some groovy class when compiling a jasper report
* declare all your parameters/field explicitly when getting a JRValidationException saying report design not valid about some not found stuff

 */


enum class TargetInvalidReason {
    ALREADY_EXISTS,
    IS_A_DIRECTORY
}

// TODO catch this exception in service layer and display dialog with mapped error message (TargetInvalidReason)
class ReportTargetFileInvalidUserException(message: String, val target: File, val reason: TargetInvalidReason) :
        GadsuException(message), GadsuUserException


interface JasperEngine {

    fun savePdfTo(config: ReportConfig, target: File, forceOverwrite: Boolean = false)

    fun view(config: ReportConfig)

    // also supports exporting as HTML :)
}

data class ReportConfig(val jrxmlClasspath: String, val parameters: Map<String, out Any>, val rows: Collection<out Any>)

class JasperEngineImpl : JasperEngine {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun savePdfTo(config: ReportConfig, target: File, forceOverwrite: Boolean) {
        log.debug("savePdfTo(config={}, target={}, forceOverwrite={})", config, target.absolutePath, forceOverwrite)
        val jasperPrint = generatePrintArtifact(config)
        // first generate report, then validate (and implicitly delete file when overwrite is enabled
        validateSaveTarget(target, forceOverwrite)

        JasperExportManager.exportReportToPdfFile(jasperPrint, target.absolutePath)
        log.info("Successfully saved PDF file to: {}", target.absolutePath)
    }

    override fun view(config: ReportConfig) {
        log.debug("view(config={})", config)
        JasperViewer.viewReport(generatePrintArtifact(config), false) // disable exit on close
    }

    private fun validateSaveTarget(target: File, forceOverwrite: Boolean) {
        if (!target.exists()) {
            return
        }
        if (!forceOverwrite) {
            throw ReportTargetFileInvalidUserException("Target already exists: ${target.absolutePath}!",
                    target, TargetInvalidReason.ALREADY_EXISTS)
        }
        // force overwrite enabled, but ...
        if (target.isDirectory) {
            throw ReportTargetFileInvalidUserException("Target is an already existing directory: ${target.absolutePath}!",
                    target, TargetInvalidReason.IS_A_DIRECTORY)
        }
        val wasDeleted = target.delete()
        if (!wasDeleted) {
            throw ReportException("Could not delete target file: ${target.absolutePath}!")
        }
    }

    private fun generatePrintArtifact(config: ReportConfig): JasperPrint {
        val templateStream = javaClass.getResourceAsStream(config.jrxmlClasspath) ?:
                throw ReportException("Not found jasper report in classpath: '$config.jrxmlClasspath'!")

        val templateDesign = JRXmlLoader.load(templateStream)
        val jasperReport = JasperCompileManager.compileReport(templateDesign)
        return JasperFillManager.fillReport(jasperReport, config.parameters, JRBeanCollectionDataSource(config.rows))
    }

//    fun toByteArray(config: ReportConfig): ByteArray {
//        log.debug("toByteArray()")
//
//        val jasperPrint = generatePrintArtifact(config)
//
//        val output = ByteArrayOutputStream()
//        JasperExportManager.exportReportToPdfStream(jasperPrint, output)
//        return output.toByteArray()
//    }

}


interface GenericReportGenerator<R : ReportWithRows> {
    fun savePdfTo(report: R, target: File, forceOverwrite: Boolean = false)
    fun view(report: R)
}

interface ReportWithRows {
    val rows: List<out Any>
}
abstract class BaseReportGenerator<R : ReportWithRows>(
        private val jrxmlClasspath: String,
        private val engine: JasperEngine
) : GenericReportGenerator<R> {

    override fun view(report: R) {
        engine.view(buildConfig(report))
    }

    override fun savePdfTo(report: R, target: File, forceOverwrite: Boolean) {
        engine.savePdfTo(buildConfig(report), target, forceOverwrite)
    }

    private fun buildConfig(report: R): ReportConfig {
        val pairs = buildParameters(report)
        val params = HashMap<String, Any>()
        pairs.forEach { params.put(it.first, it.second) }
        return ReportConfig(jrxmlClasspath, params, report.rows)
    }

    abstract fun buildParameters(report: R): Array<Pair<String, out Any>>
}
