package at.cpickl.gadsu.report

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolModule
import at.cpickl.gadsu.service.PrintReportController
import com.google.inject.AbstractModule


class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
        bind(ProtocolGenerator::class.java).to(JasperProtocolGenerator::class.java)
        bind(JasperEngine::class.java).to(JasperEngineImpl::class.java)

        bind(PrintReportController::class.java).asEagerSingleton()

        install(MultiProtocolModule())
    }
}

/**
 * User requested to generate a new protocol report.
 */
class CreateProtocolEvent() : UserEvent()


class ReportException(message: String, cause: Exception? = null) : GadsuException(message, cause)
