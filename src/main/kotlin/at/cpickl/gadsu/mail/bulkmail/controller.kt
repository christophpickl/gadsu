package at.cpickl.gadsu.mail.bulkmail

import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.firstNotEmpty
import at.cpickl.gadsu.mail.Mail
import at.cpickl.gadsu.mail.MailPreferencesData
import at.cpickl.gadsu.mail.MailSender
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.TemplatingEngine
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

open class BulkMailController @Inject constructor(
        private val prefs: Prefs,
        private val mailSender: MailSender,
        private val clientService: ClientService,
        private val view: BulkMailView,
        private val dialogs: Dialogs,
        private val asyncWorker: AsyncWorker,
        private val templating: TemplatingEngine
) {

    private val log = LOG(javaClass)

    @Subscribe open fun onRequestOpenBulkMailEvent(event: RequestPrepareBulkMailEvent) {
        if (!ensurePreferencesSet()) {
            return
        }

        val mailEnabledClients = clientService.findAllForMail()
        view.initClients(mailEnabledClients)

        val mailPrefs = prefs.mailPreferencesData
        view.initSubject(mailPrefs.subject)
        view.initBody(mailPrefs.body)

        view.start()
    }

    @Subscribe open fun onRequestSendBulkMailEvent(event: RequestSendBulkMailEvent) {
        val mails = readMailsFromView() ?: return

        asyncWorker.doInBackground(AsyncDialogSettings("Versende Mail", "Verbindung zu GMail wird aufgebaut und Mails versendet ..."),
                {
                    mails.forEach {
                        mailSender.send(it)
                    }
                },
                {
                    dialogs.show(
                            title = "Mail versendet",
                            message = "Die Mail wurde an ${mails.size} Empfänger erfolgreich versendet.",
                            overrideOwner = view.asJFrame())
                    view.closeWindow()
                },
                { e ->
                    log.error("Failed to send mail!", e)
                    val detailMessage =
                            if (e is GoogleJsonResponseException)
                                "\n(code: ${e.statusCode}, message: ${e.statusMessage})" //details=${e.details.map { "${it.key}: ${it.value}" }.join(", ")})"
                            else ""
                    dialogs.show(
                            title = "Mail versendet",
                            message = "Beim Versenden der Mail ist ein Fehler aufgetreten!$detailMessage",
                            type = DialogType.ERROR,
                            overrideOwner = view.asJFrame())
                }
        )
    }

    @Subscribe open fun onMailWindowClosedEvent(event: BulkMailWindowClosedEvent) {
        if (!event.shouldPersistState) {
            return
        }
        val subject = view.readSubject()
        val body = view.readBody()

        prefs.mailPreferencesData = MailPreferencesData(subject, body)
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        view.destroy()
    }

    private fun ensurePreferencesSet(): Boolean {
        if (prefs.preferencesData.gmailAddress == null) {
            showDialog("Um Mails zu versenden muss zuerst eine GMail Adresse in den Einstellungen angegeben werden.")
            return false
        }
        if (prefs.preferencesData.gapiCredentials == null) {
            showDialog("Um Mails zu versenden müssen die Google API credentials gesetzt sein.")
            return false
        }
        return true
    }

    private fun readMailsFromView(): List<Mail>? {
        val recipients = view.readRecipients()
        val subject = view.readSubject()
        val body = view.readBody()

        if (recipients.isEmpty()) {
            dialogs.showInvalidInput("Es muss zumindest ein Klient ausgewählt sein!")
            return null
        }
        if (subject.isEmpty() || body.isEmpty()) {
            dialogs.showInvalidInput("Betreff und Mailinhalt darf nicht leer sein!")
            return null
        }

        return recipients.map {
            val data = mapOf("name" to firstNotEmpty(it.nickNameExt, it.firstName))
            val processedSubject = templating.process(subject, data)
            val processedBody = templating.process(body, data)
            Mail(it.contact.mail, processedSubject, processedBody)
        }
    }

    private fun Dialogs.showInvalidInput(message: String) {
        dialogs.show(
                title = "Ungültige Eingabe",
                message = message,
                type = DialogType.WARN,
                overrideOwner = view.asJFrame())
    }

    private fun showDialog(message: String) {
        dialogs.show(title = "Mail senden", message = message, type = DialogType.WARN)
    }

    private fun ClientService.findAllForMail(): List<Client> {
        return findAll(ClientState.ACTIVE).filter { it.wantReceiveMails && it.contact.mail.isNotBlank() }
    }

}
