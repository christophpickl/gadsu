package non_test._main_.view

import at.cpickl.gadsu.view.components.RichTextArea
import non_test.Framed
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JTextField

fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize {
        JPanel().apply {
            layout = BorderLayout()
            add(RichTextArea("", it.bus), BorderLayout.CENTER)
            add(JTextField("dummy txt field"), BorderLayout.SOUTH)
        }
    }
}
