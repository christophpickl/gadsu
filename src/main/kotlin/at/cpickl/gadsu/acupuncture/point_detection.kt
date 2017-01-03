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
    private val listeners = LinkedList<(Acupunct, Int) -> Unit>()

    override fun onWord(word: String, position: Int) {
        if (!AcupunctCoordinate.isPotentialLabel(word)) return

        val punct = Acupunct.byLabel(word) ?: return
        listeners.forEach { it.invoke(punct, position) }
    }

    fun addAcupunctListener(listener: (Acupunct, Int) -> Unit) {
        listeners.add(listener)
    }

}
