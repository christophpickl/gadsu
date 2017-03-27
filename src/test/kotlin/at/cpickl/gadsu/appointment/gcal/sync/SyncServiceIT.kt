package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.AppointmentService
import at.cpickl.gadsu.appointment.gcal.testInstance
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.mail.Mail
import at.cpickl.gadsu.mail.MailSender
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.testinfra.TestBusListener
import at.cpickl.gadsu.testinfra.initTestGuice
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import com.google.common.eventbus.EventBus
import com.google.inject.testing.fieldbinder.Bind
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.joda.time.DateTime
import org.mockito.Mockito.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import javax.inject.Inject

@Test(groups = arrayOf("integration", "guice"))
class SyncServiceIT {

    private lateinit var busListener: TestBusListener

    @Inject private lateinit var bus: EventBus
    @Inject private lateinit var clientService: ClientService
    @Inject private lateinit var appointmentService: AppointmentService
    @Inject private lateinit var syncService: SyncService
    @Inject private lateinit var prefs: Prefs
    @Inject private lateinit var clock: Clock

    @Bind private lateinit var mockMailSender: MailSender

    private val templateSubject = "test subject"
    private val templateBody = "test body"
    private lateinit var appointmentDate: DateTime
    private lateinit var client: Client

    @BeforeMethod
    fun init() {
        busListener = TestBusListener()
        mockMailSender = mock(MailSender::class.java)

        initTestGuice(this)

        bus.register(busListener)

        appointmentDate = clock.now().plusDays(1)
        client = clientService.insertOrUpdate(Client.unsavedValidInstance().copy(
                contact = Contact.EMPTY.copy(mail = "test@gadsu.org")
        ))
        prefs.preferencesData = prefs.preferencesData.copy(
                templateConfirmSubject = templateSubject,
                templateConfirmBody = templateBody
        )
    }

    fun `import single new appointment with sending confirmation`() {
        syncService.import(listOf(
                ImportAppointment.testInstance(client, appointmentDate, sendConfirmation = true)
        ))

        assertThat(appointmentService.findAllFutureFor(client), hasSize(1))
        verify(mockMailSender).send(Mail(
                recipient = client.contact.mail,
                subject = templateSubject,
                body = templateBody))
        verifyNoMoreInteractions(mockMailSender)
    }

    fun `import single new appointment without sending confirmation`() {
        syncService.import(listOf(
                ImportAppointment.testInstance(client, appointmentDate, sendConfirmation = false)
        ))

        assertThat(appointmentService.findAllFutureFor(client), hasSize(1))
        verifyNoMoreInteractions(mockMailSender)
    }

}
