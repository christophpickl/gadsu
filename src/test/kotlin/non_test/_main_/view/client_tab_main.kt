package non_test._main_.view

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.appointment.view.AppointmentList
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.detail.ClientTabMain
import at.cpickl.gadsu.treatment.inclient.TreatmentList
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.components.DEFAULT_FRAMED_DIMENSION
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.FramedContext
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import javax.swing.JComponent


fun main(args: Array<String>) {
    Framed.showWithContext(::clientTabMain, DEFAULT_FRAMED_DIMENSION)
}

fun clientTabMain(context: FramedContext): JComponent {
    return ClientTabMain(
            Client.INSERT_PROTOTYPE,
            ModificationChecker(object : ModificationAware {
                override fun isModified() = true
            }),
            AppoinmentsInClientView(context.swing, AppointmentList(context.bus)),
            TreatmentsInClientView(context.swing, TreatmentList(context.bus)),
            context.bus
    ).asComponent()
}
