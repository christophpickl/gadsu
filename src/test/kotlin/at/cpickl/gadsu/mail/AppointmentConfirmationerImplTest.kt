package at.cpickl.gadsu.mail

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.FreemarkerTemplatingEngine
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.testinfra.savedValidInstance
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class AppointmentConfirmationerImplTest {

    private lateinit var confirmer: AppointmentConfirmationerImpl
    private lateinit var mockSender: MailSender
    private lateinit var mockPrefs: Prefs
    private val templating = FreemarkerTemplatingEngine()

    @BeforeMethod fun setup() {
        mockSender = mock(MailSender::class.java)
        mockPrefs = mock(Prefs::class.java)
        confirmer = AppointmentConfirmationerImpl(templating, mockSender, mockPrefs)
    }

    fun `confirm sends via mail sender`() {
        `when`(mockPrefs.preferencesData).thenReturn(PreferencesData.DEFAULT.copy(
                templateConfirmSubject = "subject for \${name}",
                templateConfirmBody = "body for \${date?datetime}"
        ))

        val client = Client.savedValidInstance().copy(contact = Contact.INSERT_PROTOTYPE.copy(mail = "client@mail.at"))
        val appointment = Appointment.savedValidInstance(client.id!!).copy(start = "1.2.2001 14:30:59".parseDateTime())

        confirmer.sendConfirmation(client, appointment)

        verify(mockSender).send(Mail(
                recipient = client.contact.mail,
                subject = "subject for ${client.firstName}",
                body = "body for 01.02.2001 14:30:59"
        ))
        Mockito.verifyNoMoreInteractions(mockSender)
    }

    @Test(expectedExceptions = arrayOf(AppointmentConfirmationException::class))
    fun `no mail configured throws exception`() {
        val client = Client.savedValidInstance().copy(contact = Contact.INSERT_PROTOTYPE.copy(mail = ""))
        val appointment = Appointment.savedValidInstance(client.id!!)

        confirmer.sendConfirmation(client, appointment)
    }
}
