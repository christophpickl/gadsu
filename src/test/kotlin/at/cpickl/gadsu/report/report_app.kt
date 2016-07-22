package at.cpickl.gadsu.report

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import org.joda.time.DateTime


fun main(args: Array<String>) {
    LogConfigurator(debugEnabled = true).configureLog()

    // dynamic text height
    // dont display whole section if empty (when enums are UNKNOWN, or text is empty)

    val report = ProtocolReportData(
            "Dr. Med Wurst",
            printDate = DateTime.now(),
            client = ClientReportData.testInstance(
                    job = "Maler",
                    // MyImage.DEFAULT_PROFILE_MAN // should NOT be used by report, as default pic not rendered
                    picture = MyImage.TEST_CLIENT_PIC1.toReportRepresentation()
            ),
            rows = listOf(
                    TreatmentReportData("", 1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque elementum eros luctus, sagittis tellus vel, vestibulum sem. Morbi semper sit amet risus vel tristique. Vestibulum eleifend ante est, sed luctus massa lobortis in. Integer iaculis neque in eros tempor, vitae efficitur quam elementum. Curabitur laoreet leo sed dui commodo blandit. Suspendisse ut dolor sollicitudin mi venenatis vulputate quis quis ipsum. Morbi nec consectetur justo. Sed luctus leo non felis suscipit venenatis. Proin molestie orci blandit, dapibus risus ac, facilisis sem. Nullam hendrerit lacus ut mi lobortis, at malesuada quam facilisis. Morbi at elit eu ex pellentesque commodo non sed augue. Aenean ultrices dui lacus, eget vestibulum turpis vestibulum non. Suspendisse nec egestas felis. Aliquam tristique tincidunt mauris quis elementum. Suspendisse potenti. Sed vulputate volutpat dictum.", DateTime.now()),
                    TreatmentReportData("", 2, null, DateTime.now().plusDays(1)),
                    TreatmentReportData("", 3, "a little bit better", DateTime.now().plusDays(4)),
                    TreatmentReportData("", 4, "very goooood.\nbut has been better.\nyet again third line.", DateTime.now().plusDays(42)),
                    TreatmentReportData("", 5, "not good", DateTime.now().plusDays(43)),
                    TreatmentReportData("", 6, "final one", DateTime.now().plusDays(45))
            )
    )
    JasperProtocolGenerator(JasperEngineImpl())
            .view(report)
    //            .savePdfTo(report, File("report.pdf"), true)
}
