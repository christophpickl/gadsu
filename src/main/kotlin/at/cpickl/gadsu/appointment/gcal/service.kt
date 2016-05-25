package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.InternetConnectionLostEvent
import at.cpickl.gadsu.service.InternetConnectionStateChangedEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import com.google.api.services.calendar.Calendar
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.net.UnknownHostException
import javax.inject.Inject

data class GCalEventMeta(val id: String, val url: String)

interface GCalService {
    /**
     * @return the GCal event ID if was saved, null otherwise (offline or GCal not enabled)
     */
    fun createEvent(gCalEvent: GCalEvent): GCalEventMeta?

    fun updateEvent(gCalEvent: GCalUpdateEvent)

    fun deleteEvent(eventId: String)
}

@Logged
open class InternetConnectionAwareGCalService @Inject constructor(
        private val connector: GCalConnector,
        private val prefs: Prefs,
        private val bus: EventBus
) : GCalService {
    private val log = LOG(javaClass)

    private var repo: GCalRepository? = null

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
        if (!isGCalEnabled()) {
            return OfflineGCalRepository
        }
        log.info("Connecting to Google Calendar API.")
        val calendar: Calendar
        try {
            calendar = connector.connect()
        } catch (e: UnknownHostException) {
            bus.post(InternetConnectionLostEvent())
            return OfflineGCalRepository
        }
        val calendarName = prefs.preferencesData.gcalName!!
        val calendarId = transformCalendarNameToId(calendar, calendarName)
        return RealGCalRepository(calendar, calendarId)
    }

    private fun transformCalendarNameToId(calendar: Calendar, name: String): String {
        log.debug("transformCalendarNameToId(name={})", name)
        val calendars = calendar.calendarList().list().setMaxResults(100).execute().items
        return calendars.firstOrNull { it.summary.equals(name) }?.id ?:
                throw GadsuException("Could not find calendar by name '$name'! (Available calendars: ${calendars.map { it.summary }.joinToString(", ")})")
    }

    private fun isGCalEnabled() = prefs.preferencesData.gcalName != null

    private fun initRepo() {
        if (repo != null) {
            return
        }
        repo = connectRepo()
    }

}
