package at.cpickl.gadsu._main_

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.appointment.view.AppointmentList
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.view.detail.ClientTabMain
import at.cpickl.gadsu.service.SuggesterControllerImpl
import at.cpickl.gadsu.service.SuggesterImpl
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.savedValidInstance2
import at.cpickl.gadsu.treatment.inclient.TreatmentList
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import org.mockito.Mockito
import java.awt.Dimension


fun main(args: Array<String>) {
    Framed.showWithContext({ context ->
        ClientTabMain(
                Client.INSERT_PROTOTYPE,
                ModificationChecker(object : ModificationAware {
                    override fun isModified() = true
                }),
                AppoinmentsInClientView(context.swing, AppointmentList(context.bus)),
                TreatmentsInClientView(context.swing, TreatmentList(context.bus)),
                SuggesterControllerImpl(SuggesterImpl(mockClientRepo()))
        )
    }, Dimension(800, 600))
}

//fun main(args: Array<String>) {
//    val txt = JTextField(50)
//    txt.name = "foobar"
//    SuggesterImpl(mockClientRepo()).enableSuggestionsFor(txt)
//    Framed.show(JPanel().apply {
//        add(txt)
//    })
//}

private fun mockClientRepo(): ClientRepository {
    val clientRepo = Mockito.mock(ClientRepository::class.java)
    Mockito.`when`(clientRepo.findAll()).thenReturn(listOf(Client.savedValidInstance(), Client.savedValidInstance2().copy(countryOfOrigin = "Hungary")))
    return clientRepo
}
