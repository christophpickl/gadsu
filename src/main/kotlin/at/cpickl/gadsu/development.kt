package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.DateFormats
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


            addItemTo(menuDevelopment, "Reset Data", DevelopmentResetDataEvent(), bus)
            addItemTo(menuDevelopment, "Clear Data", DevelopmentClearDataEvent(), bus)
        }

        private fun addItemTo(menu: JMenu, label: String, event: UserEvent, bus: EventBus) {
            val item = JMenuItem(label)
            item.addActionListener { bus.post(event) }
            menu.add(item)
        }

    }
}


class DevelopmentResetDataEvent : UserEvent()
class DevelopmentClearDataEvent : UserEvent()

@Suppress("UNUSED_PARAMETER")
class DevelopmentController @Inject constructor(
        private val clientRepo: ClientRepository,
        private val clientService: ClientService,
        private val treatmentRepo: TreatmentRepository,
        private val clock: Clock,
        private val bus: EventBus
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onDevelopmentResetDataEvent(event: DevelopmentResetDataEvent) {
        log.debug("DevelopmentResetDataEvent(event)")

        deleteAll()

        arrayOf(
//                Client.INSERT_PROTOTYPE.copy(
//                        firstName = "Max",
//                        lastName = "Mustermann",
//                        gender = Gender.MALE,
//                        picture = Images.DEFAULT_PROFILE_MAN
                Client(null, DUMMY_CREATED, "Max", "Mustermann",
                        Contact(
                                mail = "max@mustermann.at",
                                phone = "0699 11 22 33 432",
                                street = "Hauptstrasse 22/11/A",
                                zipCode = "1010",
                                city = "Wien"
                        ),
                        DateFormats.DATE.parseDateTime("26.10.1986"), Gender.MALE, "\u00d6sterreich",
                        Relationship.MARRIED, "Computermensch", "keine", "Meine supi wuzi Anmerkung.",
                        Images.DEFAULT_PROFILE_MAN
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
                    treatmentRepo.insert(it)
                    bus.post(TreatmentCreatedEvent(it))
                }
            }
        }
    }

    @Subscribe fun onDevelopmentClearDataEvent(event: DevelopmentClearDataEvent) {
        log.debug("onDevelopmentClearDataEvent(event={})", event)

        deleteAll()
    }

    private fun deleteAll() {
        clientService.findAll().forEach {
            clientService.delete(it)
        }
    }

}

var JComponent.debugColor: Color?
    get() = null
    set(value) {
        if (Development.COLOR_ENABLED) {
            background = value
        }
    }

