package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.SwingFactory
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
import javax.swing.JPanel
import javax.swing.JTextField


interface ClientDetailView {
    var currentClient: Client

    fun readClient(): Client
    fun isModified(): Boolean
    fun updateModifiedStateIndicator()
    fun asComponent(): Component

}
class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        treatmentTable: TreatmentsInClientView
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

        val formPanel = FormPanel()
        formPanel.addFormInput("Vorname", inpFirstName)
        formPanel.addFormInput("Nachname", inpLastName)

        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)
        buttonPanel.debugColor = Color.BLUE

        buttonPanel.add(btnSave)
        buttonPanel.add(btnCancel)

        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.weighty = 0.0
        add(formPanel)

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(treatmentTable)

        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.CENTER
        c.weighty = 0.0
        add(buttonPanel)
    }

    override fun updateModifiedStateIndicator() {
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
