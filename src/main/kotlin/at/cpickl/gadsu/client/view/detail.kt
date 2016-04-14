package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.newEventButton
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

@Suppress("UNUSED") val ClientViewNames.SaveButton: String get() = "Client.SaveButton"
@Suppress("UNUSED") val ClientViewNames.CancelButton: String get() = "Client.CancelButton"
@Suppress("UNUSED") val ClientViewNames.InputFirstName: String get() = "Client.InputFirstName"
@Suppress("UNUSED") val ClientViewNames.InputLastName: String get() = "Client.InputLastName"

interface ClientDetailView {
    fun changeClient(client: Client)
    fun asComponent(): Component

    fun readClient(): Client

}
class SwingClientDetailView @Inject constructor(
        swing: SwingFactory
) : GridPanel(), ClientDetailView {
    companion object {
        private val BTN_SAVE_LABEL_INSERT = "Neu Anlegen"
        private val BTN_SAVE_LABEL_UPDATE = "Speichern"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    private val btnSave = swing.newEventButton(BTN_SAVE_LABEL_INSERT, ViewNames.Client.SaveButton, { SaveClientEvent() })

    private val inpFirstName = JTextField()
    private val inpLastName = JTextField()
    private var currentClient: Client = Client.INSERT_PROTOTYPE

    init {
        inpFirstName.name = ViewNames.Client.InputFirstName
        inpLastName.name = ViewNames.Client.InputLastName

        c.anchor = GridBagConstraints.FIRST_LINE_START
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(JLabel("Vorname"))
        c.gridx++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(inpFirstName)

        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        c.gridx = 0
        c.gridy++
        add(JLabel("Nachname"))
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.gridx++
        add(inpLastName)

        c.gridx = 0
        c.gridy++
        c.gridwidth = 2

        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT))

        val btnCancel = JButton("Abbrechen")
        btnCancel.name = ViewNames.Client.CancelButton
        btnCancel.addActionListener {
            changeClient(currentClient)
        }

        buttonPanel.add(btnSave)
        buttonPanel.add(btnCancel)
        add(buttonPanel)
    }

    override fun changeClient(client: Client) {
        log.trace("changeClient(client={})", client)
        btnSave.text = if (client.yetPersisted) BTN_SAVE_LABEL_UPDATE else BTN_SAVE_LABEL_INSERT
        currentClient = client
        updateFields()
    }

    override fun readClient(): Client {
        return Client(currentClient.id, inpFirstName.text, inpLastName.text, currentClient.created)
    }

    private fun updateFields() {
        inpFirstName.text = currentClient.firstName
        inpLastName.text = currentClient.lastName
    }

    override fun asComponent() = this

}
