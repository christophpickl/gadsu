package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.report.multiprotocol.ReportMetaData
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.nullIfEmpty
import com.google.common.annotations.VisibleForTesting
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
    companion object {} // needed for extension
}

data class ClientReportData(
        val anonymizedName: String,
        val picture: InputStream?,

        val since: DateTime,
        val birthday: DateTime?,
        val birthPlace: String?,
        val livePlace: String?,
        val relationship: String?,
        val children: String?,

        val job: String?,
        val hobbies: String?,

        val textsNotes: String?,
        val textsImpression: String?,
        val textsMedical: String?,
        val textsComplaints: String?,
        val textsPersonal: String?,
        val textsObjective: String?,

        val tcmProps: String?,
        val tcmNotes: String?
) {
    companion object {} // needed for extension
}

class TreatmentReportData(
        val id: String, // only used for DB insert, not for actual report (UI)
        val number: Int,
        val note: String?,
        date: DateTime
) {
    companion object {} // needed for extension

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
            Pair("countTreatments", report.rows.size), // MINOR @REPORT - counting row items is most likely possible to do in jasper itself
            Pair("client_picture", report.client.picture),
            Pair("client_name", report.client.anonymizedName),
            Pair("client_since", report.client.since.formatDate()),

            Pair("client_birthday", report.client.birthday?.formatDate()),
            Pair("client_birthplace", report.client.birthPlace?.nullIfEmpty()),
            Pair("client_liveplace", report.client.livePlace?.nullIfEmpty()),
            Pair("client_relationship", report.client.relationship?.nullIfEmpty()),
            Pair("client_children", report.client.children?.nullIfEmpty()),

            Pair("client_job", report.client.job?.nullIfEmpty()),
            Pair("client_hobbys", report.client.hobbies?.nullIfEmpty()),

            Pair("texts_notes", report.client.textsNotes?.nullIfEmpty()),
            Pair("texts_impression", report.client.textsImpression?.nullIfEmpty()),
            Pair("texts_medical", report.client.textsMedical?.nullIfEmpty()),
            Pair("texts_complaints", report.client.textsComplaints?.nullIfEmpty()),
            Pair("texts_personal", report.client.textsPersonal?.nullIfEmpty()),
            Pair("texts_objective", report.client.textsObjective?.nullIfEmpty()),

//            Pair("author", report.author),
//            Pair("printDate", report.printDate.formatDate()),
            Pair("tcm_properties", report.client.tcmProps),
            Pair("tcm_notes", report.client.tcmNotes)
    )

}

@VisibleForTesting
val Client.anonymizedName: String get() {
    if (lastName.isEmpty()) {
        return firstName
    }
    return firstName + " " + lastName.substring(0, 1) + "."
}


