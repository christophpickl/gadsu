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

private enum class RichFormat(
        val label: String,
        val htmlTag: String,
        val shortcutKey: Char
) {
    Highlight("highlight", "hl", 'b') {

        override fun addingAttribute(): AttributeSet {
            val sc = StyleContext.getDefaultStyleContext()
            var aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.RED)
            aset = sc.addAttribute(aset, StyleConstants.Bold, true)
            return aset
        }

        override fun removalAttribute(): AttributeSet {
            val sc = StyleContext.getDefaultStyleContext()
            var aset = sc.removeAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background)
            aset = sc.addAttribute(aset, StyleConstants.Bold, false)
            return aset
        }

        override fun isStyle(attributes: AttributeSet): Boolean {
            val map = attributes.toMap()
            return map[StyleConstants.Bold]?.equals(true) ?: false
        }
    },

    Italic("italic", "i", 'i') {

        override fun addingAttribute(): AttributeSet {
            return StyleContext.getDefaultStyleContext()
                    .addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true)
        }

        override fun removalAttribute(): AttributeSet {
            return StyleContext.getDefaultStyleContext()
                    .addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, false)
        }

        override fun isStyle(attributes: AttributeSet): Boolean {
            val map = attributes.toMap()
            return map[StyleConstants.Italic]?.equals(true) ?: false
        }
    }
    ;


    abstract fun isStyle(attributes: AttributeSet): Boolean
    abstract fun removalAttribute(): AttributeSet
    abstract fun addingAttribute(): AttributeSet

}

fun AttributeSet.toMap(): Map<Any, Any> {
    val map = HashMap<Any, Any>()
    attributeNames.iterator().forEach {
        map.put(it, getAttribute(it))
    }
    return map
}

open class RichTextArea(
        viewName: String
) : JTextPane() {

    companion object {
        private val FORMATS = RichFormat.values().associateBy { it.shortcutKey }
    }

    private val log = LOG(javaClass)

    init {
        name = viewName
        focusTraversalWithTabs()

        // MINOR enforce max chars input for RichTextArea
        // needs a styled document
//        enforceMaxCharacters(MAX_FIELDLENGTH_LONG)

        if (IS_OS_WIN) {
            font = JLabel().font
        }

        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {

                if (e.isShortcutDown && FORMATS.containsKey(e.keyChar)) {
                    onToggleFormat(FORMATS[e.keyChar]!!)
                }
            }
        })
    }

    fun readEnrichedText(enrichedText: String) {
        log.trace("readEnrichedText(enrichedText=[{}])", enrichedText)

        var cleanText = enrichedText
        RichFormat.values().forEach {
            cleanText = cleanText.replace("<${it.htmlTag}>", "").replace("</${it.htmlTag}>", "")
        }
        text = cleanText


        RichFormat.values().forEach {
            var txt = enrichedText
            val tag = it.htmlTag
            while (txt.contains("<$tag>")) {
//            println("txt: [$txt]")
                val start = txt.indexOf("<$tag>")
                val end = txt.indexOf("</$tag>") - "<$tag>".length
//            println("going to select: $start/$end")
                select(start, end)
                onToggleFormat(it)
                txt = txt.replaceFirst("<$tag>", "").replaceFirst("</$tag>", "")
            }
        }

        select(text.length, text.length)
        moveCaretPosition(text.length)
    }

    fun toEnrichedText(): String {
        val result = StringBuilder()
        val n = text.length - 1
        for (i in 0..n) {
            val char = text[i]

            RichFormat.values().forEach {
                val isNowStyled = it.isStyle(styledDocument.getCharacterElement(i).attributes)
                val previousWasStyled = if (i == 0) false else {
                    it.isStyle(styledDocument.getCharacterElement(i - 1).attributes)
                }
                if (!previousWasStyled && isNowStyled) {
                    result.append("<${it.htmlTag}>")
                }
            }

            result.append(char)

            RichFormat.values().forEach {
                val isNowStyled = it.isStyle(styledDocument.getCharacterElement(i).attributes)
                val nextIsStyled = if (i == (n)) false else {
                    it.isStyle(styledDocument.getCharacterElement(i + 1).attributes)
                }
                if (isNowStyled && !nextIsStyled) {
                    result.append("</${it.htmlTag}>")
                }
            }
        }

        return result.toString()
    }


    private fun areAllCharsSameFormat(start: Int, end: Int, format: RichFormat): Boolean {
        for (i in start..end - 1) {
            val element = styledDocument.getCharacterElement(i)
//            element.attributes.dump()
            val isNotFormat = !format.isStyle(element.attributes)
            if (isNotFormat) {
                return false
            }
        }
        return true
    }

    private fun onToggleFormat(format: RichFormat) {
        if (selectedText == null || selectedText.isEmpty()) {
            log.trace("onToggleFormat() aborted because is empty")
            return
        }
        val allAreStyledByFormat = areAllCharsSameFormat(selectionStart, selectionEnd, format)
        log.trace("onToggleFormat() selectionStart=$selectionStart, selectionEnd=$selectionEnd; allAreStyledByFormat=$allAreStyledByFormat; selectedText=[$selectedText]")

        val aset: AttributeSet
        if (allAreStyledByFormat) {
            log.trace("remove format")
            aset = format.removalAttribute()
        } else {
            log.trace("add format")
            aset = format.addingAttribute()
        }

        val adoc = styledDocument as AbstractDocument
        val previousSelection = Pair(selectionStart, selectionEnd)

        // FIXME BUG: when select all, make it bold, then save => format is gone, but saved internally still
        _isReformatting = true
        adoc.replace(selectionStart, selectedText.length, selectedText, aset)
        _isReformatting = false

        // FIXME dispatch change event
//        val e = AbstractDocument.DefaultDocumentEvent(offs, str.length, DocumentEvent.EventType.CHANGE)

        select(previousSelection.first, previousSelection.second)
    }

    private var _isReformatting = false
    val isReformatting: Boolean get() = _isReformatting

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
