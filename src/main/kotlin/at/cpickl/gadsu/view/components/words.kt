package at.cpickl.gadsu.view.components

import java.awt.event.ActionEvent
import java.util.LinkedList
import javax.swing.AbstractAction
import javax.swing.KeyStroke
import javax.swing.text.JTextComponent

fun extractPreviousWord(text: String, exclusiveEndPosition: Int): String? {
    if (exclusiveEndPosition == 0) return null // inserted space at very beginning
    if (exclusiveEndPosition > text.length) return null

    val textToPosition = text.substring(0, exclusiveEndPosition)
    val lastSpacePosition = textToPosition.lastIndexOf(' ')
    if (lastSpacePosition == -1) {
        return textToPosition
    }

    val substring = text.substring(lastSpacePosition + 1, exclusiveEndPosition)
    return if (substring.isEmpty()) null else substring
}

fun extractWordAt(text: String, position: Int): String? {
    if (position < 0) return null
    if (text.isEmpty()) return null
    if (position > text.length) return null

    val firstPosSpace = text.substring(0, position).lastIndexOf(' ')
    val lastPosSpace = text.substring(position).indexOf(' ')
    val x = text.substring(
            if (firstPosSpace != -1) firstPosSpace + 1 else 0,
            if (lastPosSpace != -1) lastPosSpace + position else text.length
    )
    if (x.trim().isEmpty()) {
        return null
    }
    return x
}

class WordDetector(val jtext: JTextComponent) {


    private val listeners = LinkedList<WordListener>()

    init {
        // http://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
        jtext.inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "released")
        jtext.actionMap.put("released", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                onSpaceEntered()
            }
        })
    }

    private fun onSpaceEntered() {
        val spacePosition = jtext.selectionStart - 1 // ignore selectionEnd as no area should be selected anyway
        val word = extractPreviousWord(jtext.text, spacePosition) ?: return
        listeners.forEach { it.onWord(word, spacePosition) }
    }


    fun addWordListener(listener: WordListener) {
        listeners.add(listener)
    }

}

interface WordListener {
    fun onWord(word: String, endPosition: Int)
}
