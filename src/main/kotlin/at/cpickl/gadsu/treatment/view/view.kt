package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.toMinutes
import at.cpickl.gadsu.treatment.NextTreatmentEvent
import at.cpickl.gadsu.treatment.PreviousTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.DynTreatmentTabbedPane
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.MainContent
import at.cpickl.gadsu.view.MainContentType
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.gadsuWidth
import at.cpickl.gadsu.view.components.inputs.MeridianSelector
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.VFillFormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.transparent
import at.cpickl.gadsu.view.swing.withFont
import com.google.common.collect.ComparisonChain
import com.google.common.eventbus.EventBus
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.plaf.basic.BasicTabbedPaneUI

interface TreatmentView : ModificationAware, MainContent {
    fun readTreatment(): Treatment
    fun wasSaved(newTreatment: Treatment)
    fun enablePrev(enable: Boolean)
    fun enableNext(enable: Boolean)
    fun addDynTreatment(dynTreatment: DynTreatment)
    fun removeDynTreatmentAt(tabIndex: Int)
    fun getDynTreatmentAt(tabIndex: Int): DynTreatment
    fun getAllDynTreatmentClasses(): List<Class<DynTreatment>>
}


class SwingTreatmentView @Inject constructor(
        swing: SwingFactory,
        menuBar: GadsuMenuBar, // this is kind a design hack, but it was quicker to do ;)
        @Assisted private val client: Client,
        @Assisted private var treatment: Treatment
) : GridPanel(
        viewName = ViewNames.Treatment.MainPanel,
        _debugColor = Color.YELLOW
), TreatmentView {

    companion object {
        val DYN_TAB_TITLE_ADD = "+"
    }

    override val type = MainContentType.TREATMENT
    private val log = LoggerFactory.getLogger(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Treatment.SaveButton, { TreatmentSaveEvent() }).gadsuWidth()
    private val btnBack = swing.newEventButton(Labels.Buttons.Back, ViewNames.Treatment.BackButton, { TreatmentBackEvent() }).gadsuWidth()

    private val modificationChecker = ModificationChecker(this, btnSave, menuBar.treatmentSave)

    private val fields = Fields<Treatment>(modificationChecker)
    private val inpDateAndTime = fields.newDateAndTimePicker("Datum", treatment.date, { it.date }, ViewNames.Treatment.InputDatePrefix, JTextField.RIGHT)

    private val inpDuration = fields.newMinutesField("Dauer", { it.duration.toMinutes() }, ViewNames.Treatment.InputDuration, 2)
    private val inpAboutDiscomfort = fields.newTextArea("Zustand", { it.aboutDiscomfort }, ViewNames.Treatment.InputAboutDiscomfort)

    private val inpAboutContent = fields.newTextArea("Inhalt (Begründung)", { it.aboutContent }, ViewNames.Treatment.InputAboutContent)
    private val inpAboutDiagnosis = fields.newTextArea("Diagnose", { it.aboutDiagnosis }, ViewNames.Treatment.InputAboutDiagnosis)
    private val inpAboutFeedback = fields.newTextArea("Feedback", { it.aboutFeedback }, ViewNames.Treatment.InputAboutFeedback)
    private val inpAboutHomework = fields.newTextArea("Homework", { it.aboutHomework }, ViewNames.Treatment.InputAboutHomework)
    private val inpAboutUpcoming = fields.newTextArea("Upcoming", { it.aboutUpcoming }, ViewNames.Treatment.InputAboutUpcoming)
    private val inpNote = fields.newTextArea("Sonstige Anmerkungen", { it.note }, ViewNames.Treatment.InputNote)

    private val meridianSelector = MeridianSelector()

    private val btnPrev = swing.newEventButton("<<", ViewNames.Treatment.ButtonPrevious, { PreviousTreatmentEvent() }).gadsuWidth()
    private val btnNext = swing.newEventButton(">>", ViewNames.Treatment.ButtonNext, { NextTreatmentEvent() }).gadsuWidth()

    private val subTreatmentView = DynTreatmentTabbedPane(treatment, swing.bus)
    private val bus: EventBus

    init {
        modificationChecker.enableChangeListener(subTreatmentView)
        modificationChecker.enableChangeListener(meridianSelector)

        bus = swing.bus
        if (treatment.yetPersisted) {
            modificationChecker.disableAll()
        }
        btnSave.changeLabel(treatment)
        fields.updateAll(treatment)
        meridianSelector.selectedMeridians = treatment.treatedMeridians

        initComponents()
        prepareSubTreatmentView()
    }

    override fun enablePrev(enable: Boolean) {
        btnPrev.isEnabled = enable
    }

    override fun enableNext(enable: Boolean) {
        btnNext.isEnabled = enable
    }

    override fun addDynTreatment(dynTreatment: DynTreatment) {
        subTreatmentView.addDynTreatment(dynTreatment)
    }

    override fun removeDynTreatmentAt(tabIndex: Int) {
        subTreatmentView.removeDynTreatmentAt(tabIndex)
    }

    override fun getDynTreatmentAt(tabIndex: Int) = subTreatmentView.getDynTreatmentAt(tabIndex)
    override fun getAllDynTreatmentClasses() = subTreatmentView.getAllDynTreatmentClasses()

    private fun initTopPanel() = GridPanel().apply {
        c.fill = GridBagConstraints.NONE
        c.weightx = 0.0
        c.gridheight = 2
        c.anchor = GridBagConstraints.NORTH
        c.insets = Insets(Pad.DEFAULT_SIZE, 0, 0, Pad.DEFAULT_SIZE) // top right
        add(initClientProfile())

        c.gridx++
        c.gridheight = 1
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.bottom(20)
        add(JLabel("Behandlung #${treatment.number} für ${client.preferredName}").withFont(Font.BOLD, 20))

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.0
        c.weighty = 0.0
        c.insets = Pad.ZERO
        add(initDetailPanel())

        c.gridy = 0
        c.gridx++
        c.anchor = GridBagConstraints.CENTER
        c.gridheight = 2
        c.weightx = 1.0
        c.weighty = 1.0
        add(JPanel(BorderLayout()).apply {
            add(meridianSelector.component, BorderLayout.EAST)
        })
    }

    private fun initComponents() {
        c.weightx = 0.0
        c.weighty = 0.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(initTopPanel())

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(initMainPanel())

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        add(initButtonPanel())
    }

    private fun initMainPanel(): Component {
        val panel = GridPanel()
        with(panel.c) {

            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
            panel.add(initTextAreas())

            gridx++
            weightx = 0.0
            insets = Pad.left(8)
            subTreatmentView.enforceWidth(400)
            panel.add(subTreatmentView)
        }
        return panel
    }

    private fun prepareSubTreatmentView() {
        subTreatmentView.addTab(DYN_TAB_TITLE_ADD, GridPanel().apply {
            c.anchor = GridBagConstraints.CENTER
            add(JLabel("Füge einen neuen Behandlungsteil hinzu."))
        })

        treatment.dynTreatments.forEach { addDynTreatment(it) }

        if (treatment.dynTreatments.isNotEmpty()) {
            subTreatmentView.selectedIndex = 1 // preselect first diagnosis tab
        }

//        val uiClass = subTreatmentView.ui.javaClass
        // AquaTabbedPaneContrastUI
        subTreatmentView.ui = object : BasicTabbedPaneUI() {
            override fun createMouseListener(): MouseListener {
                return object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        val index = subTreatmentView.ui.tabForCoordinate(subTreatmentView, e.x, e.y)
                        if (index == -1) {
                            return
                        }
                        val title = subTreatmentView.getTitleAt(index)

                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (title == DYN_TAB_TITLE_ADD) {
                                val tabBounds = subTreatmentView.getBoundsAt(index)
                                bus.post(DynTreatmentRequestAddEvent(PopupSpec(subTreatmentView, tabBounds.x, tabBounds.y + tabBounds.height)))
                            } else {
                                if (subTreatmentView.selectedIndex != index) {
                                    subTreatmentView.selectedIndex = index
                                } else if (subTreatmentView.isRequestFocusEnabled) {
                                    subTreatmentView.requestFocusInWindow()
                                }
                            }
                        } else if (SwingUtilities.isRightMouseButton(e)) {
                            if (title == DYN_TAB_TITLE_ADD) {
                                return
                            }
                            val popup = JPopupMenu()
                            val closeItem = JMenuItem("Löschen")
                            closeItem.addActionListener {
                                bus.post(DynTreatmentRequestDeleteEvent(index))
                            }
                            popup.add(closeItem)
                            val tabBounds = subTreatmentView.getBoundsAt(index)
                            popup.show(subTreatmentView, tabBounds.x, tabBounds.y + tabBounds.height)
                        }
                    }
                }
            }
        }
    }

    private fun initDetailPanel(): Component {
        val panel = GridPanel()
        with(panel.c) {
            fill = GridBagConstraints.HORIZONTAL
            panel.add(JLabel("Am "))
            gridx++
            panel.add(inpDateAndTime.toComponent())
            gridx++
            panel.add(JLabel(" und dauerte "))
            gridx++
            panel.add(inpDuration)
            gridx++
            panel.add(JLabel(" Minuten."))
            gridx++
            weightx = 1.0 // layout hack ;)
            panel.add(JPanel())
        }
        return panel
    }

    private fun initClientProfile(): Component {
        val panel = GridPanel()
        with(panel.c) {
            fill = GridBagConstraints.BOTH
            panel.add(JLabel(client.picture.toViewMedRepresentation()))
        }
        return panel
    }

    private fun initButtonPanel(): Component {
        val panel = JPanel(BorderLayout())
        panel.transparent()
        panel.debugColor = Color.ORANGE

        panel.add(JPanel().apply {
            transparent()
            add(btnSave)
            add(btnBack)

        }, BorderLayout.WEST)
        panel.add(JPanel().apply {
            transparent()
            add(btnPrev)
            add(btnNext)
        }, BorderLayout.EAST)

        return panel
    }

    private fun initTextAreas() = VFillFormPanel().apply {
        addFormInput(inpAboutDiscomfort)
        addFormInput(inpAboutContent)
        addFormInput(inpAboutDiagnosis)
        addFormInput(inpAboutFeedback)
        addFormInput(inpAboutHomework)
        addFormInput(inpAboutUpcoming)
        addFormInput(inpNote)
    }

    override fun isModified(): Boolean {
        if (fields.isAnyModified(treatment)) {
            return true
        }
        // additional checks not handled by fields instance
        return ComparisonChain.start()
                .compare(treatment.date, inpDateAndTime.selectedDate) // watch out for nulls!
                .result() != 0
                ||
                subTreatmentView.isModified() ||
                meridianSelector.isAnySelectedMeridianDifferentFrom(treatment.treatedMeridians)
    }

    override fun wasSaved(newTreatment: Treatment) {
        log.trace("wasSaved(newTreatment)")
        treatment = newTreatment

        meridianSelector.selectedMeridians = newTreatment.treatedMeridians
        fields.updateAll(newTreatment)
        subTreatmentView.wasSaved(newTreatment)
        btnSave.changeLabel(treatment)
        modificationChecker.trigger()
    }

    override fun closePreparations() {
        inpDateAndTime.hidePopup()
    }

    override fun asComponent() = this

    override fun readTreatment(): Treatment {
        log.trace("readTreatment()")
        // use full-init constructor (not copy method!) so to be aware of changes
        return Treatment(
                treatment.id,
                treatment.clientId,
                treatment.created,
                treatment.number,
                inpDateAndTime.selectedDate,
                minutes(inpDuration.numberValue),
                inpAboutDiscomfort.toEnrichedText(),
                inpAboutDiagnosis.toEnrichedText(),
                inpAboutContent.toEnrichedText(),
                inpAboutFeedback.toEnrichedText(),
                inpAboutHomework.toEnrichedText(),
                inpAboutUpcoming.toEnrichedText(),
                inpNote.toEnrichedText(),
                subTreatmentView.readDynTreatments(),
                meridianSelector.selectedMeridians
        )
    }

    override fun toString(): String {
        return "SwingTreatmentView(type=$type)"
    }

}

data class PopupSpec(val component: Component, val x: Int, val y: Int)
