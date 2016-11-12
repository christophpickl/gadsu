package at.cpickl.gadsu.mail


import at.cpickl.gadsu.QuitEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.closeOnEscape
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel

open class MailController @Inject constructor(
        private val mailSender: MailSender,
        private val clientService: ClientService,
        private val view: MailView,
        private val dialogs: Dialogs
) {

    @Subscribe open fun onRequestOpenBulkMailEvent(event: RequestPrepareMailEvent) {
        view.initClients(clientService.findAll(ClientState.ACTIVE).filter { it.contact.mail.isNotBlank() })
        // TODO read from preferences
        view.initSelectedClients(emptyList<Client>())
        view.initSubject("init subj")
        view.initBody("message body")
        view.start()
    }

    @Subscribe open fun onRequestSendBulkMailEvent(event: RequestSendMailEvent) {
        val recipients = view.readRecipients()
        if (recipients.isEmpty()) {
            dialogs.show(title = "Ungültige Eingabe", message = "Es muss zumindest ein Klient ausgewählt sein!", type = DialogType.WARN)
            return
        }
        val subject = view.readSubject()
        val body = view.readBody()
        if (subject.isEmpty() || body.isEmpty()) {
            dialogs.show(title = "Ungültige Eingabe", message = "Betreff und Mailinhalt darf nicht leer sein!", type = DialogType.WARN)
            return
        }
        // TODO verify mail address via regexp

        // wrap with progress dialog; close send mail window after success
        mailSender.send(Mail(recipients, subject, body))
    }

    @Subscribe open fun onMailWindowClosedEvent(event: MailWindowClosedEvent) {
        // TODO store in preferences
        println("store in preferences (subject and message body")
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        view.destroy()
    }

}


interface MailView : ClosableWindow {
    fun start()
    fun destroy()

    fun initClients(clients: List<Client>)
    fun initSelectedClients(selectedClients: List<Client>)
    fun initSubject(subject: String)
    fun initBody(body: String)

    fun readRecipients(): List<String>
    fun readSubject(): String
    fun readBody(): String
}

class MailSwingView @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus
) : MyFrame("Send Mail"), MailView {

    private val log = LOG(javaClass)

    private val clientsModel = MyListModel<Client>()

    // TODO make it clickable just as TCM lists
    private val inpClients = MyList<Client>("MailView.clients", clientsModel, bus, object : MyListCellRenderer<Client>() {
        override fun newCell(value: Client) = MailClientCell(value)
    })

    private val inpSubject = JTextField(60)
    private val inpBody = MyTextArea("MailView.body", 10)

    private var yetCreated: Boolean = false

    init {
        closeOnEscape()
        addCloseListener { doClose() }

        // see ClientList
        inpClients.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        contentPane.layout = BorderLayout()

        contentPane.add(inpClients.scrolled(), BorderLayout.WEST)

        contentPane.add(GridPanel().apply {
            c.weightx = 1.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(inpSubject)

            c.gridy++
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(inpBody.scrolled())

        }, BorderLayout.CENTER)

        contentPane.add(GridPanel().apply {
            add(JButton("Senden").apply {
                addActionListener { bus.post(RequestSendMailEvent()) }
            })
        }, BorderLayout.SOUTH)
    }

    override fun start() {
        // TODO copied from SwingPreferencesFrame => reuse instead
        if (yetCreated == false) {
            yetCreated = true
            pack()
            setLocationRelativeTo(mainFrame.asJFrame())
        }
        if (isVisible != true) {
            log.trace("Mail window visible.")
            isVisible = true
        } else {
            requestFocus()
        }
    }

    override fun initClients(clients: List<Client>) {
        clientsModel.resetData(clients)
    }

    override fun initSelectedClients(selectedClients: List<Client>) {
        inpClients.addSelectedValues(selectedClients)
    }

    override fun initSubject(subject: String) {
        inpSubject.text = subject
    }

    override fun initBody(body: String) {
        inpBody.text = body
    }

    override fun readRecipients() = inpClients.selectedValuesList.map { it.contact.mail }

    override fun readSubject() = inpSubject.text!!

    override fun readBody() = inpBody.text!!

    override fun closeWindow() {
        doClose()
    }

    override fun destroy() {
        isVisible = false
        dispose()
    }

    private fun doClose() {
        isVisible = false
        bus.post(MailWindowClosedEvent())
    }
}


private class MailClientCell(val client: Client) : DefaultCellView<Client>(client) {
    private val nameLbl = JLabel("${client.preferredName} ${client.lastName}").bold()
    private val mailAddress = JLabel(client.contact.mail)

    override val applicableForegrounds: Array<JComponent> = arrayOf(nameLbl, mailAddress)
    private val detailLabels = arrayOf(mailAddress)

    init {
        val detailFont = mailAddress.font.deriveFont(9.0F)
        detailLabels.forEach { it.font = detailFont }

        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.RIGHT
        c.gridheight = 3
        add(JLabel(client.picture.toViewLilRepresentation()))

        c.gridheight = 1
        c.insets = Pad.ZERO
        c.weightx = 1.0
        c.gridx++
        c.fill = GridBagConstraints.HORIZONTAL
        add(nameLbl)

        c.gridy++
        add(mailAddress)

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(JPanel().transparent())
    }

}
