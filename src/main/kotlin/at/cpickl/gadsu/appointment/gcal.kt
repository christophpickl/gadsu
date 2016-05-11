package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.GADSU_DIRECTORY
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
import com.google.common.io.Files
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Reader
import java.util.Base64

fun main(args: Array<String>) {

    val gcal = GCalService()
    val events = gcal.readFutureEvents()

    val eventsFile = File("events.dat")
//    events.serialize().saveToFile(eventsFile)
    // val events = eventsFile.readContent().deserialize()

//            .forEach {
//                val startDate = it.start.dateTime ?: it.start.date
//                println("${it.summary} - $startDate")
//            }
    println("done")
}

fun String.saveToFile(target: File) {
    Files.write(this, target, Charsets.UTF_8)
}

fun File.readContent(): String {
    return Files.toString(this, Charsets.UTF_8)
}


fun String.deserialize(): Any  {
    val data = Base64.getDecoder().decode(this)
    val ois = ObjectInputStream(ByteArrayInputStream(data))
    val o = ois.readObject()
    ois.close()
    return o
}

fun <T> T.serialize(): String {
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(this)
    oos.close()
    return Base64.getEncoder().encodeToString(baos.toByteArray())
}

class GCalService {
    private val lowLevel = GCalLowLevel(
            applicationName = "gadsu",
            dataStore = File(GADSU_DIRECTORY, "gcal_datastore.json"),
            credentialsReader = InputStreamReader(javaClass.getResourceAsStream("/gadsu/gcal_client_secret.json"))
    )

    fun readFutureEvents(): List<Event> {
        val cal = lowLevel.calendarService()

        val now = DateTime(System.currentTimeMillis())
        val events: List<Event> = cal.events().list("primary")
                .setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute().items

        println("Found events: ${events.size}")
        return events

    }
}

private class GCalLowLevel constructor(
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
