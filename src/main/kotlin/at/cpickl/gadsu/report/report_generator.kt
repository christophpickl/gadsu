package at.cpickl.gadsu.report

import java.io.ByteArrayOutputStream
import java.io.File
import java.util.HashMap


/*
JASPER GUIDE
=======================================================

* delete <<language="groovy">> in JRXML file when getting a class not found exception about some groovy class when compiling a jasper report
* declare all your parameters/field explicitly when getting a JRValidationException saying report design not valid about some not found stuff

 */

data class ReportConfig(val jrxmlClasspath: String, val parameters: Map<String, Any?>, val rows: Collection<Any>)

interface ReportWithRows {
    val rows: List<Any>
}

interface GenericReportGenerator<R : ReportWithRows> {
    fun savePdfTo(report: R, target: File)
    fun view(report: R)
    fun generateByteStream(report: R): ByteArrayOutputStream
}

abstract class BaseReportGenerator<R : ReportWithRows>(
        private val jrxmlClasspath: String,
        private val engine: JasperEngine
) : GenericReportGenerator<R> {

    override fun savePdfTo(report: R, target: File) {
        engine.savePdfTo(buildConfig(report), target)
    }

    override fun view(report: R) {
        engine.view(buildConfig(report))
    }

    override fun generateByteStream(report: R): ByteArrayOutputStream {
        return engine.toByteStream(buildConfig(report))
    }

    private fun buildConfig(report: R): ReportConfig {
        val pairs = buildParameters(report)
        val params = HashMap<String, Any?>()
        pairs.forEach { params.put(it.first, it.second) }
        return ReportConfig(jrxmlClasspath, params, report.rows)
    }

    abstract fun buildParameters(report: R): Array<Pair<String, Any?>>
}

