package non_test._main_

import at.cpickl.gadsu.view.components.DEFAULT_FRAMED_DIMENSION
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.RichTextArea

object RichTextAreaApp {
    @JvmStatic
    fun main(args: Array<String>) {
        Framed.showWithContext({
            RichTextArea("", it.bus)
        }, DEFAULT_FRAMED_DIMENSION)
    }
}
