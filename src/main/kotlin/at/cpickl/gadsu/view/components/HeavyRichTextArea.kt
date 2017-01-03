package at.cpickl.gadsu.view.components

import org.openmechanics.htmleditor.HTMLEditor
import java.io.IOException
import java.io.StringReader
import javax.swing.JComponent
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.parser.ParserDelegator

// not yet used
open class HeavyRichTextArea {

    private val editor = HTMLEditor()

    var htmlText: String
        get() = editor.content
//            val writer = StringWriter()
//            editor.editorKit.write(writer, editor.document, 0, editor.document.length)
//            return writer.toString()

        set(value) {
//            editor.editorKit.read(StringReader(value), editor.document, 0)
//            editor.resetHtml(value)
            editor.editor.text = value
        }

    fun asJComponent(): JComponent = editor

    private fun HTMLEditor.resetHtml(htmlText: String) {
        var reader: StringReader? = null
        try {
//            println("Loading: $htmlText")
            reader = StringReader(htmlText)

            // Create empty HTMLDocument to read into
            val htmlKit = HTMLEditorKit()
            val htmlDoc = htmlKit.createDefaultDocument() as HTMLDocument
            // Create parser (javax.swing.text.html.parser.ParserDelegator)
            val parser = ParserDelegator()
            // Get parser callback from document
            val callback = htmlDoc.getReader(0)
            // Load it (true means to ignore character set)
            parser.parse(reader, callback, true)
            // Replace document
            editor.document = htmlDoc
//            println("Loaded")

        } catch (exception: IOException) {
            println("Load oops")
            exception.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (ignoredException: IOException) {
                }

            }
        }
    }
}
