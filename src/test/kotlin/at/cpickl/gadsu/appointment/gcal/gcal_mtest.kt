package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.testinfra.GoogleManualTest
import com.google.api.services.calendar.Calendar
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.empty
import org.joda.time.DateTime
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test(groups = arrayOf("mTest")
        , enabled = false
)
class GcalManualTest : GoogleManualTest() {

    private val log = LOG(javaClass)

    private val gEvent = GCalEvent.withoutIdAndUrl()

    private lateinit var calendarId: String
    private lateinit var calendar: Calendar
    private lateinit var repo: RealGCalRepository

    @BeforeClass fun initClassState() {
        calendar = connector.connectCalendar(credentials)
        calendarId = transformCalendarNameToId(calendar, CALENDER_NAME)
        calendar.deleteAllEvents(calendarId, now)
    }

    @BeforeMethod fun initMethodState() {
        repo = RealGCalRepository(calendar, calendarId)
    }

    @AfterMethod fun cleanUpEvents() {
        calendar.deleteAllEvents(calendarId, now)
    }

    fun `create and list`() {
        val created = createEvent(gEvent)

        assertThat(listEvents(),
                contains(gEvent.copy(id = created.id, url = created.url)))

    }

    fun `update an event`() {
        val created = createEvent(gEvent)
        val updateGEvent = GCalUpdateEvent(created.id, "updatedGadsuId", "updatedClientId", "updatedSummary", now.plusDays(1), now.plusDays(1).plusHours(1))

        repo.updateEvent(updateGEvent)

        assertThat(listEvents(),
                contains(gEvent.copy(
                        id = created.id,
                        gadsuId = updateGEvent.gadsuId,
                        clientId = updateGEvent.clientId,
                        summary = updateGEvent.summary,
                        start = updateGEvent.start,
                        end = updateGEvent.end,
                        url = created.url
                )))

    }

    fun `delete yet deleted does not throw an exception`() {
        val created = createEvent(gEvent)

        repo.deleteEvent(created.id)
        assertThat(listEvents(), empty())
        repo.deleteEvent(created.id)
    }

    private fun createEvent(gEvent: GCalEvent) = repo.createEvent(gEvent)!!

    private fun listEvents() = repo.listEvents(now.minusYears(1), now.plusYears(1))


}

fun Calendar.deleteAllEvents(calendarId: String, now: DateTime) {
    events().list(calendarId)
            .setTimeMin(now.minusYears(10).toGDateTime())
            .setTimeMax(now.plusYears(10).toGDateTime())
            .setMaxResults(1000)
            .execute().items.forEach {
//        log.debug("deleting left over gcal event: {}", it)
        events().delete(calendarId, it.id).execute()
    }
}
