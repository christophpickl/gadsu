package at.cpickl.gadsu.mail

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

data class MailPreferencesData(
        val recipientClientIds: List<String>,
        val subject: String,
        val body: String
)

open class MailController @Inject constructor(
        private val prefs: Prefs,
        private val mailSender: MailSender,
        private val clientService: ClientService,
        private val view: MailView,
        private val dialogs: Dialogs,
        private val asyncWorker: AsyncWorker
) {

    private val log = LOG(javaClass)

    @Subscribe open fun onRequestOpenBulkMailEvent(event: RequestPrepareMailEvent) {
        if (prefs.preferencesData.gmailAddress == null) {
            dialogs.show(
                    title = "Mail senden",
                    message = "Um Mails zu versenden muss zuerst eine GMail Adresse in den Einstellungen angegeben werden.",
                    type = DialogType.WARN
            )
            return
        }
        val mailEnabledClients = clientService.findAllForMail()
        view.initClients(mailEnabledClients)

        val mailPrefs = prefs.mailPreferencesData
        val preselectedClients = mailEnabledClients.filter { mailPrefs.recipientClientIds.contains(it.id) }

        view.initSelectedClients(preselectedClients)
        view.initSubject(mailPrefs.subject)
        view.initBody(mailPrefs.body)
        view.start()
    }

    @Subscribe open fun onRequestSendBulkMailEvent(event: RequestSendMailEvent) {
        val recipients = view.readRecipients()
        if (recipients.isEmpty()) {
            dialogs.show(
                    title = "Ungültige Eingabe",
                    message = "Es muss zumindest ein Klient ausgewählt sein!",
                    type = DialogType.WARN,
                    overrideOwner = view.asJFrame())
            return
        }
        val subject = view.readSubject()
        val body = view.readBody()
        if (subject.isEmpty() || body.isEmpty()) {
            dialogs.show(
                    title = "Ungültige Eingabe",
                    message = "Betreff und Mailinhalt darf nicht leer sein!",
                    type = DialogType.WARN,
                    overrideOwner = view.asJFrame())
            return
        }

        val myAddress = prefs.preferencesData.gmailAddress!!
        asyncWorker.doInBackground(AsyncDialogSettings("Versende Mail", "Verbindung zu GMail wird aufgebaut ..."),
                { mailSender.send(Mail(recipients.map { it.contact.mail }, subject, body), myAddress) },
                { dialogs.show(
                        title = "Mail versendet",
                        message = "Die Mail wurde an ${recipients.size} Empfänger erfolgreich versendet.",
                        overrideOwner = view.asJFrame())
                    view.closeWindow() },
                { e ->
                    log.error("Failed to send mail!", e)
                    dialogs.show(
                        title = "Mail versendet",
                        message = "Beim Versenden der Mail ist ein Fehler aufgetreten!",
                        type = DialogType.ERROR,
                        overrideOwner = view.asJFrame()) }
        )
    }

    @Subscribe open fun onMailWindowClosedEvent(event: MailWindowClosedEvent) {
        if (!event.shouldPersistState) {
            return
        }
        val recipients = view.readRecipients()
        val subject = view.readSubject()
        val body = view.readBody()

        prefs.mailPreferencesData = MailPreferencesData(recipients.map { it.id!! }, subject, body)
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        view.destroy()
    }

    private fun ClientService.findAllForMail(): List<Client> {
        return findAll(ClientState.ACTIVE).filter { it.wantReceiveDoodleMails && it.contact.mail.isNotBlank() }
    }

}
