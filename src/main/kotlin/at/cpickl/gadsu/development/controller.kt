package at.cpickl.gadsu.development

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentSavedEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.treatment.CurrentTreatment
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatments
import at.cpickl.gadsu.treatment.dyn.DynTreatmentsCallback
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureMeasurement
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty
import at.cpickl.gadsu.treatment.forTreatment
import at.cpickl.gadsu.view.MainFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime
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
        private val currentTreatment: CurrentTreatment,
        private val screenshotInserter: ScreenshotDataInserter
) {

    private var devFrame: DevelopmentFrame? = null

    @Subscribe open fun onShowDevWindowEvent(event: ShowDevWindowEvent) {
        devFrame = DevelopmentFrame(mainFrame.dockPositionRight)
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
                Client.REAL_DUMMY,
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Xnna",
                        lastName = "Xym",
                        contact = Contact.EMPTY.copy(mail = "xnna@discard.email"),
                        gender = Gender.FEMALE,
                        picture = MyImage.DEFAULT_PROFILE_WOMAN,
                        donation = ClientDonation.MONEY,
                        cprops = CProps.empty
                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Alien",
                        contact = Contact.EMPTY.copy(mail = "alien@discard.email"),
                        wantReceiveMails = false,
                        gender = Gender.UNKNOWN,
                        category = ClientCategory.C,
//                        picture = MyImage.DEFAULT_PROFILE_ALIEN,
                        cprops = CProps.empty
                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Soon"
                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Later"
                ),
                Client.INSERT_PROTOTYPE.copy(
                        firstName = "Latest"
                )
        ).forEach {
            val saved = clientService.insertOrUpdate(it)

            if (saved.firstName == "Xnna") {
                bus.post(TreatmentCreatedEvent(treatmentService.insert(
                        Treatment.insertPrototype(clientId = saved.id!!, number = 1, date = DateTime.now().minusDays(20))
                )))
            } else if (saved.firstName == "Soon") {
                bus.post(AppointmentSavedEvent(appointmentService.insertOrUpdate(Appointment.insertPrototype(saved.id!!, DateTime.now().plusDays(7)))))
            } else if (saved.firstName == "Later") {
                bus.post(TreatmentCreatedEvent(treatmentService.insert(
                        Treatment.insertPrototype(clientId = saved.id!!, number = 1, date = DateTime.now().minusDays(42))
                )))
            } else if (saved.firstName == "Latest") {
                bus.post(TreatmentCreatedEvent(treatmentService.insert(
                        Treatment.insertPrototype(clientId = saved.id!!, number = 1, date = DateTime.now().minusDays(99))
                )))
            } else if (saved.firstName == Client.REAL_DUMMY.firstName) {
                val clientId = saved.id!!
                val clientWithPic = saved.copy(picture = it.picture)
                clientService.savePicture(clientWithPic)
                bus.post(ClientUpdatedEvent(clientWithPic))

                arrayOf(
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 1,
                                date = DateTime.now().minusDays(2),
                                duration = minutes(20),
                                aboutDiagnosis = "Ihm gings ganz ganz schlecht.",
                                aboutContent = "Den Herzmeridian hab ich behandelt.",
                                aboutHomework = "Er soll mehr sport machen, eh kloa. Und weniger knoblauch essen!",
                                note = "Aja, und der kommentar passt sonst nirgends rein ;)",
                                treatedMeridians = listOf(Meridian.GallBladder, Meridian.UrinaryBladder, Meridian.Stomach),
                                dynTreatments = listOf(
                                        HaraDiagnosis(
                                                kyos = listOf(MeridianAndPosition.UrinaryBladderBottom),
                                                jitsus = listOf(MeridianAndPosition.Liver),
                                                bestConnection = MeridianAndPosition.UrinaryBladderBottom to MeridianAndPosition.Liver,
                                                note = "* rechts mehr kyo\n* insgesamt sehr hohe spannung"
                                        ),
                                        TongueDiagnosis(
                                                color = listOf(TongueProperty.Color.RedTip, TongueProperty.Color.Pink),
                                                shape = listOf(TongueProperty.Shape.Long),
                                                coat = listOf(TongueProperty.Coat.Yellow, TongueProperty.Coat.Thick),
                                                special = emptyList(),
                                                note = "* zunge gruen"),
                                        PulseDiagnosis(
                                                properties = listOf(PulseProperty.Ascending, PulseProperty.Deep, PulseProperty.Soft),
                                                note = "* war irgendwie \"zaeh\""
                                        ),
                                        BloodPressure(
                                                before = BloodPressureMeasurement(90, 110, 80),
                                                after = BloodPressureMeasurement(80, 100, 70)
                                        )
                                )
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 2,
                                date = DateTime.now().minusDays(2),
                                dynTreatments = emptyList()
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 3,
                                date = DateTime.now().minusDays(3),
                                note = "A my note for treatment 3 for maxiiii. my note for treatment 3 for maxiiii. \nB my note for treatment 3 for maxiiii.\n\n\nXXX nmy note for treatment 3 for maxiiii. ",
                                dynTreatments = emptyList()
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 4,
                                date = DateTime.now().minusDays(4),
                                duration = minutes(30),
                                note = "Was a quick one",
                                dynTreatments = DynTreatments.values().map { it.call(object : DynTreatmentsCallback<DynTreatment> {
                                    override fun onHaraDiagnosis() = HaraDiagnosis(
                                            kyos = listOf(MeridianAndPosition.LargeIntestineLeft),
                                            jitsus = listOf(MeridianAndPosition.Liver),
                                            bestConnection = Pair(MeridianAndPosition.LargeIntestineLeft, MeridianAndPosition.Liver),
                                            note = "* oberer bereich war hart"
                                    )
                                    override fun onTongueDiagnosis() = TongueDiagnosis(
                                            color = listOf(TongueProperty.Color.DarkRed),
                                            shape = listOf(TongueProperty.Shape.Flaccid),
                                            coat = listOf(TongueProperty.Coat.Yellow),
                                            special = listOf(TongueProperty.Special.MiddleCrack),
                                            note = "* grooosser mittelrisse!"
                                    )
                                    override fun onPulseDiagnosis() = PulseDiagnosis(
                                            properties = listOf(PulseProperty.Deep, PulseProperty.Full),
                                            note = "* helle, zarte haut beim palpieren"
                                    )
                                    override fun onBloodPressure() = BloodPressure(
                                            before = BloodPressureMeasurement(80, 120, 60),
                                            after = BloodPressureMeasurement(70, 110, 50)
                                    )
                                }) }
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 5,
                                date = DateTime.now().minusDays(5),
                                dynTreatments = emptyList()
                        ),
                        Treatment.insertPrototype(
                                clientId = clientId,
                                number = 6,
                                date = DateTime.now().minusDays(6),
                                dynTreatments = emptyList()
                        )
                ).forEach {
                    treatmentService.insert(it)
                    bus.post(TreatmentCreatedEvent(it))
                }
            }
        }
    }

    @Subscribe open fun onDevelopmentResetScreenshotDataEvent(event: DevelopmentResetScreenshotDataEvent) {
        deleteAll()

        screenshotInserter.insertData()
    }

    @Subscribe open fun onDevelopmentClearDataEvent(event: DevelopmentClearDataEvent) {
        deleteAll()
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
