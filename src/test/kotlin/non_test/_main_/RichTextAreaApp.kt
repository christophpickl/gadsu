package non_test._main_

import at.cpickl.gadsu.view.components.DEFAULT_FRAMED_DIMENSION
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.RichTextArea
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextField

object RichTextAreaApp {
    @JvmStatic
    fun main(args: Array<String>) {
        Framed.showWithContext({
            JPanel().apply {
                layout = BorderLayout()
                add(RichTextArea("", it.bus), BorderLayout.CENTER)
                add(JTextField("dummy txt field"), BorderLayout.SOUTH)
            }
        }, DEFAULT_FRAMED_DIMENSION)
    }
}
