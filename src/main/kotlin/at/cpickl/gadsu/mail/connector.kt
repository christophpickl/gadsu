package at.cpickl.gadsu.mail

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
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import java.io.File
import java.io.Reader
import java.io.StringReader


interface GMailServerConector {
    fun connect(credentials: GapiCredentials): Gmail
}

// https://developers.google.com/gmail/api/quickstart/java
// https://developers.google.com/gmail/api/guides/sending
class GMailServerConectorImpl : GMailServerConector {
    companion object {
        private val APPLICATION_NAME: String = "gadsu"
        private val DATA_STORE_DIR = File(GADSU_DIRECTORY, "gmail_datastore")
        private val SCOPES = GmailScopes.all()
    }

    private val log = LOG(javaClass)
    private val jsonFactory = JacksonFactory.getDefaultInstance()
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

    override fun connect(credentials: GapiCredentials) = Gmail.Builder(
            httpTransport,
            jsonFactory,
            authorize(credentials)
    )
            .setApplicationName(APPLICATION_NAME)
            .build()!!

    private fun authorize(credentials: GapiCredentials): Credential {
        val credentialsReader: Reader = StringReader(buildClientSecretJson(credentials))
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, credentialsReader)

        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(DATA_STORE_DIR))
                .setAccessType("offline")
                .build()

        val credential = AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
        log.debug("Credentials saved to: {}", DATA_STORE_DIR.absolutePath);
        return credential!!
    }

    private fun buildClientSecretJson(credentials: GapiCredentials) = """
{
  "installed": {
    "client_id": "${credentials.clientId}",
    "client_secret": "${credentials.clientSecret}",
    "project_id": "lithe-bazaar-130716",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://accounts.google.com/o/oauth2/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "redirect_uris": [
      "urn:ietf:wg:oauth:2.0:oob",
      "http://localhost"
    ]
  }
}
"""
}
