package at.cpickl.gadsu.report

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CPropTypeCallback
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.treatment.TreatmentService
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Provider
import org.slf4j.LoggerFactory
import javax.inject.Inject


class ReportModule : AbstractModule() {
    override fun configure() {
        bind(ReportController::class.java).asEagerSingleton()
        bind(ProtocolGenerator::class.java).to(JasperProtocolGenerator::class.java)
        bind(JasperEngine::class.java).to(JasperEngineImpl::class.java)

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
