package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolCoverData
import at.cpickl.gadsu.report.multiprotocol.MultiProtocolStatistics
import at.cpickl.gadsu.service.minutes
import at.cpickl.gadsu.service.parseDate
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import org.jfree.data.time.DateRange
import org.joda.time.DateTime
import java.io.InputStream

fun ClientReportData.Companion.testInstance(
        anonymizedName: String,
        picture: InputStream? = null,

        since: DateTime = DateTime.now(),
        birthday: DateTime? = null,
        birthPlace: String? = null,
        livePlace: String? = null,
        relationship: String? = null,
        children: String? = null,
        job: String? = null,
        hobbies: String? = null,
        textsNotes: String? = null,
        textsImpression: String? = null,
        textsMedical: String? = null,
        textsComplaints: String? = null,
        textsPersonal: String? = null,
        textsObjective: String? = null,
        textsMainObjective: String? = null,
        textsSymptoms: String? = null,
        textsFiveElements: String? = null,
        textsSyndrom: String? = null,

        tcmProps: String? = null,
        tcmNotes: String? = null
) =
        ClientReportData(
                anonymizedName = anonymizedName,
                picture = picture,
                gender = Gender.UNKNOWN,

                since = since,
                birthday = birthday,
                birthPlace = birthPlace,
                livePlace = livePlace,
                relationship = relationship,
                children = children,
                job = job,
                hobbies = hobbies,
                textsNotes = textsNotes,
                textsImpression = textsImpression,
                textsMedical = textsMedical,
                textsComplaints = textsComplaints,
                textsPersonal = textsPersonal,
                textsObjective = textsObjective,
                textMainObjective = textsMainObjective,
                textSymptoms = textsSymptoms,
                textFiveElements = textsFiveElements,
                textSyndrom = textsSyndrom,
                tcmProps = tcmProps,
                tcmNotes = tcmNotes
        )

fun ProtocolReportData.Companion.testInstance(
        client: ClientReportData = ClientReportData.testInstance(
                anonymizedName = "Klient U.",
                picture = MyImage.TEST_CLIENT_PIC1.toReportRepresentation(),

                since = DateTime.now(),
                birthday = DateTime.now().minusYears(31),
                birthPlace = "Eisenstadt",
                livePlace = "Wien 1030",
                relationship = Relationship.SINGLE.label,
                children = "2 Kinder",
                job = "Flugbegleiterin, Studentin (Psychologie und Pharmazie)",
                hobbies = "radfahren, schwimmen, laufen, eislaufen, klettern, lesen (Romane), kochen",

                textsNotes = "erde schwach",
                textsMedical = "* Bein gebrochen links\n* Anus OP",
                textsMainObjective = "Kopfschmerzen",
                textsSymptoms = "Kopfschmerzen, Nackenverspannungen, viel schwitzen",
                textsFiveElements = "Yang, Holztyp",
                textsSyndrom = "Lu Yin Mangel",

                tcmProps = "Hunger: Gross\nSchlaf: Viel\nMenstruation: Stark\nFoo: bar\nAnother: Value\nAnd: More",
                tcmNotes = "* @ Zunge rot\n* Husten viel"
        )
) = ProtocolReportData(
        author = "Report Author",
        printDate = DateTime.now(),
        client = client,
        rows = TreatmentReportData.DUMMIES
)

fun TreatmentReportData.Companion.testInstance(number: Int = 0, date: String, duration: Int): TreatmentReportData {
    return TreatmentReportData("anyId", number, date.parseDate(), duration,
            null, null, null, null, null, null, null, null, null)
}

val TreatmentReportData.Companion.DUMMIES: List<TreatmentReportData> get() = listOf(
        TreatmentReportData(
                id = "",
                number = 1,
                date = DateTime.now(),
                duration = 90,
                aboutDiscomfort = "* fuehlt sich gut",
                aboutDiagnosis = "* wollte immer mithelfen",
                aboutContent = "* RL BP Haende\n* SP BP nacken",
                aboutFeedback = "war angenehme; gut",
                aboutHomework = "mehr sport machen",
                aboutUpcoming = "=> beim naechsten mal GB machen!!!!",
                note = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque elementum eros luctus, sagittis tellus vel, vestibulum sem. Morbi semper sit amet risus vel tristique. Vestibulum eleifend ante est, sed luctus massa lobortis in. Integer iaculis neque in eros tempor, vitae efficitur quam elementum. Curabitur laoreet leo sed dui commodo blandit. Suspendisse ut dolor sollicitudin mi venenatis vulputate quis quis ipsum. Morbi nec consectetur justo. Sed luctus leo non felis suscipit venenatis. Proin molestie orci blandit, dapibus risus ac, facilisis sem. Nullam hendrerit lacus ut mi lobortis, at malesuada quam facilisis. Morbi at elit eu ex pellentesque commodo non sed augue. Aenean ultrices dui lacus, eget vestibulum turpis vestibulum non. Suspendisse nec egestas felis. Aliquam tristique tincidunt mauris quis elementum. Suspendisse potenti. Sed vulputate volutpat dictum.",
                treatedMeridians = "Lu, Di, Ma",
                dynTreatments = "Hara: weich, rund, eckig\nPuls: foo, bar"
        ),
        newTreatmentReportData(number = 2, plusDays = 1),
        newTreatmentReportData(number = 3, plusDays = 4),
        newTreatmentReportData(number = 4, plusDays = 42),
        newTreatmentReportData(number = 5, plusDays = 43),
        newTreatmentReportData(number = 6, plusDays = 45)
)

private fun newTreatmentReportData(number: Int, plusDays: Int) =
        TreatmentReportData("", number, DateTime.now().plusDays(plusDays), 60, null, null, null, null, null, null, null, null, null)

val MultiProtocolStatistics.Companion.DUMMY: MultiProtocolStatistics get() =
    MultiProtocolStatistics(1, 6, DateRange("01.01.2012".parseDate().toDate(), "31.12.2012".parseDate().toDate()), minutes(102))

val MultiProtocolCoverData.Companion.DUMMY: MultiProtocolCoverData get() =
    MultiProtocolCoverData(DateTime.now(), "Christoph Author", MultiProtocolStatistics.DUMMY)
