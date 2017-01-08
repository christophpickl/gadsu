package non_test._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.parseDateTime
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.view.SwingTreatmentView
import at.cpickl.gadsu.view.GadsuMenuBar
import at.cpickl.gadsu.view.MacHandler
import at.cpickl.gadsu.view.TestMacHandler
import at.cpickl.gadsu.view.components.Framed
import java.awt.Dimension


private fun dummyLines(lineCount: Int) = 1.rangeTo(lineCount).map { "$it - eine zeile" }.joinToString("\n")

fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    val client = Client.INSERT_PROTOTYPE.copy(id = "myId", firstName = "Anna", lastName = "Nym")
    val treatment = Treatment.insertPrototype(clientId = client.id!!, number = 1, date = "31.12.2016 15:30:00".parseDateTime(),
            duration = minutes(42),
            aboutHomework = dummyLines(20), aboutContent = dummyLines(20)
    )
    Framed.showWithContext({ context ->
        SwingTreatmentView(context.swing, GadsuMenuBar(context.bus, TestMacHandler), client, treatment)
    }, size = Dimension(1000, 600))
}
