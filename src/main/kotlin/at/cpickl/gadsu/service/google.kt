package at.cpickl.gadsu.service

import at.cpickl.gadsu.GADSU_DIRECTORY
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
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import java.io.File
import java.io.InputStreamReader
import java.io.Reader


interface GoogleConnector {
    fun connectCalendar(): Calendar
    fun connectGmail(): Gmail
}

// https://developers.google.com/gmail/api/quickstart/java
// https://developers.google.com/gmail/api/guides/sending

class GoogleConnectorImpl : GoogleConnector {
    companion object {
        private val APPLICATION_NAME: String = "gadsu"
        private val CREDENTIALS: Reader = InputStreamReader(GoogleConnectorImpl::class.java.getResourceAsStream("/gadsu/google_client_secret.json"))
        private val DATA_STORE: File = File(GADSU_DIRECTORY, "google_datastore.json")

        private val SCOPES_CALENDAR = CalendarScopes.all()
        private val SCOPES_GMAIL = listOf(GmailScopes.GMAIL_SEND)
    }

    private val log = LOG(javaClass)

    private val jsonFactory = JacksonFactory.getDefaultInstance()

    private val dataStoreFactory = FileDataStoreFactory(DATA_STORE)
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    override fun connectCalendar(): Calendar {
        log.debug("Connecting to GMail Calendar.")
        val credential = authorize(SCOPES_CALENDAR)
        return Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build()
    }

    override fun connectGmail() = Gmail.Builder(httpTransport, jsonFactory, authorize(SCOPES_GMAIL))
            .setApplicationName(APPLICATION_NAME)
            .build()!!

    private fun authorize(scopes: Collection<String>): Credential {
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, CREDENTIALS)
        val authFlow = GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .build()
        val credential = AuthorizationCodeInstalledApp(authFlow, LocalServerReceiver()).authorize("user")
        log.debug("Credentials saved to: ${DATA_STORE.absolutePath}")
        return credential
    }

}
