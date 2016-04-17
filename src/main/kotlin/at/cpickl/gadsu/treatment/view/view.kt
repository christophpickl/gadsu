package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.newEventButton
import java.awt.Component
import javax.inject.Inject
import javax.swing.JLabel


interface TreatmentView {
    fun asComponent(): Component
}


class SwingTreatmentView @Inject constructor(
        private val swing: SwingFactory
) : GridPanel(), TreatmentView {

    init {
        name = ViewNames.Treatment.MainPanel

        add(JLabel("treatments"))
        c.gridy++
        add(swing.newEventButton("Zur\u00fcck", ViewNames.Treatment.BackButton, { TreatmentBackEvent() }))
    }

    override fun asComponent() = this

}
