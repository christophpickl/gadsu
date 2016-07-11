package at.cpickl.gadsu.development

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentSavedEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.treatment.CurrentTreatment
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.forTreatment
import at.cpickl.gadsu.view.MainFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.io.File
import javax.inject.Inject


@Logged
@Suppress("UNUSED_PARAMETER")
open class DevelopmentController @Inject constructor(
        private val clientService: ClientService,
        private val treatmentService: TreatmentService,
        private val appointmentService: AppointmentService,
        private val bus: EventBus,
        private val mainFrame: MainFrame,
        private val currentClient: CurrentClient,
        private val currentTreatment: CurrentTreatment
) {

    private var devFrame: DevelopmentFrame? = null

    @Subscribe open fun onShowDevWindowEvent(event: ShowDevWindowEvent) {
        devFrame = DevelopmentFrame(mainFrame.dockPositionRight, bus)
        devFrame!!.updateClient(currentClient.data)
        devFrame!!.updateTreatment(currentTreatment.data)
        devFrame!!.start()
    }

    @Subscribe open fun onCurrentEvent(event: CurrentEvent) {
        event.forClient { devFrame?.updateClient(it) }
        event.forTreatment{ devFrame?.updateTreatment(it) }

    }

    @Subscribe fun onAny(event: Any) {
        devFrame?.addEvent(event)
    }


    @Subscribe open fun onDevelopmentResetDataEvent(event: DevelopmentResetDataEvent) {
        deleteAll()

        arrayOf(
                Client(null, DUMMY_CREATED/* will not be used anyway, hmpf ... */, ClientState.ACTIVE, "Max", "Mustermann",
                        Contact(
                                mail = "max@mustermann.at",
                                phone = "0699 11 22 33 432",
                                street = "Hauptstrasse 22/11/A",
                                zipCode = "1010",
                                city = "Wien"
                        ),
                        DateFormats.DATE.parseDateTime("26.10.1986"),
                        Gender.MALE,
                        "\u00d6sterreich",
                        "Eisenstadt, Bgld",
                        Relationship.MARRIED,
                        "Computermensch",
                        "keine",
                        "Radfahren",
                        "Meine supi wuzi Anmerkung.",

                        "mein eindruck ist blub",
                        "er hatte ein gebrochenes bein",
                        "nacken; zuwenig selbstbewusstsein",
                        "perfektionist; schlangen phobie; single",
                        "mag mehr selbstbewusstein",

                        "zyklus 24T-6T; drahtiger puls",
                        MyImage.byFile(File("src/test/resources/gadsu_test/profile_pic-valid_man1.jpg")),
                        CProps.builder
                                .add(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep, XProps.SleepOpts.TiredInMorning)
                                .add(XProps.Hungry, XProps.HungryOpts.BigHunger)
                                .build()

                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Anna",
                        lastName = "Nym",
                        countryOfOrigin = "Austria",
                        gender = Gender.FEMALE,
                        picture = MyImage.DEFAULT_PROFILE_WOMAN,
                        cprops = CProps.empty
                )
        ).forEach {
            val saved = clientService.insertOrUpdate(it)

            if (saved.firstName.equals("Max")) {
                val firstDate = "31.12.2001 14:15:00".parseDateTime()
                val clientId = saved.id!!
                val clientWithPic = saved.copy(picture = it.picture)
                clientService.savePicture(clientWithPic)
                bus.post(ClientUpdatedEvent(clientWithPic))

                arrayOf(
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 1,
                                date = firstDate,
                                duration = minutes(20),
                                aboutDiagnosis = "Ihm gings ganz ganz schlecht.",
                                aboutContent = "Den Herzmeridian hab ich behandelt.",
                                aboutHomework = "Er soll mehr sport machen, eh kloa. Und weniger knoblauch essen!",
                                note = "Aja, und der kommentar passt sonst nirgends rein ;)"
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 2,
                                date = firstDate.plusWeeks(1),
                                note = ""
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 3,
                                date = firstDate.plusWeeks(2),
                                note = "A my note for treatment 3 for maxiiii. my note for treatment 3 for maxiiii. \nB my note for treatment 3 for maxiiii.\n\n\nXXX nmy note for treatment 3 for maxiiii. "
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 4,
                                date = firstDate.plusWeeks(3),
                                duration = minutes(30),
                                note = "Was a quick one"
                        )
                ).forEach {
                    treatmentService.insert(it) // TODO dispatching event should happen in service, shouldnt it?!
                    bus.post(TreatmentCreatedEvent(it))
                }

                val appDate = "15.01.2020 14:30:00".parseDateTime()
                arrayOf(
                    Appointment.insertPrototype(clientId, appDate),
                    Appointment.insertPrototype(clientId, appDate.plusDays(1))
                ).forEach {
                    val savedApp = appointmentService.insertOrUpdate(it)
                    bus.post(AppointmentSavedEvent(savedApp))
                }
            }
        }
    }

    @Subscribe open fun onDevelopmentClearDataEvent(event: DevelopmentClearDataEvent) {
        deleteAll()
    }

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
//        val max = clientService.findAll().first { it.firstName == "Max" }
//        bus.post(ClientSelectedEvent(max, null))
//        val treatment = treatmentService.findAllFor(max).first { it.number == 1 }
//        bus.post(OpenTreatmentEvent(treatment))
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        devFrame?.close()
    }

    private fun deleteAll() {
        clientService.findAll().forEach { // not directly supported in service, as this is a DEV feature only!
            clientService.delete(it)
        }
    }

}
