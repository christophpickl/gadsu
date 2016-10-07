package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import org.joda.time.DateTime


fun main(args: Array<String>) {
    LogConfigurator(debugEnabled = true).configureLog()

    // dynamic text height
    // dont display whole section if empty (when enums are UNKNOWN, or text is empty)

    val report = ProtocolReportData(
            author = "Dr. Med Wurst",
            printDate = DateTime.now(),
            client = ClientReportData.testInstance(
                    anonymizedName = "Anna N.",
                    hobbies = "radfahren, schwimmen, laufen, eislaufen, klettern, lesen (Romane), kochen",
                    birthday = DateTime.now().minusYears(31),
                    birthPlace = "Wien",
                    relationship = Relationship.SINGLE.label,
                    job = "Flugbegleiterin, Studentin (Psychologie und Pharmazie)",
                    picture = MyImage.TEST_CLIENT_PIC1.toReportRepresentation(),
                    children = "keine",
                    // MyImage.DEFAULT_PROFILE_MAN // should NOT be used by report, as default pic not rendered
                    textsNotes = "erde schwach"
//                    textsMedical = "* Bein gebrochen links\n* Anus OP"
//                    tcmProps = "Hunger: Gross\nSchlaf: Viel\nMenstruation: Stark\nFoo: bar\nAnother: Value\nAnd: More",
//                    tcmNotes = "* @ Zunge rot\n* Husten viel"
            ),
            rows = TreatmentReportData.DUMMIES
    )
    JasperProtocolGenerator(JasperEngineImpl())
            .view(report)
    //            .savePdfTo(report, File("report.pdf"), true)
}
