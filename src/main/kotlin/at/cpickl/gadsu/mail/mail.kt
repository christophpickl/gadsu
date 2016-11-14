package at.cpickl.gadsu.mail

import at.cpickl.gadsu.service.LOG
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Base64
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStreamReader
import java.util.Properties
import java.util.regex.Pattern
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


interface MailSender {
    fun send(mail: Mail)
}

data class Mail(
        val recipients: List<String>,
        val subject: String,
        val body: String
) {
    init {
        if (recipients.isEmpty()) {
            throw IllegalArgumentException("Recipients must not be empty! ($this)")
        }
    }
}

class GMailSender : MailSender {
    
    private val log = LOG(javaClass)
    
    override fun send(mail: Mail) {
        log.info("send(mail={})", mail)
    }
}

// also see: GCalConnector

// https://developers.google.com/gmail/api/quickstart/java
// https://developers.google.com/gmail/api/guides/sending
class GConector {
    companion object {
        private val APPLICATION_NAME: String = "gadsu"
    }
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

    private fun authorize(): Credential {

        val CREDENTIALS = InputStreamReader(GConector::class.java.getResourceAsStream("/gadsu/gcal_client_secret.json"))
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, CREDENTIALS)

        val SCOPES = listOf(GmailScopes.GMAIL_SEND)

        val DATA_STORE_DIR = File(System.getProperty("user.home"), ".gadsu/gmail_datastore")
        val DATA_STORE_FACTORY = FileDataStoreFactory(DATA_STORE_DIR)

        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .setAccessType("offline")
            .build()

        val credential = AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
        println("Credentials saved to: " + DATA_STORE_DIR.absolutePath);
        return credential!!
    }

    fun gmailService() = Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize())
            .setApplicationName(APPLICATION_NAME)
            .build()!!
}

fun createMail(): MimeMessage {
    val session = Session.getDefaultInstance(Properties(), null)
    val email = MimeMessage(session)

    val from = "christoph.pickl@gmail.com"
    val to = "christoph.pickl@gmail.com"
    email.setFrom(InternetAddress(from))
    email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
    email.setSubject("my subj")
    email.setText("bodyyyy")

    return email
}

fun createMessageWithEmail(emailContent: MimeMessage): Message {
    val buffer = ByteArrayOutputStream()
    emailContent.writeTo(buffer)
    val bytes = buffer.toByteArray()
    val encodedEmail = Base64.encodeBase64URLSafeString(bytes)
    val message = Message()
    message.raw = encodedEmail
    return message
}


fun main(args: Array<String>) {
    val gmail = GConector().gmailService()

    val mail = createMessageWithEmail(createMail())

    val sent = gmail.users().messages().send("christoph.pickl@gmail.com", mail).execute();

    println("Message id: " + sent.getId());
    println(sent.toPrettyString());
}

private val mailPattern = Pattern.compile("""^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$""")
fun String.isNotValidMail() = !this.isValidMail()
fun String.isValidMail(): Boolean {
    return mailPattern.matcher(this).matches()
}
