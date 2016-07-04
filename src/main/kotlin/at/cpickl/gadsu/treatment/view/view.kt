package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.service.toMinutes
import at.cpickl.gadsu.treatment.*
import at.cpickl.gadsu.view.*
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.VFillFormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.transparent
import at.cpickl.gadsu.view.swing.withFont
import com.google.common.collect.ComparisonChain
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory
import java.awt.*
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

private fun dummyLines(lineCount: Int) = 1.rangeTo(lineCount).map { "$it - eine zeile" }.joinToString("\n")

fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    val client = Client.INSERT_PROTOTYPE.copy(id = "myId", firstName = "Anna", lastName = "Nym")
    val treatment = Treatment.insertPrototype(clientId = client.id!!, number = 1, date = "31.12.2016 15:30:00".parseDateTime(),
            duration = minutes(42),
            aboutHomework = dummyLines(20), aboutContent = dummyLines(20)
    )
    Framed.showWithContext({ context ->
        SwingTreatmentView(context.swing, client, treatment)
    }, size = Dimension(800, 600))
}


interface TreatmentView : ModificationAware, MainContent {
    fun wasSaved(newTreatment: Treatment)
    fun enablePrev(enable: Boolean)
    fun enableNext(enable: Boolean)
}


class SwingTreatmentView @Inject constructor(
        private val swing: SwingFactory,
        @Assisted private val client: Client,
        @Assisted private var treatment: Treatment
) : GridPanel(
        viewName = ViewNames.Treatment.MainPanel,
        _debugColor = Color.YELLOW
), TreatmentView {
    private val log = LoggerFactory.getLogger(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Treatment.SaveButton, {
        TreatmentSaveEvent(readTreatment())
    })

    private val modificationChecker = ModificationChecker(this, btnSave)

    private val fields = Fields<Treatment>(modificationChecker)
    private val inpDateAndTime = fields.newDateAndTimePicker("Datum", treatment.date, { it.date }, ViewNames.Treatment.InputDatePrefix, JTextField.RIGHT)

    private val inpDuration = fields.newMinutesField("Dauer", { it.duration.toMinutes() }, ViewNames.Treatment.InputDuration, 2)
    private val inpAboutDiscomfort = fields.newTextArea("Beschwerden", { it.aboutDiscomfort }, ViewNames.Treatment.InputAboutDiscomfort)

    private val inpAboutDiagnosis = fields.newTextArea("Diagnose", { it.aboutDiagnosis }, ViewNames.Treatment.InputAboutDiagnosis)
    private val inpAboutContent = fields.newTextArea("Inhalt", { it.aboutContent }, ViewNames.Treatment.InputAboutContent)
    private val inpAboutFeedback = fields.newTextArea("Feedback", { it.aboutFeedback }, ViewNames.Treatment.InputAboutFeedback)
    private val inpAboutHomework = fields.newTextArea("Homework", { it.aboutHomework }, ViewNames.Treatment.InputAboutHomework)
    private val inpAboutUpcoming = fields.newTextArea("Upcoming", { it.aboutUpcoming }, ViewNames.Treatment.InputAboutUpcoming)
    private val inpNote = fields.newTextArea("Sonstige Anmerkungen", { it.note }, ViewNames.Treatment.InputNote)

    private val btnPrev = swing.newEventButton("Vorherige", ViewNames.Treatment.ButtonPrevious, { PreviousTreatmentEvent() })

    private val btnNext = swing.newEventButton("N\u00e4chste", ViewNames.Treatment.ButtonNext, { NextTreatmentEvent() })
    init {
        if (treatment.yetPersisted) {
            modificationChecker.disableAll()
        }
        btnSave.changeLabel(treatment)
        fields.updateAll(treatment)

        initComponents()
    }

    override fun enablePrev(enable: Boolean) {
        btnPrev.isEnabled = enable
    }
    override fun enableNext(enable: Boolean) {
        btnNext.isEnabled = enable
    }

    private fun initComponents() {
        c.fill = GridBagConstraints.NONE
        c.weightx = 0.0
        c.gridheight = 2
        c.anchor = GridBagConstraints.NORTH
        c.insets = Insets(Pad.DEFAULT_SIZE, 0, 0, Pad.DEFAULT_SIZE) // top right
        add(initClientProfile())

        c.gridx++
        c.gridheight = 1
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.bottom(20)
        add(JLabel("Behandlung #${treatment.number} für ${client.firstName}").withFont(Font.BOLD, 20))

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.0
        c.weighty = 0.0
        c.insets = Pad.ZERO
        add(initDetailPanel())

        c.gridx = 0
        c.gridy++
        c.gridwidth = 2
        c.fill = GridBagConstraints.BOTH
        c.gridwidth = 2
        c.weightx = 1.0
        c.weighty = 1.0
        add(initTextAreas())

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        add(initButtonPanel())
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
//            weightx = 1.0
//            weighty = 1.0
            fill = GridBagConstraints.BOTH
            panel.add(JLabel(client.picture.toViewMedRepresentation()))

//            gridy++
//            insets = Pad.TOP
//            fill = GridBagConstraints.NONE
//            anchor = GridBagConstraints.CENTER
//            weightx = 0.0
//            weighty = 0.0
//            panel.add(JLabel(client.firstName))
        }
        return panel
    }

    private fun initButtonPanel(): Component {
        val panel = JPanel(BorderLayout())
        panel.transparent()
        panel.debugColor = Color.ORANGE

        panel.add(JPanel().apply {
            transparent()
            add(btnPrev)
        }, BorderLayout.WEST)
        panel.add(JPanel().apply {
            transparent()
            add(btnSave)
            add(swing.newEventButton(Labels.Buttons.Back, ViewNames.Treatment.BackButton, { TreatmentBackEvent() }))
        }, BorderLayout.CENTER)
        panel.add(JPanel().apply {
            transparent()
            add(btnNext)
        }, BorderLayout.EAST)

        return panel
    }

    private fun initTextAreas() = VFillFormPanel().apply {
        addFormInput(inpAboutDiscomfort)
        addFormInput(inpAboutDiagnosis)
        addFormInput(inpAboutContent)
        addFormInput(inpAboutFeedback)
        addFormInput(inpAboutHomework)
        addFormInput(inpAboutUpcoming)
        addFormInput(inpNote)
    }

    override fun isModified(): Boolean {
        if (fields.isAnyModified(treatment)) {
            return true
        }
        return ComparisonChain.start()
                .compare(treatment.date, inpDateAndTime.selectedDate) // watch out for nulls!
                .compare(treatment.note, inpNote.text)
                .result() != 0
    }

    override fun wasSaved(newTreatment: Treatment) {
        log.trace("wasSaved(newTreatment)")
        treatment = newTreatment

        fields.updateAll(newTreatment)
        btnSave.changeLabel(treatment)
        modificationChecker.trigger()
    }

    override fun closePreparations() {
        inpDateAndTime.hidePopup()
    }

    override fun asComponent() = this

    private fun readTreatment(): Treatment {
        log.trace("readTreatment()")
        // use full-init constructor (not copy method!) so to be aware of changes
        return Treatment(
                treatment.id,
                treatment.clientId,
                treatment.created,
                treatment.number,
                inpDateAndTime.selectedDate,
                minutes(inpDuration.numberValue),
                inpAboutDiscomfort.text,
                inpAboutDiagnosis.text,
                inpAboutContent.text,
                inpAboutFeedback.text,
                inpAboutHomework.text,
                inpAboutUpcoming.text,
                inpNote.text
        )
    }

}
