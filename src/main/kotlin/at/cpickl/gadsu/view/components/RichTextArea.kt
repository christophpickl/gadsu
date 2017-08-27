package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.acupuncture.Acupunct
import at.cpickl.gadsu.acupuncture.AcupunctCoordinate
import at.cpickl.gadsu.acupuncture.AcupunctWordDetector
import at.cpickl.gadsu.acupuncture.ShowAcupunctEvent
import at.cpickl.gadsu.isShortcutDown
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_LONG
import at.cpickl.gadsu.view.logic.beep
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import at.cpickl.gadsu.view.swing.focusTraversalWithTabs
import at.cpickl.gadsu.view.swing.onTriedToInsertTooManyChars
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.common.string.removePreAndSuffix
import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.EventBus
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.HashMap
import java.util.LinkedList
import javax.swing.JLabel
import javax.swing.JTextPane
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.JTextComponent
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext


data class ShortcutEvent(val format: RichFormat, val selectedText: String)
interface ShortcutListener {
    fun onShortcut(event: ShortcutEvent)
}

fun String.removeAllTags(): String {
    // this logic can be optimized a bit ;)
    var x = this
    RichFormat.values().forEach {
        x = x.replace(it.tag1, "").replace(it.tag2, "")
    }
    return x
}

enum class RichFormat(
        val label: String,
        val htmlTag: String,
        val shortcutKey: Char
) {

    Bold("bold", "b", 'b') {

        override fun addingAttribute(): AttributeSet {
            val sc = StyleContext.getDefaultStyleContext()
            var aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Colors.TEXTEDITOR_BOLD)
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

    val tag1 = "<$htmlTag>"
    val tag2 = "</$htmlTag>"

    companion object {
        val CLEAN_FORMAT: AttributeSet

        init {
            val sc = StyleContext.getDefaultStyleContext()
            var aset = sc.removeAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background)
            aset = sc.addAttribute(aset, StyleConstants.Bold, false)
            aset = sc.addAttribute(aset, StyleConstants.Italic, false)
            CLEAN_FORMAT = aset
        }
    }


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
        viewName: String,
        private val bus: EventBus,
        private val maxChars: Int = MAX_FIELDLENGTH_LONG
) : JTextPane() {

    companion object {
        private val FORMATS = RichFormat.values().associateBy { it.shortcutKey }

        private val FORMAT_ATTRIBUTES_ACUPUNCT: AttributeSet by lazy {
            val sc = StyleContext()//StyleContext.getDefaultStyleContext()
            var aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Colors.ACUPUNCT_LINK)
            aset = sc.addAttribute(aset, StyleConstants.Underline, true)
            aset
        }

        private fun isAcupunctFormat(aset: AttributeSet): Boolean {
            return aset.toMap()[StyleConstants.Foreground]?.equals(Colors.ACUPUNCT_LINK) ?: false // sufficient to check color ;)
        }

        @VisibleForTesting
        fun extractAcupuncts(text: String): List<Acupunct> {
            return text
                    .split("\n")
                    .flatMap { it.split(" ") }
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                    .map {
                        it.removePreAndSuffix(".")
                                .removePreAndSuffix(",")
                                .removePreAndSuffix("(")
                                .removePreAndSuffix(")")
                                .removePreAndSuffix("\n")
                    }
//                    .map { println("word: [$it]"); it }
                    .filter { AcupunctCoordinate.isPotentialLabel(it) }
                    .map { Acupunct.byLabel(it) }
                    .filterNotNull()
                    .distinct()
        }

    }

    private val log = LOG {}

    init {
        name = viewName
        focusTraversalWithTabs()

        enforceMaxCharacters(maxChars)
        enableAcupunctDetection() // enable for ALL by defaut ;)

        if (IS_OS_WIN) {
            font = JLabel().font
        }

        @Suppress("LeakingThis")
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {

                if (e.isShortcutDown && FORMATS.containsKey(e.keyChar)) {
                    onToggleFormat(FORMATS[e.keyChar]!!)
                }
            }
        })
    }

    private var isAcupunctDetectionEnabled = false

    private fun formatAcupunct(punct: Acupunct, endPosition: Int) {
        replaceTextStyle { adoc ->
            val label = punct.coordinate.label
            adoc.replace(endPosition - label.length, label.length, label, FORMAT_ATTRIBUTES_ACUPUNCT)
        }
    }

    private fun clearAcupunctFormat(offset: Int, textToClear: String) {
        replaceTextStyle { adoc ->
            adoc.replace(offset, textToClear.length, textToClear, RichFormat.CLEAN_FORMAT)
        }
    }

    private fun enableAcupunctDetection() {
        if (isAcupunctDetectionEnabled) {
            log.warn("acupunct detection already enabled!")
            return
        }
        isAcupunctDetectionEnabled = true
        WordDetector(this).addWordListener(AcupunctWordDetector().apply {
            addAcupunctListener { punct, position ->
                formatAcupunct(punct, position)
            }
        })
        // https://stackoverflow.com/questions/7740465/text-changed-event-in-jtextarea-how-to
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                checkAcupunct(e)
            }

            override fun removeUpdate(e: DocumentEvent) {
                checkAcupunct(e)
            }

            override fun changedUpdate(e: DocumentEvent) {
                checkAcupunct(e)
            }

            private fun checkAcupunct(e: DocumentEvent) {
                log.debug { "checkAcupunct(e.type=${e.type}, e.offset=${e.offset}, e.length=${e.length})" }
                val text = this@RichTextArea.text
                log.debug { "RichTextArea.text = [[[$text]]]" }

                if (e.type == DocumentEvent.EventType.INSERT && e.length == 1) {
//                    val prevWord = extractPreviousWord(text, this@RichTextArea.selectionEnd + 1) ?: return
//                    println("prevWord: $prevWord")
                    val word = extractWordAt(text, this@RichTextArea.selectionEnd)
                    println("word: $word")
                }
                // MINOR #113 be more precise when it comes to acupunct coordinages (there is no Lu99!)
//                val regexp = Pattern.compile("((Lu)|(Di)|(Ma)|(MP)|(He)|(Due)|(Bl)|(Ni)|(Pk)|(3E)|(Gb)|(Le))[1-9][0-9]?")
//                val matches = regexp.matcher(text)
//                while (matches.find()) {
//                    val found = matches.group(0)
//                    println("FOUND: $found at ")
//                    println("Start: ${matches.}, end: ${matches.regionEnd()}")
//                }
//                this@RichTextArea.text.split(" ").map {
//                    it
//                            .replace("(", "")
//                            .replace(")", "")
//                            .replace(".", "")
//                            .replace(",", "")
//                            .replace(";", "")
//                            .replace("-", "")
//                }.also { word -> println(word) }


                // TODO clearAcupunctFormat
//                var word: String? = null
//                var afterWordPos: Int? = null
//                if (e.type == DocumentEvent.EventType.INSERT) {
//                    log.debug { "modified text ===> [[[${e.extractText(this@RichTextArea)}]]]" }
//                    // jtext.selectionStart
//                    afterWordPos = e.offset + e.length
//                    word = extractPreviousWord(text = text, position = afterWordPos)
//                } else if (e.type == DocumentEvent.EventType.REMOVE) {
//
//                } else if (e.type == DocumentEvent.EventType.CHANGE) {
//
//                } else {
//                    log.warn { "Unhandled DocumentEvent.type: [${e.type}]" }
//                }
//
//                if (word == null || afterWordPos == null) return
//                if (!AcupunctCoordinate.isPotentialLabel(word)) return
//                val punct = Acupunct.byLabel(word) ?: return
//                formatAcupunct(punct, afterWordPos)
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.button == MouseEvent.BUTTON1 && e.clickCount == 1) {
                    val acupunct = extractAcupunctInSelection() ?: return
                    log.debug("Acupunct clicked in view: {}", acupunct)
                    bus.post(ShowAcupunctEvent(acupunct))
                }
            }
        })
    }

    private fun DocumentEvent.extractText(jtext: JTextComponent): String {
        if (type == DocumentEvent.EventType.REMOVE) {
            return ""
        }
        return jtext.text.substring(offset, offset + length)
    }

    /** In order to enable click action. */
    private fun extractAcupunctInSelection(): Acupunct? {
        val txt = text
        if (txt.isEmpty()) return null

        val start = selectionStart
        if (start == 0 || start == txt.length) return null

        var cursor = start
        while (cursor != 0 && isAcupunctFormatAt(cursor - 1)) {
            cursor--
        }
        val label = StringBuilder()
        while (isAcupunctFormatAt(cursor)) {
            label.append(txt[cursor])
            cursor++
            if (cursor == txt.length) {
                break
            }
        }
        if (label.isEmpty()) return null
        val punct = Acupunct.byLabel(label.toString())
        if (punct == null) {
            log.warn("Formatted as an acupunct label, but ain't one: [$label]!")
            beep()
        }
        return punct
    }

    private fun applyAcupunctStyles() {
        if (!isAcupunctDetectionEnabled) {
            return
        }
        val puncts = extractAcupuncts(text)

        puncts.forEach { punct ->
            var position = text.indexOf(punct.coordinate.label)
            if (position == -1) {
                throw GadsuException("Expected acupunct to be existing in text! punct=$punct")
            }
            var positionAfterLabel = position + punct.coordinate.label.length
            formatAcupunct(punct, positionAfterLabel)

            position = text.indexOf(punct.coordinate.label, positionAfterLabel)
            while (position != -1) {
                positionAfterLabel = position + punct.coordinate.label.length
                formatAcupunct(punct, positionAfterLabel)
                position = text.indexOf(punct.coordinate.label, positionAfterLabel)
            }
        }
    }


    @VisibleForTesting
    fun isAcupunctFormatAt(index: Int) = isAcupunctFormat(styledDocument.getCharacterElement(index).attributes)

    private fun RichFormat.clearTag(input: String) = input.replace(tag1, "").replace(tag2, "")

    fun readEnrichedText(enrichedText: String) {
        log.trace("readEnrichedText(enrichedText=[{}]) viewName={}", enrichedText, name)

        // dont forget to reset style before reading new!
        replaceTextStyle { adoc ->
            val txt = text
            adoc.replace(0, txt.length, txt, RichFormat.CLEAN_FORMAT)
        }

        var cleanText = enrichedText
        RichFormat.values().forEach {
            cleanText = it.clearTag(cleanText)
        }
        text = cleanText

        var txt = enrichedText
        RichFormat.values().forEach {
            val tag1 = it.tag1
            val tag2 = it.tag2
            while (txt.contains(tag1)) {
                var pivotableTxt = txt
                RichFormat.values().forEach { j ->
                    pivotableTxt = if (j == it) pivotableTxt else j.clearTag(pivotableTxt)
                }

                val start = pivotableTxt.indexOf(tag1)
                val end = pivotableTxt.indexOf(tag2) - tag1.length
//                println("going to select: $start/$end")
                select(start, end)

                replaceTextStyle { adoc ->
                    adoc.replace(start, end - start, selectedText, it.addingAttribute())
                }

                txt = txt.replaceFirst(tag1, "").replaceFirst(tag2, "")
            }
        }
        applyAcupunctStyles()

        caretPosition = text.length
    }

    fun toEnrichedText(): String {
        val result = StringBuilder()
        val n = text.length - 1
        for (i in 0..n) {
            val char = text[i]
            val charAttributes = styledDocument.getCharacterElement(i).attributes

            RichFormat.values().forEach {
                val isNowStyled = it.isStyle(charAttributes)
                val previousWasStyled = if (i == 0) false else {
                    val prevCharAttributes = styledDocument.getCharacterElement(i - 1).attributes
                    it.isStyle(prevCharAttributes)
                }
                if (!previousWasStyled && isNowStyled) {
                    result.append("<${it.htmlTag}>")
                }
            }

            result.append(char)

            RichFormat.values().forEach {
                val isNowStyled = it.isStyle(charAttributes)
                val nextIsStyled = if (i == (n)) false else {
                    val nextCharAttributes = styledDocument.getCharacterElement(i + 1).attributes
                    it.isStyle(nextCharAttributes)
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


    private val listeners = LinkedList<ShortcutListener>()
    fun registerListener(listener: ShortcutListener) {
        listeners.add(listener)
    }

    private fun onToggleFormat(format: RichFormat, enableSimulation: Boolean = true) {
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

        val previousSelection = Pair(selectionStart, selectionEnd)

        if (enableSimulation && simulationSaysLengthIsTooLong(format)) {
            onTriedToInsertTooManyChars()
            return
        }
        replaceTextStyle { adoc ->
            adoc.replace(selectionStart, selectedText.length, selectedText, aset)
        }

        select(previousSelection.first, previousSelection.second)

        listeners.forEach { it.onShortcut(ShortcutEvent(format, selectedText)) }
//        val e = AbstractDocument.DefaultDocumentEvent(offs, str.length, DocumentEvent.EventType.CHANGE)
    }

    private fun simulationSaysLengthIsTooLong(format: RichFormat): Boolean {
        val simulation = RichTextArea("simulation", EventBus(), maxChars)
        simulation.readEnrichedText(this.toEnrichedText())
        simulation.select(this.selectionStart, this.selectionEnd)
        simulation.onToggleFormat(format, enableSimulation = false)

        return simulation.toEnrichedText().length > maxChars
    }

    private fun replaceTextStyle(fn: (AbstractDocument) -> Unit) {
        val adoc = styledDocument as AbstractDocument
        _isReformatting = true
        fn(adoc)
        _isReformatting = false
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
