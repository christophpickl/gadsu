package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.GADSU_DIRECTORY
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.LOG
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.inject.AbstractModule
import com.google.inject.Provider
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import javax.inject.Inject

// https://developers.google.com/google-apps/calendar/quickstart/java
// https://developers.google.com/google-apps/calendar/v3/reference/

class GCalModule : AbstractModule() {
    override fun configure() {

        bind(GCalConnector::class.java).toInstance(GCalConnectorImpl())

        bind(GCalRepositoryImpl::class.java).to(GCalRepository::class.java)
    }
}

interface GCalRepository {
    fun listEvents(startDate: org.joda.time.DateTime,
                   endDate: org.joda.time.DateTime,
                   maxResults: Int = 100
    ): List<Event>
}
class CalendarIdProvider @Inject constructor(
        private val connector: GCalConnector
) : Provider<String> {

    var calendarName: String? = null

    private var cachedCalendarId: String? = null
    override fun get(): String {
        if (cachedCalendarId == null) {
            if (calendarName == null) {

            }
            val cal = connector.connect()
            cachedCalendarId = cal.transformCalendarNameToId(calendarName!!)
        }
        return cachedCalendarId!!

    }

}
class GCalRepositoryImpl @Inject constructor(
        private val connector: GCalConnector,
        private val calendarName: String // this means, changing the calendar at runtime is not support, but... not needed that urgent anyway ;)
) : GCalRepository {


    private lateinit var calendarId: String
    private val cal: Calendar by lazy {
        val connection = connector.connect()
        calendarId = connection.transformCalendarNameToId(calendarName)
        connection
    }

    override fun listEvents(startDate: org.joda.time.DateTime,
                            endDate: org.joda.time.DateTime,
                            maxResults: Int
    ): List<Event> {
        return cal.events().list(calendarId)
                .setMaxResults(maxResults)
                .setTimeMin(startDate.toGDateTime())
                .setTimeMax(endDate.toGDateTime())
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute().items
    }

    // https://developers.google.com/google-apps/calendar/create-events
    fun createEvent(calendarId: String, summary: String, description: String, start: org.joda.time.DateTime, durationInMin: Int) {
        val newEvent = Event()
//                .setId(UUID.randomUUID().toString()) // possible to set custom ID (which will be the same as the AppointmentID)
                .setSummary(summary)
//                .setAttendees()
//                .setReminders()
                .setDescription(description)
                .setStart(start.toGEventDateTime())
                .setEnd(start.plusMinutes(durationInMin).toGEventDateTime())
//                .setExtendedProperties(Event.ExtendedProperties().set("MYappointmentId", "fuchur"))
                .set("MYappointmentId", "fuchur")
        val savedEvent = cal.events().insert(calendarId, newEvent).execute()
        println("Saved event: ${savedEvent.htmlLink}")
        println("ID: ${savedEvent.id}")
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/update#examples
    fun updateEvent(calendarId: String, eventId: String, newSummary: String) {
        val updateEvent = cal.events().get(calendarId, eventId).execute()
        updateEvent.summary = newSummary
        cal.events().update(calendarId, eventId, updateEvent).execute()
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/delete#examples
    fun deleteEvent(calendarId: String, eventId: String) {
        cal.events().delete(calendarId, eventId).execute()
    }
}

interface GCalConnector {
    fun connect(): Calendar
}

class GCalConnectorImpl constructor(
        private val applicationName: String = "gadsu",
        private val credentialsReader: Reader = InputStreamReader(GCalConnectorImpl::class.java.getResourceAsStream("/gadsu/gcal_client_secret.json")),
        private val dataStore: File = File(GADSU_DIRECTORY, "gcal_datastore.json")
) : GCalConnector {
    private val log = LOG(javaClass)

    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val scopes = CalendarScopes.all()
    private val dataStoreFactory = FileDataStoreFactory(dataStore)
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    private val lazyConnection: Calendar by lazy {
        log.trace("Connecting to GMail Calendar.")
        val credential = authorize()
        Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build()
    }
    override fun connect(): Calendar = lazyConnection

    private fun authorize(): Credential {
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsReader)
        val authFlow = GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build()
        val credential = AuthorizationCodeInstalledApp(authFlow, LocalServerReceiver()).authorize("user")
        log.debug("Credentials saved to: ${dataStore.absolutePath}")
        return credential
    }

}

private val log = LOG(Calendar::class.java as Class<Any>)
fun Calendar.transformCalendarNameToId(name: String): String {
    log.debug("transformCalendarNameToId(name={})", name)
    val calendars = this.calendarList().list().setMaxResults(100).execute().items
    return calendars.firstOrNull { it.summary.equals(name) }?.id ?: throw GadsuException("Could not find calendar by name '$name'! " +
            "(Available calendars: ${calendars.map { it.summary }.joinToString(", ")})")
}
