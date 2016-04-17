package at.cpickl.gadsu.treatment.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentBackEvent
import at.cpickl.gadsu.treatment.TreatmentSaveEvent
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyDatePicker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.newDatePicker
import at.cpickl.gadsu.view.components.newEventButton
import com.google.inject.assistedinject.Assisted
import java.awt.Component
import javax.inject.Inject
import javax.swing.JLabel


interface TreatmentView {
    fun asComponent(): Component
}


class SwingTreatmentView @Inject constructor(
        private val swing: SwingFactory,
        @Assisted private val client: Client,
        @Assisted private val originalTreatment: Treatment
) : GridPanel(), TreatmentView {

    // FIXME calculate number in DB

    private val inpDate: MyDatePicker = swing.newDatePicker(originalTreatment.date)

    // FIXME is modified support
    init {
        name = ViewNames.Treatment.MainPanel

        add(JLabel("Treatment for ${client.firstName}"))

        c.gridy++
        add(JLabel("Number: ${originalTreatment.number}"))

        c.gridy++
        add(inpDate)

        c.gridy++
        add(swing.newEventButton("Speichern", ViewNames.Treatment.SaveButton, { TreatmentSaveEvent(readTreatment(), client) }))

        c.gridy++
        add(swing.newEventButton("Zur\u00fcck", ViewNames.Treatment.BackButton, { TreatmentBackEvent() }))
    }

    private fun readTreatment(): Treatment {
        return Treatment(
                originalTreatment.id,
                originalTreatment.clientId,
                originalTreatment.number,
                originalTreatment.created,
                inpDate.selectedDate()!!
        )
    }

    override fun asComponent() = this

}
