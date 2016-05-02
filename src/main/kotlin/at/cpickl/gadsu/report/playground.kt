package at.cpickl.gadsu.report

import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream


fun main(args: Array<String>) {
//    createPdf("text.pdf")
//    manipulatePdf("cover.pdf", "cover2.pdf")
    addCover("text.pdf", "text_with_cover.pdf")
}

// http://developers.itextpdf.com/examples/itext-action-second-edition/chapter-1
private fun createPdf(target: String) {
    val document = Document()
    val targetFile = File(target)
    PdfWriter.getInstance(document, FileOutputStream(targetFile))
    document.open()
    document.add(Paragraph("My text!"))
    document.close()
    println("Saved pdf to: ${targetFile.absolutePath}")
}

// http://developers.itextpdf.com/examples/merging-pdf-documents
private fun addCover(src: String, dest: String) {
    val cover = PdfReader("cover.pdf")
    val reader = PdfReader(src)
    val document = Document()
    val copy = PdfCopy(document, FileOutputStream(dest))
    document.open()
    copy.addDocument(cover)
    copy.addDocument(reader)
    document.close()
    cover.close()
    reader.close()
}

private fun manipulatePdf(src: String, dest: String) {
    val reader = PdfReader(src)
    val stamper = PdfStamper(reader, FileOutputStream(dest))
//    val bf = findFontInForm(reader, new PdfName("Calibri"))
    val canvas = stamper.getOverContent(1)
    val phrase = Phrase("Some text in Calibri") // , Font(13)
    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, 36F, 806F, 0F)
    stamper.close()
}
