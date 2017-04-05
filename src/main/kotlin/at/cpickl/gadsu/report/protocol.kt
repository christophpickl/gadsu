package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.report.multiprotocol.ReportMetaData
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import com.github.christophpickl.kpotpourri.common.string.nullIfEmpty
import com.google.common.annotations.VisibleForTesting
import org.joda.time.DateTime
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Date
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

class ClientReportData(
        val anonymizedName: String,
        val picture: InputStream?,
        val gender: Gender,

        since: DateTime?,
        birthday: DateTime?,
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

        val textMainObjective: String?,
        val textSymptoms: String?,
        val textFiveElements: String?,
        val textSyndrom: String?,

        val tcmProps: String?,
        val tcmNotes: String?
) {
    companion object {} // needed for extension

    val since: Date?
    val birthday: Date?

    init {
        this.since = since?.toDate()
        this.birthday = birthday?.toDate()
    }

}

/**
 * Used by Jasper to render fields in repeating detail band.
 */
@Suppress("unused")
class TreatmentReportData(
        val id: String, // only used for DB insert, not for actual report (UI)
        val number: Int,
        date: DateTime,
        duration: Int,
        val aboutDiscomfort: String?,
        val aboutDiagnosis: String?,
        val aboutContent: String?,
        val aboutFeedback: String?,
        val aboutHomework: String?,
        val aboutUpcoming: String?,
        val note: String?,
        val dynTreatments: String?,
        val treatedMeridians: String?
) {
    companion object {} // needed for extension

    val date: Date
    val time: String
    val duration: Int

    init {
        this.date = date.toDate()
        this.time = date.formatTimeWithoutSeconds()
        this.duration = fakeDurationForProtocolFeedback(duration)
    }

    private fun fakeDurationForProtocolFeedback(duration: Int): Int {
        when {
            duration >= 90 -> return 70
            duration >= 70 -> return 60
            else -> return duration
        }
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
            Pair("client_since", report.client.since),
            Pair("client_salutation", buildSalutation(report.client.gender)),

            Pair("client_birthday", report.client.birthday),
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

            Pair("client_mainObjective", report.client.textMainObjective?.nullIfEmpty()),
            Pair("client_symptoms", report.client.textSymptoms?.nullIfEmpty()),
            Pair("client_fiveElements", report.client.textFiveElements?.nullIfEmpty()),
            Pair("client_syndrom", report.client.textSyndrom?.nullIfEmpty()),

            //            Pair("author", report.author),
//            Pair("printDate", report.printDate.formatDate()),
            Pair("tcm_properties", report.client.tcmProps),
            Pair("tcm_notes", report.client.tcmNotes)
    )

    private fun buildSalutation(gender: Gender): String {
        val salut = if (gender == Gender.MALE) "Klient" else if (gender == Gender.FEMALE) "Klientin" else "KlientIn"
        return "$salut seit:"
    }

}

@VisibleForTesting
val Client.anonymizedName: String get() {
    if (lastName.isEmpty()) {
        return firstName
    }
    return firstName + " " + lastName.substring(0, 1) + "."
}


