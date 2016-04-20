package at.cpickl.gadsu

import at.cpickl.gadsu.client.*
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentRepository
import at.cpickl.gadsu.view.GadsuMenuBar
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JMenu
import javax.swing.JMenuItem


class Development {
    companion object {
        private val SYSPROPERTY_KEY = "gadsu.development"

        val ENABLED: Boolean = System.getProperty(SYSPROPERTY_KEY, "").toLowerCase().equals("true") || System.getProperty(SYSPROPERTY_KEY, "").equals("1")
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

        arrayOf(
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Max",
                        lastName = "Mustermann",
                        gender = Gender.MALE,
                        picture = Images.DEFAULT_PROFILE_MAN
                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Anna",
                        lastName = "Nym",
                        gender = Gender.FEMALE,
                        picture = Images.DEFAULT_PROFILE_WOMAN
                )
        ).forEach {
            val savedClient = clientRepo.insert(it)
            bus.post(ClientCreatedEvent(savedClient))

            if (savedClient.firstName.equals("Max")) {
                arrayOf(
                        Treatment.insertPrototype(
                                clientId = savedClient.id!!,
                                number = 1,
                                date = DateTime.now(),
                                note = "my note for treatment 1 for maxiiii"
                        )
                ).forEach {
                    treatmentRepo.insert(it, savedClient)
                    bus.post(TreatmentCreatedEvent(it))
                }
            }
        }
    }

    private fun newTreatment(number: Int, client: Client) =
            Treatment(null, client.id!!, clock.now(), number, clock.nowWithoutSeconds(),
            "note for treatment number $number")

}

var JComponent.debugColor: Color?
    get() = null
    set(value) {
        if (Development.COLOR_ENABLED) {
            background = value
        }
    }

