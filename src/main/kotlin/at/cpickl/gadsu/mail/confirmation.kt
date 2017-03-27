package at.cpickl.gadsu.mail

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.TemplatingEngine
import com.google.common.annotations.VisibleForTesting
import javax.inject.Inject

interface AppointmentConfirmationer {

    fun sendConfirmation(client: Client, appointment: Appointment)

}

class AppointmentConfirmationerImpl @Inject constructor(
        private val templating: TemplatingEngine,
        private val mailSender: MailSender,
        private val prefs: Prefs
) : AppointmentConfirmationer {


    // see: GCalSyncService
    override fun sendConfirmation(client: Client, appointment: Appointment) {
        client.validateTemplateData()

        val subjectTemplate = prefs.preferencesData.templateConfirmSubject ?: throw GadsuException("confirm subject not set!")
        val bodyTemplate = prefs.preferencesData.templateConfirmBody?: throw GadsuException("confirm body not set!")

        mailSender.send(buildMail(subjectTemplate, bodyTemplate, client, appointment))
    }

    @VisibleForTesting fun buildMail(subjectTemplate: String, bodyTemplate: String, client: Client, appointment: Appointment): Mail {
        val data = mapOf(
                "name" to client.firstName,
                "dateStart" to appointment.start.toDate(),
                "dateEnd" to appointment.end.toDate(),
                "gender" to client.gender.sqlCode
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
