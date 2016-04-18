package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.changeSize
import at.cpickl.gadsu.view.components.newPersistableEventButton
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

    fun readClient(): Client
    fun writeClient(client: Client)
    fun isModified(): Boolean
//    fun updateModifiedStateIndicator()
    fun focusFirst()
    fun asComponent(): Component
}

class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        treatmentTable: TreatmentsInClientView
) : GridPanel(), ClientDetailView, ModificationAware {

    private var originalClient = Client.INSERT_PROTOTYPE

    private val log = LoggerFactory.getLogger(javaClass)

    // FIXME use it private var _currentClient: Client = Client.INSERT_PROTOTYPE

    private val btnSave = swing.newPersistableEventButton(ViewNames.Client.SaveButton, { SaveClientEvent() })
    private val btnCancel = JButton("Abbrechen")

    private val modificationChecker = ModificationChecker(this, btnSave, btnCancel)

    // attention: must come AFTER list of buttons due to hacky design nature ;)
    private val inpFirstName = modificationChecker.enableChangeListener(JTextField())
    private val inpLastName = modificationChecker.enableChangeListener(JTextField())


    init {
        modificationChecker.disableAll()

        inpFirstName.name = ViewNames.Client.InputFirstName
        inpLastName.name = ViewNames.Client.InputLastName

        btnCancel.name = ViewNames.Client.CancelButton
        btnCancel.addActionListener {
            updateFields()
        }
        val newSize = Dimension(btnSave.preferredSize.width + 20, btnSave.preferredSize.height)
        btnSave.changeSize(newSize)
        btnCancel.changeSize(newSize)

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

    override fun readClient(): Client {
        return Client(originalClient.id, originalClient.created, inpFirstName.text, inpLastName.text)
    }

    override fun writeClient(client: Client) {
        log.trace("set currentClient(client={})", client)

        originalClient = client
        btnSave.changeLabel(client)
        updateFields()
        modificationChecker.trigger()
    }

//    override fun updateModifiedStateIndicator() {
//        val modified = isModified()
//        allButtons.forEach {
//            it.isEnabled = modified
//        }
//    }

    override fun isModified(): Boolean {
        return ComparisonChain.start()
                .compare(originalClient.firstName, inpFirstName.text)
                .compare(originalClient.lastName, inpLastName.text)
                .result() != 0
    }

    override fun focusFirst() {
        log.trace("focusFirst()")
        val requested = inpFirstName.requestFocusInWindow()
        if (!requested) {
            log.warn("Requesting focus failed for: {}", inpFirstName)
        }
    }

    override fun asComponent() = this

    private fun updateFields() {
        inpFirstName.text = originalClient.firstName
        inpLastName.text = originalClient.lastName
    }

}
