package at.cpickl.gadsu.report

import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import java.io.FileOutputStream

fun main(args: Array<String>) {
    MultiProtocolGeneratorImpl().generate(ProtocolReportData.DUMMY)
}

interface MultiProtocolGenerator {

}

class MultiProtocolGeneratorImpl : MultiProtocolGenerator {
    val protocolGenerator = JasperProtocolGenerator(JasperEngineImpl())

    fun generate(protocolData: ProtocolReportData) {
        val protocol = PdfReader(protocolGenerator.generateByteStream(protocolData).toByteArray())
        addCover(protocol, "foobar.pdf")
    }

    // http://developers.itextpdf.com/examples/merging-pdf-documents
    private fun addCover(actualContent: PdfReader, dest: String) {
        val cover = PdfReader(javaClass.getResourceAsStream("/gadsu/reports/multiprotocol_cover.pdf"))

        val document = Document()
        val copy = PdfCopy(document, FileOutputStream(dest))
        document.open()
        copy.addDocument(cover)
        copy.addDocument(actualContent)
        document.close()
        cover.close()
        actualContent.close()
    }
}
