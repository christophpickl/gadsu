package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.addChangeListener
import at.cpickl.gadsu.view.components.changeSize
import at.cpickl.gadsu.view.components.newEventButton
import com.google.common.collect.ComparisonChain
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

@Suppress("UNUSED") val ClientViewNames.SaveButton: String get() = "Client.SaveButton"
@Suppress("UNUSED") val ClientViewNames.CancelButton: String get() = "Client.CancelButton"
@Suppress("UNUSED") val ClientViewNames.InputFirstName: String get() = "Client.InputFirstName"
@Suppress("UNUSED") val ClientViewNames.InputLastName: String get() = "Client.InputLastName"

interface ClientDetailView {
    var currentClient: Client

    fun readClient(): Client
    fun isModified(): Boolean
    fun updateModifiedStateIndicator()
    fun asComponent(): Component

}
class SwingClientDetailView @Inject constructor(
        swing: SwingFactory
) : GridPanel(), ClientDetailView {
    companion object {
        private val BTN_SAVE_LABEL_INSERT = "Neu anlegen"
        private val BTN_SAVE_LABEL_UPDATE = "Speichern"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    private var _currentClient: Client = Client.INSERT_PROTOTYPE
    override var currentClient: Client
        get() = _currentClient
        set(value) {
            log.trace("set currentClient(value={})", value)
            btnSave.text = if (value.yetPersisted) BTN_SAVE_LABEL_UPDATE else BTN_SAVE_LABEL_INSERT
            _currentClient = value
            updateFields()
        }

    private val btnSave = swing.newEventButton(BTN_SAVE_LABEL_INSERT, ViewNames.Client.SaveButton, { SaveClientEvent() })
    private val btnCancel = JButton("Abbrechen")

    private val inpFirstName = JTextField()
    private val inpLastName = JTextField()

    /** Used to change enable/disable state on changes. */
    private val allButtons = arrayOf(btnSave, btnCancel)
    /** Used to detect any changes. */
    private val allInputs = arrayOf(inpFirstName, inpLastName)

    init {
        inpFirstName.name = ViewNames.Client.InputFirstName
        inpLastName.name = ViewNames.Client.InputLastName
        allInputs.forEach { it.addChangeListener { updateModifiedStateIndicator() } }
        btnCancel.name = ViewNames.Client.CancelButton
        btnCancel.addActionListener {
            currentClient = _currentClient
        }
        val newSize = Dimension(btnSave.preferredSize.width + 20, btnSave.preferredSize.height)
        btnSave.changeSize(newSize)
        btnCancel.changeSize(newSize)
        updateModifiedStateIndicator() // set buttons disabled at startup

        val formPanel = GridPanel()
        formPanel.c.anchor = GridBagConstraints.WEST
        formPanel.c.weightx = 0.0
        formPanel.c.fill = GridBagConstraints.NONE
        formPanel.add(JLabel("Vorname"))
        formPanel.c.gridx++
        formPanel.c.weightx = 1.0
        formPanel.c.fill = GridBagConstraints.HORIZONTAL
        formPanel.add(inpFirstName)

        formPanel.c.weightx = 0.0
        formPanel.c.fill = GridBagConstraints.NONE
        formPanel.c.gridx = 0
        formPanel.c.gridy++
        formPanel.add(JLabel("Nachname"))
        formPanel.c.weightx = 1.0
        formPanel.c.fill = GridBagConstraints.HORIZONTAL
        formPanel.c.gridx++
        formPanel.add(inpLastName)


        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)
        if (Development.ENABLED) buttonPanel.background = Color.BLUE
        buttonPanel.add(btnSave)
        buttonPanel.add(btnCancel)

        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.weighty = 0.0
        add(formPanel)
        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(JLabel("")) // hack the UI
        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.CENTER
        c.weighty = 0.0
        add(buttonPanel)
    }

    override fun updateModifiedStateIndicator() {
        log.trace("updateModifiedStateIndicator()")
        val modified = isModified()
        allButtons.forEach {
            it.isEnabled = modified
        }
    }

    override fun isModified(): Boolean {
        return ComparisonChain.start()
                .compare(currentClient.firstName, inpFirstName.text)
                .compare(currentClient.lastName, inpLastName.text)
                .result() != 0
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
