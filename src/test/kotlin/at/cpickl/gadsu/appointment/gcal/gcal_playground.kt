package at.cpickl.gadsu.appointment.gcal


/*
fun main(args: Array<String>) {
    GCalPlayground().apply {
        createNewEvent()
//        listEventsAndSaveToFile()
//        deleteEvent("hgeotfcs657bpl08thllithrvk")
//        updateEvent("aukh7uq8fhf61nguibvc3nv9ls")
    }

    println("done")
}


private class GCalPlayground {
    private val connector = GCalConnectorImpl()
    private val cal = connector.connect()
    private val gcal = GCalRepositoryImpl(connector, "gadsu")
    private val calendarId = cal.transformCalendarNameToId("gadsu")

    fun listEventsAndSaveToFile() {
        val now = DateTime.now()
        val events = gcal.listEvents(now.minusDays(30), now.plusDays(30))
        //            .forEach {
        //                val startDate = it.start.dateTime ?: it.start.date
        //                println("${it.summary} - $startDate")
        //            }
        XStream().toXML(events).saveToFile(File("gcal_events.xml").apply { println("Saving ${events.size} events to: $absolutePath") })
    }

    fun createNewEvent() {
        gcal.createEvent(calendarId, "die steffi ist da", "das wird sicher sehr toll", DateTime.now(), 120)
    }

    fun updateEvent(eventId: String) {
        gcal.updateEvent(calendarId, eventId, "new suuuumary")
    }

    fun deleteEvent(eventId: String) {
        gcal.deleteEvent(calendarId, eventId)
    }
}
*/
