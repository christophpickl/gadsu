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
        override val author: String,
        override val printDate: DateTime,
        val client: ClientReportData,
        override val rows: List<TreatmentReportData>
) : ReportWithRows, ReportMetaData {
    companion object {
        val DUMMY = ProtocolReportData(
                author = "Med Wurst",
                printDate = DateTime.now(),
                client = ClientReportData(
                        fullName = "Klient Unbekannt",
                        children = "2 Kinder",
                        job = "Doktor",
                        picture = MyImage.DEFAULT_PROFILE_MAN.toReportRepresentation(),
                        cprops = null
                ),
                rows = TreatmentReportData.DUMMIES
//                treatments.map {
//                    TreatmentReportData(it.number, it.note, it.date)
//                }.sortedBy { it.number } // we need it ascending (but internally set descendant for list view)
        )
    }


}

data class ClientReportData(
        val fullName: String,
        val children: String?,
        val job: String?,
        val picture: InputStream?,
        val cprops: String?
)

class TreatmentReportData(
        val number: Int,
        val note: String?,
        date: DateTime
) {
    companion object {
        val DUMMIES = listOf(
                TreatmentReportData(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque elementum eros luctus, sagittis tellus vel, vestibulum sem. Morbi semper sit amet risus vel tristique. Vestibulum eleifend ante est, sed luctus massa lobortis in. Integer iaculis neque in eros tempor, vitae efficitur quam elementum. Curabitur laoreet leo sed dui commodo blandit. Suspendisse ut dolor sollicitudin mi venenatis vulputate quis quis ipsum. Morbi nec consectetur justo. Sed luctus leo non felis suscipit venenatis. Proin molestie orci blandit, dapibus risus ac, facilisis sem. Nullam hendrerit lacus ut mi lobortis, at malesuada quam facilisis. Morbi at elit eu ex pellentesque commodo non sed augue. Aenean ultrices dui lacus, eget vestibulum turpis vestibulum non. Suspendisse nec egestas felis. Aliquam tristique tincidunt mauris quis elementum. Suspendisse potenti. Sed vulputate volutpat dictum.", DateTime.now()),
                TreatmentReportData(2, "something boring", DateTime.now().plusDays(1)),
                TreatmentReportData(3, "a little bit better", DateTime.now().plusDays(4)),
                TreatmentReportData(4, "very goooood", DateTime.now().plusDays(42)),
                TreatmentReportData(5, "not good", DateTime.now().plusDays(43)),
                TreatmentReportData(6, "final one", DateTime.now().plusDays(45))
        )
    }

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
            Pair("printDate", report.printDate.formatDate()),
            Pair("cprops", report.client.cprops)
    )
}

fun String.nullIfEmpty() = if (this.isEmpty()) null else this

