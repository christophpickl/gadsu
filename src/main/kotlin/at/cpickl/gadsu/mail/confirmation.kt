package at.cpickl.gadsu.mail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.TemplatingEngine
import javax.inject.Inject

class AppointmentConfirmationer @Inject constructor(
        private val templating: TemplatingEngine,
        private val sender: MailSender
) {

    private val templateTextFromPrefs = "hello \${name}"


    // FIXME see: GCalSyncService#sendConfirmationMail
    fun confirm(clients: List<Client>) {

    }

}
