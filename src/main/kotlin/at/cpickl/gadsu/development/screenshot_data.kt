package at.cpickl.gadsu.development

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentSavedEvent
import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.ClientUpdatedEvent
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.ElementMaybe
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.YinYangMaybe
import at.cpickl.gadsu.client.view.ClientMasterView
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.global.DUMMY_CREATED
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.bySrcTestFile
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import com.github.christophpickl.kpotpourri.common.numbers.forEach
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import java.util.ArrayList
import javax.inject.Inject
import javax.swing.SwingUtilities

class ScreenshotDataInserter @Inject constructor(
    private val clientService: ClientService,
    private val treatmentService: TreatmentService,
    private val appointmentService: AppointmentService,
    private val bus: EventBus,
    private val clientListView: ClientMasterView
) {

    fun insertData() {
        arrayOf(
            Client(null, DUMMY_CREATED, ClientState.ACTIVE, "Maximilian", "Mustermann", "Max Schnarcher", "Max",
                Contact(
                    mail = "max@mustermann.at",
                    phone = "0699 11 22 33 432",
                    street = "Hauptstrasse 22/11/A",
                    zipCode = "1010",
                    city = "Wien"
                ),
                "von Anna",
                true,
                true,
                DateFormats.DATE.parseDateTime("26.10.1986"),
                Gender.MALE,
                "\u00d6sterreich",
                "Eisenstadt, Bgld",
                Relationship.MARRIED,
                "Computermensch",
                "keine",
                "Radfahren",
                "Meine supi wuzi Anmerkung.",

                "", "", "", "",
                "Regelbeschwerden",
                "Emotionaler rollercoaster, stechende Schmerzen im Unterleib, seitliche Kopfschmerzen, überarbeitet, viel Stress",
                "Starker Holztyp, macht gerne neues, ärgert sich recht viel (Le), Kopfschmerzen (Gb)",
                "Le-Qi-Stau",
                YinYangMaybe.YANG,
                "eher yang",
                ElementMaybe.FIRE,
                "", "", "", "", "", "",
                ClientCategory.A,
                ClientDonation.PRESENT,
                "",
                pic("profile_pic-valid_man1.jpg"),
                CProps.builder()
                    .add(XProps.Impression, XProps.ImpressionOpts.SkinBright, XProps.ImpressionOpts.BehaveNervous, XProps.ImpressionOpts.SpeaksMuch, XProps.ImpressionOpts.VoiceQuick)
                    .add(XProps.Temperature, XProps.TemperatureOpts.AversionCold, XProps.TemperatureOpts.FeelWarm, XProps.TemperatureOpts.SweatEasily, XProps.TemperatureOpts.SweatMuch)
                    .add(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep, XProps.SleepOpts.TiredInMorning)
                    .add(XProps.BodyConception, XProps.BodyConceptionOpts.Gentle, XProps.BodyConceptionOpts.JointsStiff, XProps.BodyConceptionOpts.TissueTight)
                    .add(XProps.Hungry, XProps.HungryOpts.BigHunger)
                    .add(XProps.Digestion, XProps.DigestionOpts.StoolHard, XProps.DigestionOpts.DigestionFast)
                    .add(XProps.ChiStatus, XProps.ChiStatusOpts.Top, XProps.ChiStatusOpts.Outside, XProps.ChiStatusOpts.Much)
                    .add(XProps.Liquid, XProps.LiquidOpts.DrinkMuch, XProps.LiquidOpts.ThirstMuch, XProps.LiquidOpts.UrinColorBright)
                    .build()

            ),
            Client.INSERT_PROTOTYPE.copy(
                firstName = "Chuck",
                lastName = "Norris",
                gender = Gender.MALE,
                picture = pic("profile_pic-valid_man2.jpg")
            ),
            Client.INSERT_PROTOTYPE.copy(
                firstName = "Pam",
                lastName = "Anderson",
                gender = Gender.FEMALE,
                picture = pic("profile_pic-valid_woman1.jpg")
            ),
            Client.INSERT_PROTOTYPE.copy(
                firstName = "Queen",
                lastName = "Liz",
                gender = Gender.FEMALE,
                picture = pic("profile_pic-valid_woman2.jpg"),
                birthday = DateTime.now().plusDays(1)
            ),
            Client.INSERT_PROTOTYPE.copy(
                firstName = "Anna",
                lastName = "Nym",
                gender = Gender.FEMALE,
                picture = MyImage.DEFAULT_PROFILE_WOMAN
            )
        ).forEach {
            val saved = clientService.insertOrUpdate(it)
            updatePic(saved, it.picture)

            val clientId = saved.id!!
            val today = DateTime.now()

            if (it.firstName == "Pam") {
                TreatmentCreator(clientId)
                    .add(today.minusDays(1))
                    .insert(treatmentService, bus)
            } else if (it.firstName == "Queen") {
                TreatmentCreator(clientId)
                    .add(today.minusDays(20))
                    .insert(treatmentService, bus)
            } else if (it.firstName == "Anna") {
                TreatmentCreator(clientId)
                    .add(today.minusDays(46))
                    .add(today.minusDays(45))
                    .add(today.minusDays(44))
                    .add(today.minusDays(43))
                    .add(today.minusDays(42))
                    .add(today.minusDays(41))
                    .insert(treatmentService, bus)

            } else if (it.firstName == "Maximilian") {
                TreatmentCreator(clientId)
                    .add(today.minusWeeks(11)) { treat ->
                        treat.copy(
                            aboutDiscomfort = "* in der arbeit heute war es stressig\n* gleich nach letzter behandlung sofort daheim eingeschlafen",
                            aboutContent = "* in bauchlage die Bl am ruecken\n* barfuss shiatsu\n* Lu am arm, arme lockern, handballen\n* nacken lockern, Gb/3E",
                            aboutDiagnosis = "* ganz ruhig dagelegen, augen geschlossen\n* schultern leicht festgehalten, handballengewebe verhaertet\n* Bl schwach in der tiefe spuerbar\n* Lu jitsu v.a. am unterarm",
                            aboutFeedback = "* ist fast eingeschlafen\n* am unterarm hat es eher weh getan\n* war ein wenig kalt",
                            aboutHomework = "* mehr stocki gehen ueben :)\n* eine zeitlang versuchen einfach nur zu geniessen ohne viel muessen und verzicht",
                            aboutUpcoming = "* Lu iokai am bein\n* SL schulter, rotationen, schulterguertel/nacken",
                            dynTreatments = listOf(
                                HaraDiagnosis(
                                    kyos = listOf(MeridianAndPosition.UrinaryBladderBottom, MeridianAndPosition.LargeIntestineLeft),
                                    jitsus = listOf(MeridianAndPosition.LungRight, MeridianAndPosition.GallBladder),
                                    bestConnection = Pair(MeridianAndPosition.UrinaryBladderBottom, MeridianAndPosition.LungRight),
                                    note = "* ganze rechte bereich eher jitsu"
                                ),
                                TongueDiagnosis.insertPrototype()
                            ),
                            treatedMeridians = listOf(Meridian.Lung, Meridian.UrinaryBladder)
                        )
                    }
                    .add(today.minusWeeks(10))
                    .add(today.minusWeeks(9))
                    .add(today.minusWeeks(8))
                    .add(today.minusWeeks(7))
                    .add(today.minusWeeks(6))
                    .add(today.minusWeeks(5))
                    .add(today.minusWeeks(4))
                    .add(today.minusWeeks(3))
                    .add(today.minusWeeks(2))
                    .add(today.minusWeeks(1))
                    .insert(treatmentService, bus)

                // haha, some nice hack to get the proper number displayed in list (event dispatching is delayed, therefor DB count will return 3, and then treat created event will increase to 6! ;)
                SwingUtilities.invokeLater { 11.forEach { clientListView.treatmentCountDecrease(clientId) } }

                arrayOf(
                    Appointment.insertPrototype(clientId, today.plusWeeks(1)),
                    Appointment.insertPrototype(clientId, today.plusWeeks(2))
                ).forEach {
                    val savedApp = appointmentService.insertOrUpdate(it)
                    bus.post(AppointmentSavedEvent(savedApp))
                }
            }
        }
    }

    private fun pic(fileName: String) = MyImage.bySrcTestFile(fileName)

    private fun updatePic(saved: Client, picture: MyImage) {
        if (picture.isUnsavedDefaultPicture) {
            return
        }
        val clientWithPic = saved.copy(picture = picture)
        clientService.savePicture(clientWithPic)
        bus.post(ClientUpdatedEvent(clientWithPic))
    }


    private class TreatmentCreator(private val clientId: String) {

        private val treatments = ArrayList<Treatment>()

        fun add(date: DateTime, treatmentEnhancer: (Treatment) -> Treatment = { it }): TreatmentCreator {
            treatments.add(treatmentEnhancer(Treatment.insertPrototype(
                clientId = clientId,
                number = treatments.size + 1,
                date = date
            )))
            return this
        }

        fun insert(treatmentService: TreatmentService, bus: EventBus) {
            treatments.forEach {
                treatmentService.insert(it)
                bus.post(TreatmentCreatedEvent(it))
            }
        }
    }
}
