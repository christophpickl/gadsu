package non_test._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.report.DUMMY
import at.cpickl.gadsu.report.JasperEngineImpl
import at.cpickl.gadsu.report.JasperProtocolGenerator
import at.cpickl.gadsu.report.ProtocolReportData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolCoverData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGeneratorImpl
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolSwingWindow
import at.cpickl.gadsu.report.testInstance
import at.cpickl.gadsu.service.DUMMY
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.Framed
import com.google.common.eventbus.EventBus
import org.mockito.Mockito
import java.io.File
import javax.swing.SwingUtilities


fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()
    LogConfigurator(debugEnabled = true).configureLog()

    generateAndViewMultiProtocol()
//    view()
//    generate()

//    Guice.createInjector(GadsuModule(Args.EMPTY)).getInstance(ReportController::class.java)
//            .onTestCreateMultiProtocolEvent(TestCreateMultiProtocolEvent())

}

private fun generateAndViewMultiProtocol() {
    val repo = Mockito.mock(MultiProtocolRepository::class.java)
    val generator = MultiProtocolGeneratorImpl(
            JasperProtocolGenerator(JasperEngineImpl()), repo, SimpleTestableClock(), EventBus(), MetaInf.DUMMY)
    val target = File.createTempFile("multi", ".pdf")
    target.deleteOnExit()
    generator.generatePdf(target, MultiProtocolCoverData.DUMMY, listOf(ProtocolReportData.testInstance()))

    println("VIEW start: ${target.absolutePath}")
    val processBuilder = ProcessBuilder("open", target.absolutePath)
    val process = processBuilder.start()
    val exitCode = process.waitFor()
    println("END (exit code: $exitCode)")
}

private fun generateAndViewProtocol() {
    JasperProtocolGenerator(JasperEngineImpl())
            .view(ProtocolReportData.testInstance())
//            .savePdfTo(report, File("report.pdf"), true)
}





private fun view() {
    SwingUtilities.invokeLater {
        Framed.initUi()
        MultiProtocolSwingWindow(Mockito.mock(MainFrame::class.java), SwingFactory(EventBus(), SimpleTestableClock()))
                .start(42)
    }
}

private fun generate() {
    //    val newPicture = "/gadsu/images/profile_pic-default_man.jpg".toMyImage().toReportRepresentation()
//    val dummyClient = ClientReportData.testInstance(anonymizedName = "Foo B.")
    val protocols = listOf(ProtocolReportData.testInstance(
            //            client = dummyClient.copy( ... no COPY() available anymore as of no data class anymore (as want to have constructor with non-vals)
//                    tcmProps = CPropsComposer.compose(Client.INSERT_PROTOTYPE.copy(
//                            gender = Gender.MALE,
//                            cprops = CProps.builder().add(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep, XProps.SleepOpts.TiredInMorning).build()
//                    ))
//                    // "Something fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!"
//                    //                          picture = newPicture NO! does not work! :(
//            )
    ))

    MultiProtocolGeneratorImpl(JasperProtocolGenerator(JasperEngineImpl()),
            Mockito.mock(MultiProtocolRepository::class.java), SimpleTestableClock(), EventBus(), MetaInf.DUMMY)

            .generatePdfPersistAndDispatch(File("foobar.pdf"), MultiProtocolCoverData.DUMMY, protocols, "")
    println("DONE")
}


