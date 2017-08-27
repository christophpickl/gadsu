package at.cpickl.gadsu.view.components

import java.awt.event.ActionEvent
import java.util.LinkedList
import javax.swing.AbstractAction
import javax.swing.KeyStroke
import javax.swing.text.JTextComponent

fun extractPreviousWord(text: String, endPosition: Int): String? {
    if (endPosition == 0) return null // inserted space at very beginning

    val textToPosition = text.substring(0, endPosition)
    val lastSpacePosition = textToPosition.lastIndexOf(' ')
    if (lastSpacePosition == - 1) {
        return textToPosition
    }

    val substring = text.substring(lastSpacePosition + 1, endPosition)
    return if (substring.isEmpty()) null else substring
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
