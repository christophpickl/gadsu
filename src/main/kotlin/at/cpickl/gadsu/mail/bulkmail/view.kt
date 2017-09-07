package at.cpickl.gadsu.mail.bulkmail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.components.DefaultCellView
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
import at.cpickl.gadsu.view.swing.emptyBorderForDialogs
import at.cpickl.gadsu.view.swing.registerCloseOnEscape
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel

interface BulkMailView : ClosableWindow {
    fun start()
    fun destroy()
    fun asJFrame(): JFrame

    fun initClients(clients: List<Client>)
    fun initSubject(subject: String)
    fun initBody(body: String)

    fun readRecipients(): List<Client>
    fun readSubject(): String
    fun readBody(): String
}

class BulkMailSwingView @Inject constructor(
        private val mainFrame: MainFrame,
        private val bus: EventBus
) : MyFrame("Mails versenden"), BulkMailView {

    private val log = LOG(javaClass)

    private val clientsModel = MyListModel<Client>()
    private val inpClients = MyList<Client>("MailView.clients", clientsModel, bus, object : MyListCellRenderer<Client>() {
        override fun newCell(value: Client) = MailClientCell(value)
    })

    private val inpSubject = JTextField(60)
    private val inpBody = MyTextArea("MailView.body", 10)

    private var yetCreated: Boolean = false

    init {
        registerCloseOnEscape()
        addCloseListener { doClose(true) }

        // see ClientList
        inpClients.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        inpClients.enableToggleSelectionMode()

        val rootPanel = GridPanel()
        rootPanel.emptyBorderForDialogs()

        rootPanel.c.weightx = 0.0
        rootPanel.c.weighty = 1.0
        rootPanel.c.fill = GridBagConstraints.VERTICAL
        rootPanel.c.insets = Pad.RIGHT
        rootPanel.add(inpClients.scrolled())

        rootPanel.c.gridx++
        rootPanel.c.weightx = 1.0
        rootPanel.c.weighty = 1.0
        rootPanel.c.fill = GridBagConstraints.BOTH
        rootPanel.c.insets = Pad.ZERO
        rootPanel.add(GridPanel().apply {
            c.weightx = 0.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.NONE
            add(JLabel("Betreff: "))

            c.gridx++
            c.weightx = 1.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(inpSubject)

            c.gridx = 0
            c.gridy++
            c.gridwidth = 2
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(inpBody.scrolled())
        })

        rootPanel.c.gridx = 0
        rootPanel.c.gridy++
        rootPanel.c.gridwidth = 2
        rootPanel.c.weightx = 1.0
        rootPanel.c.weighty = 0.0
        rootPanel.c.fill = GridBagConstraints.HORIZONTAL
        rootPanel.c.anchor = GridBagConstraints.CENTER
        rootPanel.add(GridPanel().apply {
            add(JButton("Senden").apply {
                addActionListener { bus.post(RequestSendBulkMailEvent()) }
            })
            c.gridx++
            add(JButton("Abbrechen").apply {
                addActionListener { doClose(false) }
            })

        })

        contentPane.layout = BorderLayout()
        contentPane.add(rootPanel, BorderLayout.CENTER)
    }

    override fun start() {
        // MINOR @UI - copied from SwingPreferencesFrame => reuse instead
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

    override fun asJFrame() = this

    override fun initClients(clients: List<Client>) {
        clientsModel.resetData(clients)
    }

    override fun initSubject(subject: String) {
        inpSubject.text = subject
    }

    override fun initBody(body: String) {
        inpBody.text = body
    }

    override fun readRecipients() = inpClients.selectedValuesList.map { it }

    override fun readSubject() = inpSubject.text!!

    override fun readBody() = inpBody.text!!

    override fun closeWindow() {
        doClose(true)
    }

    override fun destroy() {
        isVisible = false
        dispose()
    }

    private fun doClose(shouldPersistState: Boolean) {
        isVisible = false
        bus.post(BulkMailWindowClosedEvent(shouldPersistState))
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
