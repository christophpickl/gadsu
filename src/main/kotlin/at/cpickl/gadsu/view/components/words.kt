package at.cpickl.gadsu.view.components

import com.google.common.annotations.VisibleForTesting
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.LinkedList
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
        jtext.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                if (e.keyChar == ' ') {
                    onSpaceEntered()
                }
            }
        })
    }

    private fun onSpaceEntered() {
        val position = jtext.selectionStart // should no area selected anyway
        val word = extractPreviousWord(jtext.text, position) ?: return
        listeners.forEach { it.onWord(word) }
    }


    fun addWordListener(listener: WordListener) {
        listeners.add(listener)
    }

}

interface WordListener {
    fun onWord(word: String)
}
