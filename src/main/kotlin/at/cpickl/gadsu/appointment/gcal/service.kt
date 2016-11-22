package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.GoogleConnector
import at.cpickl.gadsu.service.InternetConnectionLostEvent
import at.cpickl.gadsu.service.InternetConnectionStateChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.CalendarListEntry
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.net.UnknownHostException
import javax.inject.Inject

data class GCalEventMeta(val id: String, val url: String)

interface GCalService : GCalRepository


@Logged
open class InternetConnectionAwareGCalService @Inject constructor(
        private val connector: GoogleConnector,
        private val prefs: Prefs,
        private val bus: EventBus
) : GCalService {

    private val log = LOG(javaClass)
    private var repo: GCalRepository? = null

    override val isOnline: Boolean get() {
        initRepo()
        return repo is RealGCalRepository
    }

    override fun listEvents(start: DateTime, end: DateTime): List<GCalEvent> {
        initRepo()
        return repo!!.listEvents(start, end)
    }

    override fun createEvent(gCalEvent: GCalEvent): GCalEventMeta? {
        initRepo()
        return repo!!.createEvent(gCalEvent)
    }

    override fun updateEvent(gCalEvent: GCalUpdateEvent) {
        initRepo()
        repo!!.updateEvent(gCalEvent)
    }

    override fun deleteEvent(eventId: String) {
        initRepo()
        repo!!.deleteEvent(eventId)
    }

    @Subscribe open fun onInternetConnectionStateChangedEvent(event: InternetConnectionStateChangedEvent) {
        if (event.isConnected) {
            repo = connectRepo()
        } else {
            repo = OfflineGCalRepository
        }
    }

    private fun connectRepo(): GCalRepository {
        if (prefs.isGCalDisabled()) {
            return OfflineGCalRepository
        }
        log.info("Connecting to Google Calendar API.")
        val calendar: Calendar
        try {
            calendar = connector.connectCalendar()
        } catch (e: UnknownHostException) {
            bus.post(InternetConnectionLostEvent())
            return OfflineGCalRepository
        }
        val calendarName = prefs.preferencesData.gcalName!!
        val calendarId = transformCalendarNameToId(calendar, calendarName)
        return RealGCalRepository(calendar, calendarId)
    }

    private fun initRepo() {
        if (repo != null) {
            return
        }
        repo = connectRepo()
    }

}

fun Prefs.isGCalEnabled() = this.preferencesData.gcalName != null
fun Prefs.isGCalDisabled() = !isGCalEnabled()

val LOG_Calendar = LoggerFactory.getLogger(Calendar::class.java)!!

fun transformCalendarNameToId(calendar: Calendar, name: String): String {
    LOG_Calendar.debug("transformCalendarNameToId(name={})", name)

    val calendars: List<CalendarListEntry>
    try {
        calendars = calendar.calendarList().list().setMaxResults(100).execute().items
    } catch (e: GoogleJsonResponseException) {
        throw GCalException(e.statusCode, "GCal request failed with status code: ${e.statusCode}", e)
    }
    return calendars.firstOrNull { it.summary == name }?.id ?:
            throw GadsuException("Could not find calendar by name '$name'! (Available calendars: ${calendars.map { it.summary }.joinToString(", ")})")
}

class GCalException(
        val statusCode: Int,
        message: String,
        cause: Throwable? = null) : GadsuException("$message (status code = $statusCode)", cause) {

}
