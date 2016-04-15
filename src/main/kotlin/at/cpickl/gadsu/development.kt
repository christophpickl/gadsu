package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientDeletedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.view.GadsuMenuBar
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.JMenu
import javax.swing.JMenuItem


class Development {
    companion object {
        val ENABLED: Boolean = System.getProperty("gadsu.development", "").equals("true")
        init {
            if (ENABLED) {
                println("Development mode is enabled via '-Dgadsu.development=true'")
            }
        }

        fun fiddleAroundWithMenuBar(menu: GadsuMenuBar, bus: EventBus) {
            if (!Development.ENABLED) {
                return
            }
            val menuDevelopment = JMenu("Development")
            menu.add(menuDevelopment)

            val item = JMenuItem("Reset Data")
            item.addActionListener { e -> bus.post(DevelopmentResetDataClientEvent()) }
            menuDevelopment.add(item)
        }
    }
}


class DevelopmentResetDataClientEvent : UserEvent()

@Suppress("UNUSED_PARAMETER")
class DevelopmentController @Inject constructor(
        private val clientRepo: ClientRepository,
        private val clock: Clock,
        private val bus: EventBus
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onDevelopmentResetDataClientEvent(event: DevelopmentResetDataClientEvent) {
        log.debug("onDevelopmentResetDataClientEvent(event)")

        clientRepo.findAll().forEach {
            clientRepo.delete(it)
            bus.post(ClientDeletedEvent(it))
        }
        arrayOf(newClient("Max", "Mustermann"), newClient("Anna", "Nym")).forEach {
            val savedClient = clientRepo.insert(it)
            bus.post(ClientCreatedEvent(savedClient))
        }
    }

    private fun newClient(firstName: String, lastName: String) = Client(null, firstName, lastName, clock.now())

}
