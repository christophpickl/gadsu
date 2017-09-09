package at.cpickl.gadsu.development

import at.cpickl.gadsu.global.DUMMY_CREATED
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
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.view.ClientMasterView
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentCreatedEvent
import at.cpickl.gadsu.treatment.TreatmentService
import com.github.christophpickl.kpotpourri.common.numbers.forEach
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import java.io.File
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
                        DateFormats.DATE.parseDateTime("26.10.1986"),
                        Gender.MALE,
                        "\u00d6sterreich",
                        "Eisenstadt, Bgld",
                        Relationship.MARRIED,
                        "Computermensch",
                        "keine",
                        "Radfahren",
                        "Meine supi wuzi Anmerkung.",

                        "", "", "", "", "",
                        "Regelbeschwerden",
                        "Emotionaler rollercoaster, stechende Schmerzen im Unterleib, seitliche Kopfschmerzen, überarbeitet, viel Stress",
                        "Starker Holztyp, macht gerne neues, ärgert sich recht viel (Le), Kopfschmerzen (Gb)",
                        "Le-Qi-Stau",
                        ClientCategory.A,
                        ClientDonation.PRESENT,
                        "",
                        pic("profile_pic-valid_man1.jpg"),
                        CProps.builder()
                                .add(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep, XProps.SleepOpts.TiredInMorning)
                                .add(XProps.Hungry, XProps.HungryOpts.BigHunger)
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

            if (it.firstName == "Max") {
                val clientId = saved.id!!
                val today = DateTime.now()

                TreatmentCreator(clientId)
                        .add(today.minusWeeks(3))
                        .add(today.minusWeeks(2))
                        .add(today.minusWeeks(1))
                        .insert(treatmentService, bus)

                // haha, some nice hack to get the proper number displayed in list (event dispatching is delayed, therefor DB count will return 3, and then treat created event will increase to 6! ;)
                SwingUtilities.invokeLater { 3.forEach { clientListView.treatmentCountDecrease(clientId) } }

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

    private fun pic(fileName: String) = MyImage.byFile(File("src/test/resources/gadsu_test/$fileName"))

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
        fun add(date: DateTime): TreatmentCreator {
            treatments.add(Treatment.insertPrototype(
                    clientId = clientId,
                    number = treatments.size + 1,
                    date = date
            ))
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
