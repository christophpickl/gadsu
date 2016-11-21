package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.parseDate
import org.joda.time.DateTime
import javax.inject.Inject

interface GCalSyncer {

    fun loadGCalEvents(): List<GCalEvent>

}


class GCalSyncerImpl @Inject constructor(
        private val gcal: GCalService

) : GCalSyncer {

    private val log = LOG(javaClass)

    override fun loadGCalEvents(): List<GCalEvent> {
        if (!gcal.isOnline) {
            throw IllegalStateException("can not sync: gcal is not online!")
        }

        val (start, end) = loadStartEnd()

        val gcalEvents = gcal.listEvents(start, end)
        log.trace("gcal listed ${gcalEvents.size} events")

        return gcalEvents

    }

    private fun loadStartEnd(): Pair<DateTime, DateTime> {
        // FIXME start = load start from preferences OR at least two weeks ago; end = today + 4 weeks
        return Pair(
                "21.11.2016".parseDate(),
                "25.11.2016".parseDate()
        )
    }

}
