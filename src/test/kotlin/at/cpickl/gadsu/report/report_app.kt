package at.cpickl.gadsu.report

import at.cpickl.gadsu.report.multiprotocol.MultiProtocolCoverData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGeneratorImpl
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import com.google.common.eventbus.EventBus
import org.mockito.Mockito
import java.io.File


fun main(args: Array<String>) {
    LogConfigurator(debugEnabled = true).configureLog()

    generateAndViewMultiProtocol()
}

private fun generateAndViewMultiProtocol() {
    val repo = Mockito.mock(MultiProtocolRepository::class.java)
    val generator = MultiProtocolGeneratorImpl(
            JasperProtocolGenerator(JasperEngineImpl()), repo, SimpleTestableClock(), EventBus())
    val target = File.createTempFile("multi", ".pdf")
    target.deleteOnExit()
    generator.generatePdf(target, MultiProtocolCoverData.DUMMY, listOf(ProtocolReportData.testInstance()))

    println("VIEW start: ${target.absolutePath}")
    val processBuilder = ProcessBuilder("open", target.absolutePath)
    val process = processBuilder.start()
    val exitCode = process.waitFor()
    println("END (exit code: $exitCode)")
}

private fun generateAndViewProtocol() {
    JasperProtocolGenerator(JasperEngineImpl())
            .view(ProtocolReportData.testInstance())
//            .savePdfTo(report, File("report.pdf"), true)
}



