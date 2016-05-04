package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.GadsuSystemPropertyKeys
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.service.toMinutes
import at.cpickl.gadsu.spWriteTrue
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.MainContent
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
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
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField


fun main(args: Array<String>) {
    GadsuSystemPropertyKeys.development.spWriteTrue()

    val client = Client.INSERT_PROTOTYPE.copy(id = "myId", firstName = "Anna", lastName = "Nym")
    val treatment = Treatment.insertPrototype(clientId = client.id!!, number = 1, date = "31.12.2016 15:30:00".parseDateTime(),
            duration = minutes(42),
            aboutClient = "tat ihm weh", aboutTreatment = "zeile\nzeile\nzeile\nzeile\nzeile 111\nzeile\nzeile\nzeile\nzeile2222\nzeile\nzeile\nzeile 333")

    Framed.showWithContext({ context ->
        SwingTreatmentView(context.swing, client, treatment)
    }, size = Dimension(800, 600))
}


interface TreatmentView : ModificationAware, MainContent {
    fun wasSaved(newTreatment: Treatment)
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

    // FIXME use Fields and ElTextArea instead to be aware of changes and stuff
    private val inpDateAndTime = fields.newDateAndTimePicker("Datum", treatment.date, { it.date }, ViewNames.Treatment.InputDatePrefix, JTextField.RIGHT)
    private val inpDuration = fields.newMinutesField("Dauer", { it.duration.toMinutes() }, ViewNames.Treatment.InputDuration, 2)

    private val inpAboutClient = fields.newTextArea("Feedback Klient", { it.aboutClient }, ViewNames.Treatment.InputAboutClient)
    private val inpAboutTreatment = fields.newTextArea("Mein Feedback", { it.aboutTreatment }, ViewNames.Treatment.InputAboutTreatment)
    private val inpAboutHomework = fields.newTextArea("Hausaufgabe", { it.aboutHomework }, ViewNames.Treatment.InputAboutHomework)
    private val inpNote = fields.newTextArea("Notiz", { it.note }, ViewNames.Treatment.InputNote)

    init {
        if (treatment.yetPersisted) {
            modificationChecker.disableAll()
        }
        btnSave.changeLabel(treatment)
        fields.updateAll(treatment)

        initComponents()
    }

    private fun initComponents() {

        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.bottom(20)
        add(JLabel("Behandlung #${treatment.number}").withFont(Font.BOLD, 20))

        c.insets = Pad.ZERO
        c.gridx++
        c.fill = GridBagConstraints.NONE
        c.weightx = 0.0
        c.gridheight = 2
        c.anchor = GridBagConstraints.NORTH
        add(initClientProfile())

        c.gridx = 0
        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.0
        c.weighty = 0.0
        c.gridheight = 1
        add(initDetailPanel())

        c.gridy++
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
            panel.add(JLabel("Am "))
            gridx++
            panel.add(inpDateAndTime.toComponent())
            gridx++
            panel.add(JLabel(" und dauerte "))
            gridx++
            panel.add(inpDuration)
            gridx++
            panel.add(JLabel(" Minuten."))
        }
        return panel
    }

    private fun initClientProfile(): Component {
        val panel = GridPanel()
        with(panel.c) {
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
            panel.add(JLabel(client.picture.toViewMedRepresentation()))

            gridy++
            insets = Pad.TOP
            fill = GridBagConstraints.NONE
            anchor = GridBagConstraints.CENTER
            weightx = 0.0
            weighty = 0.0
            panel.add(JLabel(client.firstName))
        }
        return panel
    }

    private fun initButtonPanel(): Component {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        panel.transparent()
        panel.debugColor = Color.ORANGE

        panel.add(btnSave)
        panel.add(swing.newEventButton(Labels.Buttons.Back, ViewNames.Treatment.BackButton, { TreatmentBackEvent() }))
        return panel
    }

    private fun initTextAreas(): Component {
        val panel = VFillFormPanel()
        with(panel) {
            addFormInput(inpAboutClient)
            addFormInput(inpAboutTreatment)
            addFormInput(inpAboutHomework)
        }
        return panel
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
                inpAboutClient.text,
                inpAboutTreatment.text,
                inpAboutHomework.text,
                inpNote.text
        )
    }

}
