package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.report.ProtocolGenerator
import at.cpickl.gadsu.report.ProtocolReportData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.formatDateLong
import at.cpickl.gadsu.service.formatHourMinutesLong
import at.cpickl.gadsu.service.toDateTime
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import org.jfree.data.time.DateRange
import org.joda.time.DateTime
import org.joda.time.Duration
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MultiProtocolModule : AbstractModule() {
    override fun configure() {
        bind(MultiProtocolGenerator::class.java).to(MultiProtocolGeneratorImpl::class.java)
        bind(MultiProtocolRepository::class.java).to(MultiProtocolJdbcRepository::class.java)

        bind(MultiProtocolWindow::class.java).to(MultiProtocolSwingWindow::class.java)
    }
}

interface MultiProtocolGenerator {

    fun generatePdf(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>)

    fun generatePdfPersistAndDispatch(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>, description: String)

}

interface ReportMetaData {
    val printDate: DateTime
    val author: String
}

class MultiProtocolCoverData(
        override val printDate: DateTime,
        override val author: String,
        val statistics: MultiProtocolStatistics
) : ReportMetaData {
    companion object {
        // for extension methods only
    }
}

data class MultiProtocolStatistics(
        val numberOfClients: Int,
        val numberOfTreatments: Int,
        val treatmentDateRange: DateRange,
        val totalTreatmentTime: Duration // in minutes
) {
    companion object {
        // for extension methods only
    }
}

class MultiProtocolGeneratorImpl @Inject constructor(
        private val protocolGenerator: ProtocolGenerator,
        private val protocolRepository: MultiProtocolRepository,
        private val clock: Clock,
        private val bus: EventBus
        ) : MultiProtocolGenerator {

    override fun generatePdf(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>) {
        val protocols = mergeProtocols(protocolDatas)
        addCover(protocols, coverData, target)
    }

    override fun generatePdfPersistAndDispatch(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>, description: String) {
        generatePdf(target, coverData, protocolDatas)

        protocolRepository.insert(MultiProtocol(null, clock.now(), description, protocolDatas.flatMap { it.rows.map { it.id } }))
        bus.post(MultiProtocolGeneratedEvent())
    }

    private fun mergeProtocols(protocolDatas: List<ProtocolReportData>): PdfReader {
        val document = Document()
        val target = ByteArrayOutputStream()
        val copy = PdfCopy(document, target)
        document.open()

        protocolDatas.forEach {
            val reader = PdfReader(protocolGenerator.generateByteStream(it).toByteArray())
            copy.addDocument(reader)
        }
        document.close()
        return PdfReader(target.toByteArray())
    }

    // http://developers.itextpdf.com/examples/merging-pdf-documents
    private fun addCover(actualContent: PdfReader, coverData: MultiProtocolCoverData, target: File) {
        val coverRaw = PdfReader(javaClass.getResourceAsStream("/gadsu/reports/multiprotocol_cover.pdf"))
        val cover = manipulatePdf(coverData, coverRaw)

        val document = Document()
        val copy = PdfCopy(document, FileOutputStream(target))
        document.open()
        copy.addDocument(cover)
        copy.addDocument(actualContent)
        document.close()
        cover.close()
        actualContent.close()
    }

    private fun manipulatePdf(coverData: MultiProtocolCoverData, cover: PdfReader): PdfReader {
        val tempCoverPdf = File.createTempFile("gadsu_temp_cover", ".pdf")
        tempCoverPdf.deleteOnExit()
        val stamper = PdfStamper(cover, FileOutputStream(tempCoverPdf))
        val canvas = stamper.getOverContent(1)

        // increase to move more to RIGHT
        val middleX = 300F
        // increase to move more to TOP
        val boxAuthorY = 250F
        val boxDateY = boxAuthorY - 30F
        canvas.writeString(coverData.author, middleX, boxAuthorY, align = TextAlign.CENTER)
        canvas.writeString(coverData.printDate.formatDateLong(), middleX, boxDateY, align = TextAlign.CENTER)

        val column1X = 100F
        val row1Y = 140F
        val rowDiff = 15F
        val littleFont = Font(Font.FontFamily.UNDEFINED, 8.0F)
        canvas.writeString("Anzahl Klienten: ${coverData.statistics.numberOfClients}", column1X, row1Y, font = littleFont)
        canvas.writeString("Anzahl Behandlungen: ${coverData.statistics.numberOfTreatments}", column1X, row1Y - (rowDiff * 1), font = littleFont)
        canvas.writeString("Behandlungszeitraum: ${coverData.statistics.treatmentDateRange.lowerDate.toDateTime().formatDate()} - ${coverData.statistics.treatmentDateRange.upperDate.toDateTime().formatDate()}",
                column1X, row1Y - (rowDiff * 2), font = littleFont)
        canvas.writeString("Behandlungszeit Summe: ${coverData.statistics.totalTreatmentTime.formatHourMinutesLong()}", column1X, row1Y - (rowDiff * 3), font = littleFont)

        stamper.close()
        return PdfReader(tempCoverPdf.absolutePath)
    }

    private fun PdfContentByte.writeString(text: String, posX: Float, posY: Float, align: TextAlign = TextAlign.LEFT,
                                           font: Font? = null) {
        val phrase = if (font == null) Phrase(text) else Phrase(text, font)
        ColumnText.showTextAligned(this, align.itextConstant, phrase, posX, posY, 0F)
    }

}

private enum class TextAlign(val itextConstant: Int) {
    CENTER(Element.ALIGN_CENTER),
    LEFT(Element.ALIGN_LEFT)
}
