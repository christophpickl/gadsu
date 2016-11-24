package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.Args
import at.cpickl.gadsu.GadsuModule
import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.AppointmentRepository
import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalEventMeta
import at.cpickl.gadsu.appointment.gcal.RealGCalRepository
import at.cpickl.gadsu.appointment.gcal.blankDummy
import at.cpickl.gadsu.appointment.gcal.deleteAllEvents
import at.cpickl.gadsu.appointment.gcal.getEvent
import at.cpickl.gadsu.appointment.gcal.toGCalEvent
import at.cpickl.gadsu.appointment.gcal.transformCalendarNameToId
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.GoogleConnectorImpl
import at.cpickl.gadsu.service.clearMinutes
import at.cpickl.gadsu.testinfra.GoogleManualTest
import at.cpickl.gadsu.testinfra.buildDatabaseUrl
import at.cpickl.gadsu.testinfra.readGapiCredentialsFromSysProps
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import com.google.inject.Guice
import com.google.inject.Injector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.joda.time.DateTime
import org.testng.annotations.Test

@Test(groups = arrayOf("mTest")
        , enabled = false
)
class SyncManualTest {

    fun `WHEN insert client, create gcal DO sync and import ASSERT appointment in DB and gcal updated with gadsu metadata`() {
        GadsuSystemProperty.development.enable()
        val injector = Guice.createInjector(GadsuModule(Args.EMPTY.copy(databaseUrl = buildDatabaseUrl(javaClass))))
        initPreferences(injector)

        calendarSafe { gcals ->
            val givenClient = injector.getInstance(ClientService::class.java).insertOrUpdate(Client.unsavedValidInstance())
            val givenEvent = gcals.createEvent(GCalEvent.blankDummy().copy(description = javaClass.simpleName))!!

            val syncEvent = doTheImport(injector, givenClient, givenEvent)

            val insertedAppointment = assertAppointments(injector, givenClient, syncEvent)
            assertGCalEvent(gcals, givenEvent, givenClient, insertedAppointment)
        }
    }

    private fun initPreferences(injector: Injector) {
        val gapiCredentials = readGapiCredentialsFromSysProps()
        injector.getInstance(Prefs::class.java).preferencesData = PreferencesData.DEFAULT.copy(
                gcalName = GoogleManualTest.CALENDER_NAME,
                gapiCredentials = gapiCredentials
        )
    }

    private fun doTheImport(injector: Injector, client: Client, event: GCalEventMeta): GCalEvent {
        val syncer = injector.getInstance(SyncService::class.java)
        val report = syncer.syncAndSuggest()

        assertThat(report.eventsAndClients.size, equalTo(1))
        val (gcalEvent, clients) = report.eventsAndClients.entries.first()
        assertThat(gcalEvent.id, equalTo(event.id))
        assertThat(gcalEvent.gadsuId, nullValue())

        syncer.import(listOf(ImportAppointment(gcalEvent, true, client, clients)))

        return gcalEvent
    }

    private fun assertAppointments(injector: Injector, client: Client, event: GCalEvent): Appointment {
        val appointments = injector.getInstance(AppointmentRepository::class.java).findAllFor(client)
        assertThat(appointments, hasSize(1))
        val appointment = appointments[0]
        assertThat(appointment.clientId, equalTo(client.id!!))
        assertThat(appointment.gcalId, equalTo(event.id!!))
        return appointment
    }

    private fun assertGCalEvent(gcals: RealGCalRepository, event: GCalEventMeta, client: Client, appointment: Appointment) {
        val storedEvent = gcals.getEvent(event.id).toGCalEvent()
        assertThat(storedEvent.gadsuId, equalTo(appointment.id))
        assertThat(storedEvent.summary, equalTo(client.fullName))
    }

    private fun calendarSafe(function: (RealGCalRepository) -> Unit) {
        val now = DateTime.now().clearMinutes()
        val connector = GoogleConnectorImpl()
        val credentials = readGapiCredentialsFromSysProps()
        val calendar = connector.connectCalendar(credentials)
        val calendarId = transformCalendarNameToId(calendar, GoogleManualTest.CALENDER_NAME)
        calendar.deleteAllEvents(calendarId, now)
        val gcals = RealGCalRepository(calendar, calendarId)
        try {
            function(gcals)
        } finally {
            calendar.deleteAllEvents(calendarId, now)
        }
    }

}
