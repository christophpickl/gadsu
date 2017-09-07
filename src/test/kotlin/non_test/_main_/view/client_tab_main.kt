package non_test._main_.view

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.appointment.view.AppointmentList
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.detail.ClientTabMain
import at.cpickl.gadsu.treatment.inclient.TreatmentList
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker


fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize { context ->
        ClientTabMain(
                Client.INSERT_PROTOTYPE,
                ModificationChecker(object : ModificationAware {
                    override fun isModified() = true
                }),
                AppoinmentsInClientView(context.swing, AppointmentList(context.bus)),
                TreatmentsInClientView(context.swing, TreatmentList(context.bus)),
                context.bus
        ).asComponent()
    }
}
