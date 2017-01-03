package at.cpickl.gadsu.view.components

import com.google.common.annotations.VisibleForTesting
import java.awt.event.ActionEvent
import java.util.LinkedList
import javax.swing.AbstractAction
import javax.swing.KeyStroke
import javax.swing.text.JTextComponent


class WordDetector(val jtext: JTextComponent) {

    companion object {
        @VisibleForTesting fun extractPreviousWord(text: String, position: Int): String? {
            if (position == 0) return null // inserted space at very beginning

            val textToPosition = text.substring(0, position)
            val lastSpacePosition = textToPosition.lastIndexOf(' ')
            if (lastSpacePosition == - 1) {
                return textToPosition
            }

            val substring = text.substring(lastSpacePosition + 1, position)
            return if (substring.isEmpty()) null else substring
        }
    }

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
        val position = jtext.selectionStart - 1 // ignore selectionEnd as no area should be selected anyway
        val word = extractPreviousWord(jtext.text, position) ?: return
        listeners.forEach { it.onWord(word, position) }
    }


    fun addWordListener(listener: WordListener) {
        listeners.add(listener)
    }

}

interface WordListener {
    fun onWord(word: String, position: Int)
}
