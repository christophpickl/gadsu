package at.cpickl.gadsu.report

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.formatDate
import org.joda.time.DateTime
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.inject.Inject


fun MyImage.toReportRepresentation(): InputStream? {
    val bytes = toSaveRepresentation() ?: return null //throw GadsuException("Can not transform a non-saveable image to report representation (are you trying to use a default pic for reports?!)")
    return ByteArrayInputStream(bytes)
}


data class ProtocolReportData(
        val author: String,
        val printDate: DateTime,
        val client: ClientReportData,
        override val rows: List<TreatmentReportData>
) : ReportWithRows

data class ClientReportData(
        val fullName: String,
        val children: String?,
        val job: String?,
        val picture: InputStream?
)

class TreatmentReportData(
        val number: Int,
        val note: String?,
        date: DateTime
) {
    val dateFormatted: String
    init {
        dateFormatted = date.formatDate() // MINOR @REPORT - pass regular java Date and let jasper format date
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
            Pair("client_picture", report.client.picture),
            Pair("client_job", report.client.job?.nullIfEmpty()),
            Pair("client_children", report.client.children?.nullIfEmpty()),
            Pair("client_birthday", "birth as date"),
            Pair("client_relationship", "rel"),
            Pair("author", report.author),
            Pair("countTreatments", report.rows.size), // MINOR @REPORT - counting row items is most likely possible to do in jasper itself
            Pair("printDate", report.printDate.formatDate())
    )
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this

