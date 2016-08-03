package at.cpickl.gadsu._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.report.CPropsComposer
import at.cpickl.gadsu.report.JasperEngineImpl
import at.cpickl.gadsu.report.JasperProtocolGenerator
import at.cpickl.gadsu.report.ProtocolReportData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolCoverData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolGeneratorImpl
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolRepository
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolSwingWindow
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.SimpleTestableClock
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.Framed
import com.google.common.eventbus.EventBus
import org.mockito.Mockito
import java.io.File
import javax.swing.SwingUtilities


fun main(args: Array<String>) {
    view()
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
    val dummyClient = ProtocolReportData.DUMMY.client
    val protocols = listOf(ProtocolReportData.DUMMY.copy(
            client = dummyClient.copy(
                    cprops = CPropsComposer.compose(Client.INSERT_PROTOTYPE.copy(
                            gender = Gender.MALE,
                            cprops = CProps.builder().add(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep, XProps.SleepOpts.TiredInMorning).build()
                    ))
                    // "Something fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!\nSomething fancy fuchuuuur!"
                    //                          picture = newPicture NO! does not work! :(
            )
    ))

    MultiProtocolGeneratorImpl(JasperProtocolGenerator(JasperEngineImpl()), Mockito.mock(MultiProtocolRepository::class.java), SimpleTestableClock(), EventBus())
            .generatePdfPersistAndDispatch(File("foobar.pdf"), MultiProtocolCoverData.DUMMY, protocols, "")
    println("DONE")
}


