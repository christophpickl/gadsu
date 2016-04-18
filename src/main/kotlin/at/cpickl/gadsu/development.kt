package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.view.GadsuMenuBar
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JMenu
import javax.swing.JMenuItem


class Development {
    companion object {
        private val SYSPROPERTY_KEY = "gadsu.development"

        val ENABLED: Boolean = System.getProperty(SYSPROPERTY_KEY, "").equals("true")
        val COLOR_ENABLED = ENABLED && false

        init {
            if (ENABLED) {
                println("Development mode is enabled via '-D$SYSPROPERTY_KEY=true'")
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
        private val clientService: ClientService,
        private val treatmentRepo: TreatmentRepository,
        private val clock: Clock,
        private val bus: EventBus
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onDevelopmentResetDataClientEvent(event: DevelopmentResetDataClientEvent) {
        log.debug("onDevelopmentResetDataClientEvent(event)")

        clientRepo.findAll().forEach {
            clientService.delete(it)
        }

        arrayOf(newClient("Max", "Mustermann", Images.DEFAULT_PROFILE_MAN),
                newClient("Anna", "Nym", Images.DEFAULT_PROFILE_WOMAN)
        ).forEach {
            val savedClient = clientRepo.insert(it)
            bus.post(ClientCreatedEvent(savedClient))

            if (savedClient.firstName.equals("Max")) {
                arrayOf(newTreatment(1, savedClient),
                        newTreatment(2, savedClient)
                ).forEach {
                    treatmentRepo.insert(it, savedClient)
                    bus.post(TreatmentCreatedEvent(it))
                }
            }
        }
    }

    private fun newClient(firstName: String, lastName: String, image: MyImage) =
            Client(null, clock.now(), firstName, lastName, image)

    private fun newTreatment(number: Int, client: Client) = Treatment(null, client.id!!, clock.now(), number, clock.nowWithoutSeconds(),
            "note for treatment number $number")

}

var JComponent.debugColor: Color?
        get() = null
        set(value) {
            if (Development.COLOR_ENABLED) {
                background = value
            }
        }

