package at.cpickl.gadsu.appointment.gcal

/*

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



}


*/