package at.cpickl.gadsu.mail

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.TemplatingEngine
import com.google.common.annotations.VisibleForTesting
import javax.inject.Inject

interface AppointmentConfirmationer {

    fun confirm(subjectTemplate: String, bodyTemplate: String, client: Client, appointment: Appointment)

}

class AppointmentConfirmationerImpl @Inject constructor(
        private val templating: TemplatingEngine,
        private val mailSender: MailSender
) : AppointmentConfirmationer {


    // see: GCalSyncService
    override fun confirm(subjectTemplate: String, bodyTemplate: String, client: Client, appointment: Appointment) {
        client.validateTemplateData()

        mailSender.send(buildMail(subjectTemplate, bodyTemplate, client, appointment))
    }

    @VisibleForTesting fun buildMail(subjectTemplate: String, bodyTemplate: String, client: Client, appointment: Appointment): Mail {
        val data = mapOf(
                "name" to client.firstName,
                "date" to appointment.start.toDate() // TODO #87 @confirm - should we also display the end/length?
        )
        val subject = templating.process(subjectTemplate, data)
        val body = templating.process(bodyTemplate, data)
        return Mail(client.contact.mail, subject, body)
    }

    private fun Client.validateTemplateData() {
        if (contact.mail.isEmpty()) {
            throw AppointmentConfirmationException("Client mail is not configured for: $this")
        }
        if (firstName.isEmpty()) {
            throw AppointmentConfirmationException("Client mail is not configured for: $this")
        }
    }

}

class AppointmentConfirmationException(message: String, cause: Exception? = null) : GadsuException(message, cause)
