package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.view.components.WordListener
import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.util.LinkedList

class AcupunctWordDetector : WordListener {

    companion object {
        init {
            // enforce eager loading ;)
            Acupunct.all()
        }
    }

    private val log = LOG {}
    private val listeners = LinkedList<(Acupunct, Int) -> Unit>()

    private fun String.clean() = trim('(', ')', '[', ']', '!', '?', '*', ',', '.', ';', '-')

    override fun onWord(word: String, endPosition: Int) {
        val cleanWord = word.clean()
        log.trace { "onWord(word: [$word], endPosition=$endPosition); cleaned: [$cleanWord]" }
        if (!AcupunctCoordinate.isPotentialLabel(cleanWord)) return

        val punct = Acupunct.byLabel(cleanWord) ?: return
        val charsAfterAcupunctLabel = if (word.endsWith(punct.titleShort)) 0 else {
            val punctIndex = word.indexOf(punct.titleShort)
            log.trace { "punctIndex: $punctIndex" }
            val wordFromPunctOn = word.substring(punctIndex)
            val cleanedWordFromPunctOn = cleanWord.substring(punctIndex)
            wordFromPunctOn.length - cleanedWordFromPunctOn.length
        }
        val correctedPosition = endPosition - charsAfterAcupunctLabel
        if (charsAfterAcupunctLabel != 0) {

            log.trace { "correctedPosition: $correctedPosition" }
        }

        listeners.forEach { it.invoke(punct, correctedPosition) }
    }

    fun addAcupunctListener(listener: (Acupunct, Int) -> Unit) {
        listeners.add(listener)
    }

}
