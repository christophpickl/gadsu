package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.CurrentClient
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.forClient
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.changeSize
import at.cpickl.gadsu.view.components.enforceWidth
import at.cpickl.gadsu.view.components.newPersistableEventButton
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane

interface ClientDetailView {

    fun readClient(): Client
//    fun changeClient(client: Client)
    fun isModified(): Boolean
//    fun changeImage(newImage: MyImage)
    fun focusFirst()
    fun asComponent(): Component
    fun closePreparations()
}

@Logged
open class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        private val bus: EventBus,
        private val currentClient: CurrentClient,
        private val treatmentSubview: TreatmentsInClientView // passed through to TabMain
//        imagePickerFactory: ImagePickerFactory,
//        prefs: Prefs
) : GridPanel(), ClientDetailView, ModificationAware {
    private val log = LOG(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Client.SaveButton, { SaveClientEvent() })

    private val btnCancel = JButton("Abbrechen")
    // attention: must come AFTER list of buttons due to hacky design nature ;)

    private val modificationChecker = ModificationChecker(this, btnSave, btnCancel)

    private val tabMain = ClientTabMain(currentClient.data, modificationChecker, treatmentSubview
//            imagePickerFactory.create(imageViewNamePrefix, prefs.clientPictureDefaultFolder)
    )
    private val tabTcm = ClientTabTcm()
    private val allTabs = arrayOf(tabMain, tabTcm)
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

    override fun closePreparations() {
        tabMain.inpBirthday.hidePopup()
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
        var i: Int = 0
        allTabs.forEach {
            tabbed.addTab("<html><body><table width='100'><span style='align:center'>${it.title}</span></table></body></html>", it.asComponent())
            tabbed.setTabComponentAt(i++, JLabel(it.title, JLabel.CENTER).enforceWidth(100))
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
        log.trace("readClient()");
        return Client(
                currentClient.data.id,
                currentClient.data.created,
                tabMain.inpFirstName.text,
                tabMain.inpLastName.text,

                Contact(
                        mail = tabMain.inpMail.text,
                        phone = tabMain.inpPhone.text,
                        street = tabMain.inpStreet.text,
                        zipCode = tabMain.inpZipCode.text,
                        city = tabMain.inpCity.text
                ),
                tabMain.inpBirthday.selectedDate,
                tabMain.inpGender.selectedItemTyped,
                tabMain.inpCountryOfOrigin.text,
                tabMain.inpRelationship.selectedItemTyped,
                tabMain.inpJob.text,
                tabMain.inpChildren.text,
                tabMain.inpNote.text,
                currentClient.data.picture // FIXME this will use a maybe outdated reference, as list itself can update the picture!
//                tabMain.clientPicture
        )
    }

    @Subscribe open fun onCurrentEvent(event: CurrentEvent) {
        event.forClient { updateFields() }
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
            if (tab.isModified(currentClient.data)) {
                return true
            }
        }
        return false
    }

//    override fun changeImage(newImage: MyImage) {
//        log.debug("changeImage(newImage)")
//        tabMain.imageChanged = true
//
//        tabMain.originalImage = newImage
//        tabMain.imageContainer.icon = tabMain.originalImage.toViewBigRepresentation()
//
//        modificationChecker.trigger()
//    }

    private fun updateFields() {
        log.debug("updateFields(), currentClient.data={}", currentClient.data)

        btnSave.changeLabel(currentClient.data)
        allTabs.forEach {
            it.updateFields(currentClient.data)
        }

        modificationChecker.trigger()
    }

    override fun asComponent() = this

}