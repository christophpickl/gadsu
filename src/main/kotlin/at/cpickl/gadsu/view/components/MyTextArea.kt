package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import org.openmechanics.htmleditor.HTMLEditor
import java.awt.KeyboardFocusManager
import java.io.IOException
import java.io.StringReader
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.parser.ParserDelegator


open class MyTextArea constructor(
        viewName: String,
        visibleRows: Int? = null
) : JTextArea() {
    init {
        focusTraversalWithTabs()
        name = viewName
        if (visibleRows != null) {
            rows = visibleRows
        }

        lineWrap = true
        wrapStyleWord = true
        enforceMaxCharacters(MAX_FIELDLENGTH_LONG)

        if (IS_OS_WIN) {
            font = JLabel().font
        }

    }
}

fun <J : JComponent> J.focusTraversalWithTabs() = apply {
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null)
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null)
}

open class MyRichTextArea {

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
            println("Loading: $htmlText")
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
            println("Loaded")

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
    /*
      JPanel(BorderLayout()).apply {
                val editor = HTMLEditor().apply {
                    // text = editor.editor.getDocument().getText(0, editor.editor.getDocument().getLength() - 1);
//                    setStyleSheet(InputStreamReader(javaClass.getResourceAsStream("/htmleditor/css/default.css")), null)
                }

                add(editor, BorderLayout.CENTER)

                add(JButton("html text").apply {
                    addActionListener {
                        val writer = StringWriter()
                        editor.editorKit.write(writer, editor.document, 0, editor.document.length)
                        val x = writer.toString()
//                        val x = editor.editor.document.getText(0, editor.editor.document.length)
                        println("xx: [$x]")
                    }
                }, BorderLayout.SOUTH)
            },

     */
}
