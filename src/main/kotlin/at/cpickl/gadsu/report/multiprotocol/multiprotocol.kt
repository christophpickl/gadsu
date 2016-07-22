package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.report.ProtocolGenerator
import at.cpickl.gadsu.report.ProtocolReportData
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.formatDateTimeLong
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import org.joda.time.DateTime
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MultiProtocolModule : AbstractModule() {
    override fun configure() {
        bind(MultiProtocolGenerator::class.java).to(MultiProtocolGeneratorImpl::class.java)
        bind(MultiProtocolRepository::class.java).to(MultiProtocolJdbcRepository::class.java)

    }
}

interface MultiProtocolGenerator {
    fun generate(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>)
}

interface ReportMetaData {
    val printDate: DateTime
    val author: String
}
data class MultiProtocolCoverData(
        override val printDate: DateTime,
        override val author: String
) : ReportMetaData {
    companion object {
        val DUMMY = MultiProtocolCoverData(DateTime.now(), "Christoph Author")
    }
}

class MultiProtocolGeneratorImpl @Inject constructor(
        private val protocolGenerator: ProtocolGenerator,
        private val protocolRepository: MultiProtocolRepository,
        private val clock: Clock,
        private val bus: EventBus
        ) : MultiProtocolGenerator {

    override fun generate(target: File, coverData: MultiProtocolCoverData, protocolDatas: List<ProtocolReportData>) {
        val protocols = mergeProtocols(protocolDatas)
        addCover(protocols, coverData, target)

        protocolRepository.insert(MultiProtocol(null, clock.now(), "dummy description", protocolDatas.flatMap { it.rows.map { it.id } }))
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
        val cover = manipulatePdf(coverData, coverRaw, "_tmp_cover.pdf")

        val document = Document()
        val copy = PdfCopy(document, FileOutputStream(target))
        document.open()
        copy.addDocument(cover)
        copy.addDocument(actualContent)
        document.close()
        cover.close()
        actualContent.close()
    }

    private fun manipulatePdf(coverData: MultiProtocolCoverData, cover: PdfReader, targetPath: String): PdfReader {
        val stamper = PdfStamper(cover, FileOutputStream(targetPath))
        val canvas = stamper.getOverContent(1)
        // increase to move more to RIGHT
        val middleX = 300F
        // increase to move more to TOP
        val boxAuthorY = 250F
        val boxDateY = boxAuthorY - 30F
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, Phrase(coverData.author), middleX, boxAuthorY, 0F)
        ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, Phrase(coverData.printDate.formatDateTimeLong()), middleX, boxDateY, 0F)
        stamper.close()

        return PdfReader(targetPath)
    }

}
