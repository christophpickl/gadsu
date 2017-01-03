package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.view.components.WordListener
import java.util.LinkedList

class AcupunctWordDetector : WordListener {

    companion object {
        init {
            // enforce eager loading ;)
            Acupuncts.allPuncts
        }
    }
    private val listeners = LinkedList<AcupunctListener>()

    override fun onWord(word: String) {
        if (!AcupunctCoordinate.isPotentialLabel(word)) return

        val punct = Acupunct.byLabel(word) ?: return
        listeners.forEach { it.onAcupunct(punct) }
    }

    fun addAcupunctListener(listener: AcupunctListener) {
        listeners.add(listener)
    }

}

interface AcupunctListener {
    fun onAcupunct(punct: Acupunct)
}
