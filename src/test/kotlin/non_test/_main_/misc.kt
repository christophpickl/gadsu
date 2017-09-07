package non_test._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.view.detail.ClientTabAssist
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.patho.SyndromeReport
import at.cpickl.gadsu.testinfra.fullInstance
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.testinfra.savedValidInstance2
import at.cpickl.gadsu.treatment.TreatmentGoalView
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.FramedContext
import at.cpickl.gadsu.view.components.inputs.MeridianSelector
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.inputs.TriCheckBox
import org.mockito.Mockito
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize(::clientTabAssist)
//        treatmentGoal()
//        triState()
//        meridianSelector()
}

fun clientTabAssist(context: FramedContext): JComponent {
    return ClientTabAssist(context.bus).apply { updateReport(Client.fullInstance(), SyndromeReport.dummy) }.asComponent()
}

fun meridianSelector() = MeridianSelector().apply {
    selectedMeridians = listOf(Meridian.Lung, Meridian.UrinaryBladder)
}.component

fun triState() = TriCheckBox<String>("", "some text", 0)

fun treatmentGoal() = JPanel().apply {
    layout = BorderLayout()
    val goal = 20
    val current = 15
    val view = TreatmentGoalView(goal, current)
    add(view, BorderLayout.CENTER)
    add(JPanel(FlowLayout()).apply {
        val txt = NumberField(5).apply { numberValue = current }
        add(txt)
        add(JButton("Change").apply {
            addActionListener {
                view.updateCount(txt.numberValue)
            }
        })
    }, BorderLayout.NORTH)
}

//fun main(args: Array<String>) {
//    val txt = JTextField(50)
//    txt.name = "foobar"
//    SuggesterImpl(mockClientRepo()).enableSuggestionsFor(txt)
//    Framed.show(JPanel().apply {
//        add(txt)
//    })
//}

/*

fun main(args: Array<String>) {
    val dialogs = Dialogs(null)
    val clock = Mockito.mock(Clock::class.java)
    val prefs = Mockito.mock(Prefs::class.java)
    PrintReportController(dialogs, clock, prefs).onPrintReportSaveEvent(PrintReportSaveEvent(PrintReportType.ANAMNESE))

}

 */
private fun mockClientRepo(): ClientRepository {
    val clientRepo = Mockito.mock(ClientRepository::class.java)
    Mockito.`when`(clientRepo.findAll()).thenReturn(listOf(Client.savedValidInstance(), Client.savedValidInstance2().copy(countryOfOrigin = "Hungary")))
    return clientRepo
}
