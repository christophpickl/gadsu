package at.cpickl.gadsu.mail

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.firstNotEmpty
import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.TemplateData
import at.cpickl.gadsu.service.TemplateDeclaration
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
        val data = AppointmentConfirmationTemplateDeclaration.process(client to appointment)
        val subject = templating.process(subjectTemplate, data)
        val body = templating.process(bodyTemplate, data)
        return Mail(client.contact.mail, subject, body, recipientsAsBcc = false)
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

object AppointmentConfirmationTemplateDeclaration : TemplateDeclaration<Pair<Client, Appointment>> {
    override val data = listOf(
            TemplateData<Pair<Client, Appointment>>("name", "Der externe Spitzname bzw. Vorname falls nicht vorhanden, zB: \${name?lower_case}") {
                firstNotEmpty(it.first.nickNameExt, it.first.firstName)
            },
            TemplateData("dateStart", "Z.B.: termin am \${dateStart?string[\"EEEE 'der' d. MMMMM\"]?lower_case} von \${dateStart?string[\"HH:mm\"]}") {
                it.second.start.toDate()
            },
            TemplateData("dateEnd", "Z.B.: bis \${dateEnd?string[\"HH:mm\"]} uhr") {
                it.second.end.toDate()
            },
            TemplateData("gender", "Z.B.: hallo <#if gender == \"M\">lieber <#elseif gender == \"F\">liebe </#if>") {
                it.first.gender.sqlCode
            }
    )
}
