package at.cpickl.gadsu._main_

import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.report.PrintReportController
import at.cpickl.gadsu.report.PrintReportSaveEvent
import at.cpickl.gadsu.report.PrintReportType
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.view.components.Dialogs
import org.mockito.Mockito

fun main(args: Array<String>) {
    val dialogs = Dialogs(null)
    val clock = Mockito.mock(Clock::class.java)
    val prefs = Mockito.mock(Prefs::class.java)
    PrintReportController(dialogs, clock, prefs).onPrintReportSaveEvent(PrintReportSaveEvent(PrintReportType.ANAMNESE))

}
