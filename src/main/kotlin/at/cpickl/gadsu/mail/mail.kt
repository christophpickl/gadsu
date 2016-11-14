package at.cpickl.gadsu.mail

import at.cpickl.gadsu.service.GoogleConnector
import at.cpickl.gadsu.service.GoogleConnectorImpl
import at.cpickl.gadsu.service.LOG
import com.google.api.client.util.Base64
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.Properties
import java.util.regex.Pattern
import javax.inject.Inject
import javax.mail.Message.RecipientType
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


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

interface MailSender {
    fun send(mail: Mail, myAddress: String)
}


/**
 * myAddress used as the sender and (visible) receiver for sent mails.
 */
class GMailSender @Inject constructor(
        private val googleConnector: GoogleConnector
        ) : MailSender {

    private val log = LOG(javaClass)
    
    override fun send(mail: Mail, myAddress: String) {
        log.info("send(mail={})", mail)

        val message = mail.toMimeMessage(myAddress).toMessage()
        val gmail = googleConnector.connectGmail()
        val sent = gmail.users().messages().send(myAddress, message).execute();
        log.debug("Sent message id: {}", sent.id)
    }

    private fun Mail.toMimeMessage(myAddress: String): MimeMessage {
        val session = Session.getDefaultInstance(Properties(), null)
        val email = MimeMessage(session)

        email.setFrom(InternetAddress(myAddress))
        email.addRecipient(RecipientType.TO, InternetAddress(myAddress))
        email.addRecipients(RecipientType.BCC, this.recipients.map(::InternetAddress).toTypedArray())
        email.subject = this.subject
        email.setText(this.body)

        return email
    }

    private fun MimeMessage.toMessage(): Message {
        val buffer = ByteArrayOutputStream()
        this.writeTo(buffer)
        val bytes = buffer.toByteArray()
        val encodedEmail = Base64.encodeBase64URLSafeString(bytes)
        val message = Message()
        message.raw = encodedEmail
        return message
    }

}

fun main(args: Array<String>) {
    val sender = GMailSender(GoogleConnectorImpl())
    sender.send(Mail(listOf("gadsu1@discard.email", "gadsu2@discard.email"), "my test subject 2", "my test body"), "christoph.pickl@gmail.com")
}

private val mailPattern = Pattern.compile("""^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$""")
fun String.isNotValidMail() = !this.isValidMail()
fun String.isValidMail(): Boolean {
    return mailPattern.matcher(this).matches()
}
