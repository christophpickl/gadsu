package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.client.*
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.SuggesterController
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.changeSize
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import java.awt.*
import javax.swing.*

interface ClientDetailView {

    fun readClient(): Client
//    fun changeClient(client: Client)
    fun isModified(): Boolean
//    fun changeImage(newImage: MyImage)
    fun focusFirst()
    fun asComponent(): Component
    fun closePreparations()

    fun changeTab(tab: ClientTabType)
}

@Logged
open class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        bus: EventBus,
        private val currentClient: CurrentClient,
        appointmentsSubView: AppoinmentsInClientView, // passed through to TabMain
        treatmentSubview: TreatmentsInClientView, // passed through to TabMain
        suggester: SuggesterController // passed through to TabMain
//        imagePickerFactory: ImagePickerFactory,
//        prefs: Prefs
) : GridPanel(), ClientDetailView, ModificationAware {

    private val log = LOG(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Client.SaveButton, { SaveClientEvent() })

    private val btnCancel = JButton("Abbrechen")
    private val modificationChecker = ModificationChecker(this, btnSave, btnCancel)

    // attention: must come AFTER list of buttons due to hacky design nature ;)

    private val tabMain = ClientTabMain(currentClient.data, modificationChecker, appointmentsSubView, treatmentSubview, suggester)
//            imagePickerFactory.create(imageViewNamePrefix, prefs.clientPictureDefaultFolder)
    private val tabTexts = ClientTabTexts(modificationChecker)
    private val tabTcm = ClientTabTcm(currentClient.data, modificationChecker, bus)

    private val tabbedPane = JTabbedPane(JTabbedPane.NORTH, JTabbedPane.SCROLL_TAB_LAYOUT)
    private val allTabs = arrayOf(tabMain, tabTexts, tabTcm)
    init {
        // FIXME the focus manager should only be active when THIS view is visible
//        KeyboardFocusManager.getCurrentKeyboardFocusManager()
//                .addKeyEventDispatcher { event ->
//                    val handled: Boolean
////                    event.id == KeyEvent.KEY_RELEASED
//                    val commandDown = if (IS_OS_MAC) {
//                        event.isMetaDown
//                    } else {
//                        event.isControlDown
//                    }
//                    if (commandDown) {
//                        val newTab: JPanel? = when (event.keyCode) {
//                            KeyEvent.VK_1 -> tabMain
//                            KeyEvent.VK_2 -> tabTexts
//                            KeyEvent.VK_3 -> tabTcm
//                            else -> null
//                        }
//                        if (newTab != null && newTab != tabbedPane.selectedComponent) {
//                            handled = true
//                            log.trace("Changing selected tab to: {}", newTab)
//                            tabbedPane.selectedComponent = newTab
//                        } else {
//                            handled = false
//                        }
//                    } else {
//                        handled = false
//                    }
//                    handled
//                }

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

    override fun changeTab(tab: ClientTabType) {
        val newTab = when(tab) {
            ClientTabType.MAIN -> tabMain
            ClientTabType.TEXTS -> tabTexts
            ClientTabType.TCM -> tabTcm
        }
        if (newTab != tabbedPane.selectedComponent) {
            log.trace("Switching to tab: {}", tab)
            tabbedPane.selectedComponent = newTab
        }
    }

    override fun closePreparations() {
        tabMain.inpBirthday.hidePopup()
    }

    private fun initLayout() {
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(initTabbedPane())

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.CENTER
        c.weightx = 1.0
        c.weighty = 0.0
        add(createButtonPanel())
    }

    private fun initTabbedPane(): JTabbedPane {
        tabbedPane.isOpaque = false
        tabbedPane.name = ViewNames.Client.TabbedPane
        var i: Int = 0
        allTabs.forEach {
            val tabContent: Component = if (it.scrolled) JScrollPane(it.asComponent()).transparent() else it.asComponent()
            tabbedPane.addTab("<html><body><table width='100'><span style='align:center'>${it.title}</span></table></body></html>", tabContent)
            tabbedPane.setTabComponentAt(i++, JLabel(it.title, JLabel.CENTER).enforceWidth(100))
        }
        return tabbedPane
    }

    private fun createButtonPanel(): JPanel {
        val buttonPanel = JPanel(BorderLayout())
        buttonPanel.transparent()
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
                ClientState.ACTIVE, // FIXME state
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
                tabMain.inpOrigin.text,
                tabMain.inpRelationship.selectedItemTyped,
                tabMain.inpJob.text,
                tabMain.inpChildren.text,
                tabMain.inpHobbies.text,
                tabMain.inpNote.text,

                tabTexts.inpImpression.text,
                tabTexts.inpMedical.text,
                tabTexts.inpComplaints.text,
                tabTexts.inpPersonal.text,
                tabTexts.inpObjective.text,

                tabTcm.inpTcmNote.text,
                currentClient.data.picture, // FIXME this will use a maybe outdated reference, as list itself can update the picture!
                tabTcm.readProps()
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
