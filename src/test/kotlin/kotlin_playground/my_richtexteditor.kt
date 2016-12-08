package kotlin_playground

import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.MyRichTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JPanel




fun main(args: Array<String>) {
    Framed.show(
            JPanel(BorderLayout()).apply {
                val textArea = MyRichTextArea()

                add(textArea.asJComponent(), BorderLayout.CENTER)

                add(JButton("read from view").apply {
                    addActionListener {
                        println("textArea.htmlText: \n" + textArea.htmlText)

                    }
                }, BorderLayout.NORTH)
                add(JButton("write to view").apply {
                    addActionListener {
                        textArea.htmlText = "fuchur <b>war</b> da"
                        val x = """
<html>
  <head>

  </head>
  <body>
    <p style="margin-top: 0">
fuchur <b>war</b> da
    </p>
  </body>
</html>
"""

                    }
                }, BorderLayout.SOUTH)
            },
            Dimension(800, 500))
}
