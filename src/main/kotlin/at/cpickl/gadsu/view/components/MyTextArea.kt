package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.isShortcutDown
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import org.openmechanics.htmleditor.HTMLEditor
import java.awt.Color
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.io.IOException
import java.io.StringReader
import java.util.HashMap
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.Element
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
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

private enum class RichAction(
        val label: String
) {
    Highlight("highlight") {
        override fun foo() {

        }
    };

    abstract fun foo()

}

open class RichTextArea(viewName: String) : JTextPane() {

    private val log = LOG(javaClass)

    init {
        name = viewName

        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                if (e.keyChar == 'b' && e.isShortcutDown) {
                    onToggleBold()
                }
            }
        })
    }

    fun readEnrichedText(enrichedText: String) {
        log.trace("readEnrichedText(enrichedText=[{}])", enrichedText)
        text = enrichedText.replace("<hl>", "").replace("</hl>", "")

        var txt = enrichedText
        while (txt.contains("<hl>")) {
//            println("txt: [$txt]")
            val start = txt.indexOf("<hl>")
            val end = txt.indexOf("</hl>") - "<hl>".length
//            println("going to select: $start/$end")
            select(start, end)
            onToggleBold()
            txt = txt.replaceFirst("<hl>", "").replaceFirst("</hl>", "")
        }
        select(text.length, text.length)
        moveCaretPosition(text.length)
    }

    fun toEnrichedText(): String {
        val result = StringBuilder()
        val n = text.length-1
        for (i in 0..n) {
            val char = text[i]

            val previousWasBold = if (i == 0) false else {
                styledDocument.getCharacterElement(i - 1).isBold()
            }
            val nextIsBold = if (i == (n)) false else {
                styledDocument.getCharacterElement(i + 1).isBold()
            }
            val element = styledDocument.getCharacterElement(i)
            val isNowBold = element.isBold()

            if (!previousWasBold && isNowBold) {
                result.append("<hl>")
            }
            result.append(char)
            if (isNowBold && !nextIsBold) {
                result.append("</hl>")
            }
        }
        return result.toString()
    }

    private fun AttributeSet.toMap(): Map<Any, Any> {
        val map = HashMap<Any, Any>()
        attributeNames.iterator().forEach {
            map.put(it, getAttribute(it))
        }
        return map
    }

    private fun Element.isBold() = attributes.isBold()

    private fun AttributeSet.isBold(): Boolean {
        val map = toMap()
        return map[StyleConstants.Bold]?.equals(true) ?: false
    }

    private fun areAllCharsBold(start: Int, end: Int): Boolean {
        for (i in start..end - 1) {
            val element = styledDocument.getCharacterElement(i)
//            element.attributes.dump()
            val isNotBold = !element.attributes.isBold()
            if (isNotBold) {
                return false
            }
        }
        return true
    }

    private fun onToggleBold() {
        if (selectedText == null || selectedText.isEmpty()) {
            log.trace("onToggleBold() aborted because is empty")
            return
        }
        val allBold = areAllCharsBold(selectionStart, selectionEnd)
        log.trace("onToggleBold() selectionStart=$selectionStart, selectionEnd=$selectionEnd; allBold=$allBold; selectedText=[$selectedText]")

        val sc = StyleContext.getDefaultStyleContext()
        var aset: AttributeSet
        if (allBold) {
            log.trace("remove bold")
            aset = sc.removeAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background)
            aset = sc.addAttribute(aset, StyleConstants.Bold, false)
        } else {
            log.trace("add bold")
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.RED)
            aset = sc.addAttribute(aset, StyleConstants.Bold, true)
        }

        val adoc = styledDocument as AbstractDocument
        adoc.replace(selectionStart, selectedText.length, selectedText, aset)
    }

    private fun AttributeSet.dump() {
        println("attributes: $this")
        attributeNames.iterator().forEach {
            val name = it
            val value = getAttribute(name)
            println("    name = [$name]; value = [$value]")
        }
    }

}

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
