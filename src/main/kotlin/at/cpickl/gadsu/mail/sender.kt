package at.cpickl.gadsu.mail

import at.cpickl.gadsu.service.GoogleConnector
import at.cpickl.gadsu.service.LOG
import com.google.api.client.util.Base64
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.inject.Inject
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


interface MailSender {
    /**
     * @param myAddress (user ID) used as the sender and (visible) receiver for sent mails.
     */
    fun send(mail: Mail, myAddress: String, credentials: GapiCredentials): Message
}

class GMailSender @Inject constructor(
        private val connector: GoogleConnector
        ) : MailSender {

    private val log = LOG(javaClass)

    override fun send(mail: Mail, myAddress: String, credentials: GapiCredentials): Message {
        log.info("send(mail={}, myAddress='{}', credentials)", mail, myAddress)

        val message = mail.toMimeMessage(myAddress).toMessage()
        val gmail = connector.connectGmail(credentials)

        val sent = gmail.users().messages().send(myAddress, message).execute();
        log.debug("Sent message id: {}", sent.id)
        return sent!!
    }

    private fun Mail.toMimeMessage(myAddress: String): MimeMessage {
        val session = Session.getDefaultInstance(Properties(), null)
        val email = MimeMessage(session)

        email.setFrom(InternetAddress(myAddress))
        email.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(myAddress))
        email.addRecipients(javax.mail.Message.RecipientType.BCC, this.recipients.map(::InternetAddress).toTypedArray())
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
