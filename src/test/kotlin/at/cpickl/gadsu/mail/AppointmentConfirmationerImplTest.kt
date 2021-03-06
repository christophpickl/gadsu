package at.cpickl.gadsu.mail

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.FreemarkerTemplatingEngine
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.savedValidInstance
import org.mockito.Mockito.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class AppointmentConfirmationerImplTest {

    private lateinit var confirmer: AppointmentConfirmationerImpl
    private lateinit var mockSender: MailSender
    private lateinit var mockPrefs: Prefs
    private val templating = FreemarkerTemplatingEngine()
    private val contactWithMail = Contact.EMPTY.copy(mail = "client@mail.at")

    @BeforeMethod fun setup() {
        mockSender = mock(MailSender::class.java)
        mockPrefs = mock(Prefs::class.java)
        confirmer = AppointmentConfirmationerImpl(templating, mockSender, mockPrefs)
    }

    fun `confirm sends via mail sender`() {
        val client = Client.savedValidInstance().copy(
                firstName = "Florian",
                gender = Gender.MALE,
                contact = contactWithMail)
        val appointmentStart = "1.2.2001 14:30:00".parseDateTime()
        val appointmentEnd = appointmentStart.plusHours(1)
        val appointment = Appointment.savedValidInstance(client.id!!).copy(start = appointmentStart, end = appointmentEnd)

        `when`(mockPrefs.preferencesData).thenReturn(PreferencesData.DEFAULT.copy(
                templateConfirmSubject = "termin am \${dateStart?string[\"d.M.\"]}",
                // gender = M, F, ?
                templateConfirmBody = "hallo <#if gender == \"M\">lieber <#elseif gender == \"F\">liebe </#if>\${name?lower_case}, termin am \${dateStart?string[\"EEEE 'der' d. MMMMM\"]?lower_case} von \${dateStart?string[\"HH:mm\"]} bis \${dateEnd?string[\"HH:mm\"]} uhr, christoph."
        ))

        confirmer.sendConfirmation(client, appointment)

        verify(mockSender).send(Mail(
                recipient = client.contact.mail,
                subject = "termin am 1.2.",
                body = "hallo lieber florian, termin am donnerstag der 1. februar von 14:30 bis 15:30 uhr, christoph.",
                recipientsAsBcc = false
        ))
        verifyNoMoreInteractions(mockSender)
    }

    @Test(expectedExceptions = arrayOf(AppointmentConfirmationException::class))
    fun `no mail configured throws exception`() {
        val client = Client.savedValidInstance().copy(contact = Contact.EMPTY.copy(mail = ""))
        val appointment = Appointment.savedValidInstance(client.id!!)

        confirmer.sendConfirmation(client, appointment)
    }


    fun `Given client with all names set Should use nickNameExt`() {
        val client = Client.savedValidInstance().copy(
                firstName = "firstName",
                nickNameInt = "nickNameInt",
                nickNameExt = "nickNameExt",
                contact = contactWithMail)
        assertNameUsedInTemplate(client, client.nickNameExt)
    }

    fun `Given client with only firstName and nickNameInt set Should use firstName`() {
        val client = Client.savedValidInstance().copy(
                firstName = "firstName",
                nickNameInt = "nickNameInt",
                contact = contactWithMail)
        assertNameUsedInTemplate(client, client.firstName)
    }

    private fun assertNameUsedInTemplate(client: Client, expectedName: String) {
        val appointment = Appointment.savedValidInstance(client.id!!)
        `when`(mockPrefs.preferencesData).thenReturn(PreferencesData.DEFAULT.copy(
                templateConfirmSubject = "subject",
                templateConfirmBody = "name: \${name}"
        ))

        confirmer.sendConfirmation(client, appointment)

        verify(mockSender).send(Mail(
                recipient = client.contact.mail,
                subject = "subject",
                body = "name: $expectedName",
                recipientsAsBcc = false
        ))
    }
}
