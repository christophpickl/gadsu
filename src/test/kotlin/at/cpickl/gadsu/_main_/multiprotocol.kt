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
import at.cpickl.gadsu.tcm.model.XProps
import java.io.File


fun main(args: Array<String>) {
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
    MultiProtocolGeneratorImpl(JasperProtocolGenerator(JasperEngineImpl())).generate(File("foobar.pdf"), MultiProtocolCoverData.DUMMY, protocols)
    println("DONE")
}
