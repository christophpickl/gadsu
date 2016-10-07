package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.toDateTime
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Test
class ReportClientExtensionTest {

    @DataProvider
    fun clientNamesProvider() =
        arrayOf(
            arrayOf("Christoph", "Pickl", "Christoph P."),
            arrayOf("Christoph", "", "Christoph")
        )
    @Test(dataProvider = "clientNamesProvider")
    fun `Client anonymizedName`(firstName: String, lastName: String, expectedAnonymizedName: String) {
        assertThat(client(firstName, lastName).anonymizedName, equalTo(expectedAnonymizedName))
    }

    private fun client(firstName: String, lastName: String) =
            Client.unsavedValidInstance().copy(firstName = firstName, lastName = lastName)
}


@Test(groups = arrayOf("integration", "pdf"))
class ProtocolGeneratorTest {

    private val report = ProtocolReportData.testInstance()
    private lateinit var testee: ProtocolGenerator

    @BeforeMethod
    fun initTest() {
        testee = JasperProtocolGenerator(JasperEngineImpl())
    }

    fun `assert content`() {
        val bytes = testee.generateByteStream(report)
        val asserter = PdfAsserter.byStream(bytes)

//        println("PDF CONTENT:\n${asserter.content}\n")

        asserter.containsPdfStrings(
            "Protokoll",
            report.client.birthPlace!!,
            report.client.children!!
        )
        asserter.containsPdfStrings(
            report.rows.map {
                listOf(
                    "${it.number}", // will be on travis "Behandlun 1" instead "Behandlung 1"
                    "${it.date.toDateTime().formatDate()}" // will be on travis "Am 07.10.2016" instead "Am: 07.10.2016"
                    // "${it.note}" ... nah, got some nasty line breaks, and i dont wannaaaa! :-p
                )
            }.flatten()
        )
    }

//    private fun pdfAsserter() = PdfAsserter(target) // lazy init, as of existing target pdf file

}

class PdfAsserter(private val bytes: ByteArray) {
    companion object {
        fun byStream(stream: ByteArrayOutputStream) = PdfAsserter(stream.toByteArray())
        // byFile() = IOUtils.toByteArray(FileInputStream(pdfFile))
    }
    val content: String = extractPdfText(bytes)

    private fun extractPdfText(bytes: ByteArray): String {
        val pdfDocument = PDDocument.load(ByteArrayInputStream(bytes));
        try {
            return PDFTextStripper().getText(pdfDocument);
        } finally {
            pdfDocument.close()
        }
    }

    fun containsPdfStrings(substrings: List<String>) {
//        assertThat(content, allOf(substrings.map { Matchers.containsString(it) }))
        substrings.forEach { assertThat(content, Matchers.containsString(it)) }
    }
    fun containsPdfStrings(vararg substrings: String) {
         containsPdfStrings(substrings.toList())
    }

}
