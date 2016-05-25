package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.GADSU_DIRECTORY
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
import java.io.File
import java.io.InputStreamReader
import java.io.Reader


interface GCalConnector {
    fun connect(): Calendar
}

class GCalConnectorImpl : GCalConnector {
    companion object {
        private val APPLICATION_NAME: String = "gadsu"
        private val CREDENTIALS: Reader = InputStreamReader(GCalConnectorImpl::class.java.getResourceAsStream("/gadsu/gcal_client_secret.json"))
        private val DATA_STORE: File = File(GADSU_DIRECTORY, "gcal_datastore.json")
    }
    private val log = LOG(javaClass)

    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val scopes = CalendarScopes.all()
    private val dataStoreFactory = FileDataStoreFactory(DATA_STORE)
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    override fun connect(): Calendar {
        log.debug("Connecting to GMail Calendar.")
        val credential = authorize()
        return Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build()
    }

    private fun authorize(): Credential {
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