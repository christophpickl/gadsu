package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CPropTypeCallback
import com.google.inject.AbstractModule


class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
        bind(ProtocolGenerator::class.java).to(JasperProtocolGenerator::class.java)
        bind(JasperEngine::class.java).to(JasperEngineImpl::class.java)

        bind(PrintReportController::class.java).asEagerSingleton()
    }
}

/**
 * User requested to generate a new protocol report.
 */
class CreateProtocolEvent() : UserEvent()

class CreateMultiProtocolEvent() : UserEvent()



class ReportException(message: String, cause: Exception? = null) : GadsuException(message, cause)

object CPropsComposer {
    fun compose(client: Client) : String? {
        if (client.cprops.isEmpty()) {
            return null
        }
        return client.cprops.map { it.onType(object : CPropTypeCallback<String>{
            override fun onEnum(cprop: CPropEnum): String {
                return "Sein ${cprop.label} ist verbunden mit ${cprop.clientValue.map { it.label }.joinToString(", ")}."
            }

        } ) }.joinToString(" ")
    }
}
