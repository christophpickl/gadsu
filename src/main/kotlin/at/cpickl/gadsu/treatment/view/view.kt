package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DateAndTimePicker
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.newDateAndTimePicker
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.newPersistableEventButton
import at.cpickl.gadsu.view.components.scrolled
import at.cpickl.gadsu.view.components.showFramed
import com.google.common.collect.ComparisonChain
import com.google.common.eventbus.EventBus
import com.google.inject.assistedinject.Assisted
import org.joda.time.DateTime
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea


fun main(args: Array<String>) {
    System.setProperty("gadsu.development", "true")
    val bus = EventBus()
    val clock = RealClock()

    val client = Client.INSERT_PROTOTYPE.copy(id = "myId")
    val treatment = Treatment.insertPrototype(client.id!!, 1, DateTime.now())

    showFramed(SwingTreatmentView(SwingFactory(bus, clock), client, treatment))
}

interface TreatmentView : ModificationAware {
    fun wasSaved(newTreatment: Treatment)
    fun asComponent(): Component
}


class SwingTreatmentView @Inject constructor(
        private val swing: SwingFactory,
        @Assisted private val client: Client,
        @Assisted private var treatment: Treatment
) : GridPanel(
        viewName = ViewNames.Treatment.MainPanel,
        _debugColor = Color.YELLOW
), TreatmentView {

    // FIXME calculate number in DB

    private val btnSave = swing.newPersistableEventButton(ViewNames.Treatment.SaveButton, {
        TreatmentSaveEvent(readTreatment(), client)
    })

    private val modificationChecker = ModificationChecker(this, btnSave)

    private val inpDate: DateAndTimePicker = swing.newDateAndTimePicker(modificationChecker, treatment.date)

    private val inpNote: JTextArea = swing.newTextArea(
            viewName = ViewNames.Treatment.InputNote,
            initialText = treatment.note,
            enableOn = modificationChecker)

    init {
        if (treatment.yetPersisted) {
            modificationChecker.disableAll()
        }
        btnSave.changeLabel(treatment)

        c.weighty = 0.0
        add(JLabel("Treatment for ${client.firstName}"))

        c.gridy++
        add(JLabel("Number: ${treatment.number}"))

        c.gridy++
        add(inpDate)

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(inpNote.scrolled())

        c.gridy++
        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.weighty = 0.0
        c.anchor = GridBagConstraints.WEST

        val buttonPanel = JPanel(BorderLayout())
        buttonPanel.debugColor = Color.ORANGE
        buttonPanel.add(swing.newEventButton(Labels.Buttons.Back, ViewNames.Treatment.BackButton, { TreatmentBackEvent() }), BorderLayout.WEST)
        buttonPanel.add(btnSave, BorderLayout.EAST)
        add(buttonPanel)
    }

    override fun isModified(): Boolean {
        println("t.date=${treatment.date}, inp.date=${inpDate.readDateTime()}")
        return ComparisonChain.start()
                .compare(treatment.date, inpDate.readDateTime())
                .compare(treatment.note, inpNote.text)
                .result() != 0
    }

    override fun wasSaved(newTreatment: Treatment) {
        treatment = newTreatment

        btnSave.changeLabel(treatment)
        modificationChecker.trigger()
    }

    override fun asComponent() = this

    private fun readTreatment(): Treatment {
        return Treatment(
                treatment.id,
                treatment.clientId,
                treatment.created,
                treatment.number,
                inpDate.readDateTime(),
                inpNote.text
        )
    }

}
