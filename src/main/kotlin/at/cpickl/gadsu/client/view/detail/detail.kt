package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.image.ImagePickerFactory
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.changeSize
import at.cpickl.gadsu.view.components.newPersistableEventButton
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JTabbedPane

interface ClientDetailView {
    val imageViewNamePrefix: String get() = ViewNames.Client.ImagePrefix

    fun readClient(): Client
    fun writeClient(client: Client)
    fun isModified(): Boolean
    fun changeImage(newImage: MyImage)
    fun focusFirst()
    fun asComponent(): Component
}

class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        private val treatmentSubview: TreatmentsInClientView, // passed through to TabMain
        imagePickerFactory: ImagePickerFactory,
        prefs: Prefs
) : GridPanel(), ClientDetailView, ModificationAware {

    private val log = LoggerFactory.getLogger(javaClass)

    private var currentClient = Client.INSERT_PROTOTYPE // MINOR change to currentClient infra instead

    private val btnSave = swing.newPersistableEventButton(ViewNames.Client.SaveButton, { SaveClientEvent() })
    private val btnCancel = JButton("Abbrechen")

    // attention: must come AFTER list of buttons due to hacky design nature ;)

    private val modificationChecker = ModificationChecker(this, btnSave, btnCancel)
    private val tabMain = ClientTabMain(currentClient, modificationChecker, treatmentSubview,
            imagePickerFactory.create(imageViewNamePrefix, prefs.clientPictureDefaultFolder))
    private val tabDetail = ClientTabDetail()
    private val allTabs = arrayOf(tabMain, tabDetail)

    init {
        modificationChecker.disableAll()

        btnCancel.name = ViewNames.Client.CancelButton
        btnCancel.addActionListener {
            log.debug("btnCancel clicked")
            updateFields()
        }
        val newSize = Dimension(btnSave.preferredSize.width + 20, btnSave.preferredSize.height)
        btnSave.changeSize(newSize)
        btnCancel.changeSize(newSize)

        initLayout()
    }

    private fun initLayout() {
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(createTabbedPane())

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.CENTER
        c.weightx = 1.0
        c.weighty = 0.0
        add(createButtonPanel())
    }

    private fun createTabbedPane(): JTabbedPane {
        val tabbed = JTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT)
        tabbed.isOpaque = false
        tabbed.name = ViewNames.Client.TabbedPane
        allTabs.forEach {
            tabbed.addTab(it.title, it.asComponent())
        }
        return tabbed
    }

    private fun createButtonPanel(): JPanel {
        val buttonPanel = JPanel(BorderLayout())
        buttonPanel.debugColor = Color.BLUE
        buttonPanel.add(btnSave, BorderLayout.WEST)
        buttonPanel.add(btnCancel, BorderLayout.EAST)
        return buttonPanel
    }

    override fun readClient(): Client {
        return Client(
                currentClient.id,
                currentClient.created,
                tabMain.inpFirstName.text,
                tabMain.inpLastName.text,

                Contact(
                        mail = tabMain.inpMail.text,
                        phone = tabMain.inpPhone.text,
                        street = tabMain.inpStreet.text,
                        zipCode = tabMain.inpZipCode.text,
                        city = tabMain.inpCity.text
                ),
//              FIXME  tabMain.inpBirthday.selectedDate(),
                currentClient.birthday,
                tabMain.inpGender.selectedItemTyped,
                tabMain.inpCountryOfOrigin.text,
//                tabMain.inpRelationship.text,
                currentClient.relationship,
                tabMain.inpJob.text,
                tabMain.inpChildren.text,
                tabMain.inpNote.text,
                tabMain.clientPicture
        )
    }

    override fun writeClient(client: Client) {
        log.trace("set currentClient(client={})", client)

        tabMain.imageChanged = false
        currentClient = client
        btnSave.changeLabel(client)
        updateFields()
    }

    override fun focusFirst() {
        log.trace("focusFirst()")
        val requested = tabMain.inpFirstName.requestFocusInWindow()
        if (!requested) {
            log.warn("Requesting focus failed for: {}", tabMain.inpFirstName)
        }
    }

    override fun isModified(): Boolean {
        for (tab in allTabs) {
            if (tab.isModified(currentClient)) {
                return true
            }
        }
        return false
    }

    override fun changeImage(newImage: MyImage) {
        log.debug("changeImage(newImage)")
        tabMain.imageChanged = true

        tabMain.originalImage = newImage
        tabMain.imageContainer.icon = tabMain.originalImage.toViewBigRepresentation()

        modificationChecker.trigger()
    }

    private fun updateFields() {
        log.debug("updateFields(), originalClient={}", currentClient)
        allTabs.forEach {
            it.updateFields(currentClient)
        }
        modificationChecker.trigger()
    }

    override fun asComponent() = this

}
