package at.cpickl.gadsu.report

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.LogConfigurator
import at.cpickl.gadsu.service.formatDate
import org.joda.time.DateTime
import javax.inject.Inject


fun main(args: Array<String>) {
    LogConfigurator(debugEnabled = true).configureLog()
    val report = ProtocolReportData(
            printDate = DateTime.now(),
            client = ClientReportData("Max Mustermann"),
            rows = listOf(TreatmentReportData(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque elementum eros luctus, sagittis tellus vel, vestibulum sem. Morbi semper sit amet risus vel tristique. Vestibulum eleifend ante est, sed luctus massa lobortis in. Integer iaculis neque in eros tempor, vitae efficitur quam elementum. Curabitur laoreet leo sed dui commodo blandit. Suspendisse ut dolor sollicitudin mi venenatis vulputate quis quis ipsum. Morbi nec consectetur justo. Sed luctus leo non felis suscipit venenatis. Proin molestie orci blandit, dapibus risus ac, facilisis sem. Nullam hendrerit lacus ut mi lobortis, at malesuada quam facilisis. Morbi at elit eu ex pellentesque commodo non sed augue. Aenean ultrices dui lacus, eget vestibulum turpis vestibulum non. Suspendisse nec egestas felis. Aliquam tristique tincidunt mauris quis elementum. Suspendisse potenti. Sed vulputate volutpat dictum.", DateTime.now()),
                    TreatmentReportData(2, "something boring", DateTime.now().plusDays(1)),
                    TreatmentReportData(3, "a little bit better", DateTime.now().plusDays(4)),
                    TreatmentReportData(4, "very goooood", DateTime.now().plusDays(42)),
                    TreatmentReportData(5, "not good", DateTime.now().plusDays(43)),
                    TreatmentReportData(6, "final one", DateTime.now().plusDays(45))
            )
    )
    JasperProtocolGenerator(JasperEngineImpl())
            .view(report)
//            .savePdfTo(report, File("report.pdf"), true)
}

/**
 * User requested to generate a new protocol report.
 */
class CreateProtocolEvent(val client: Client) : UserEvent()

data class ProtocolReportData(
        val printDate: DateTime,
        val client: ClientReportData,
        override val rows: List<TreatmentReportData>
) : ReportWithRows

data class ClientReportData(
        val fullName: String
)
class TreatmentReportData(
        val number: Int,
        val note: String, // TODO resizable wrapped textarea in jasper!
        date: DateTime
) {
    val dateFormatted: String
    init {
        dateFormatted = date.formatDate() // MINOR pass regular java Date and let jasper format date
    }
}

interface ProtocolGenerator : GenericReportGenerator<ProtocolReportData> {
    // all via super-interface
}

class JasperProtocolGenerator @Inject constructor(
        engine: JasperEngine
) : BaseReportGenerator<ProtocolReportData>(TEMPLATE_CLASSPATH, engine), ProtocolGenerator {

    companion object {
        private val TEMPLATE_CLASSPATH = "/gadsu/reports/protocol.jrxml"
    }

    override fun buildParameters(report: ProtocolReportData) = arrayOf(
            Pair("client_fullName", report.client.fullName),
            Pair("author", "Christoph Pickl"), // FIXME use preferences to set author
            Pair("countTreatments", report.rows.size), // MINOR counting row items is most likely possible to do in jasper itself
            Pair("printDate", report.printDate.formatDate())
    )
}

