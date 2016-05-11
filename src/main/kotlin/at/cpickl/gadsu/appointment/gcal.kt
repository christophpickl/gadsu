package at.cpickl.gadsu.appointment

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
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import java.io.File
import java.io.InputStreamReader
import java.io.Reader

// https://developers.google.com/google-apps/calendar/quickstart/java
// https://developers.google.com/google-apps/calendar/v3/reference/

class GCalService() {

    private val cal: Lazy<com.google.api.services.calendar.Calendar>
        get() = lazy {
            GCalConnector(
                    applicationName = "gadsu",
                    dataStore = File(GADSU_DIRECTORY, "gcal_datastore.json"),
                    credentialsReader = InputStreamReader(javaClass.getResourceAsStream("/gadsu/gcal_client_secret.json")))
                    .calendarService()
        }

    fun readFutureEvents(calendarId: String): List<Event> {
        val now = DateTime(System.currentTimeMillis())
        val events: List<Event> = cal.value.events().list(calendarId)

                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute().items

        println("Found events: ${events.size}")
        return events
    }

    fun transformCalendarNameToId(name: String): String {
        val calendars = cal.value.calendarList().list().setMaxResults(100).execute().items
        return calendars.firstOrNull { it.summary.equals(name) }?.id ?: throw GadsuException("Not found calendar by name '$name'!")
    }

    // https://developers.google.com/google-apps/calendar/create-events
    fun createEvent(calendarId: String, summary: String, description: String, start: org.joda.time.DateTime, durationInMin: Int) {
        val newEvent = Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(start.toGcalDate())
                .setEnd(start.plusMinutes(durationInMin).toGcalDate())
        val savedEvent = cal.value.events().insert(calendarId, newEvent).execute()
        println("Saved event: ${savedEvent.htmlLink}")
        println("ID: ${savedEvent.id}")
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/update#examples
    fun updateEvent(calendarId: String, eventId: String, newSummary: String) {
        val updateEvent = cal.value.events().get(calendarId, eventId).execute()
        updateEvent.setSummary(newSummary)
        cal.value.events().update(calendarId, eventId, updateEvent).execute()
    }

    // https://developers.google.com/google-apps/calendar/v3/reference/events/delete#examples
    fun deleteEvent(calendarId: String, eventId: String) {
        cal.value.events().delete(calendarId, eventId).execute()
    }
}

fun org.joda.time.DateTime.toGcalDate(): EventDateTime {
    return EventDateTime().setDateTime(com.google.api.client.util.DateTime(this.millis)) // no timezone
}

private class GCalConnector constructor(
        private val applicationName: String,
        private val credentialsReader: Reader,
        private val dataStore: File
) {
    private val log = LOG(javaClass)

    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val scopes = CalendarScopes.all()
    private val dataStoreFactory = FileDataStoreFactory(dataStore)
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    private fun authorize(): Credential {
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsReader)
        val authFlow = GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build()
        val credential = AuthorizationCodeInstalledApp(authFlow, LocalServerReceiver()).authorize("user")
        log.debug("Credentials saved to ${dataStore.absolutePath}")
        return credential
    }

    fun calendarService(): com.google.api.services.calendar.Calendar {
        log.trace("Creating new calendar service.")
        val credential = authorize()
        return com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(applicationName).build()
    }

}
