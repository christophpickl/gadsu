package kotlin_playground

import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.RichTextArea
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel


fun main(args: Array<String>) {
    Framed.show(
            JPanel(BorderLayout()).apply {

                val textor = RichTextArea("viewName", EventBus())
                textor.readEnrichedText("<hl>one</hl> one-B <hl>two</hl> three <hl>four</hl>")

                add(textor, BorderLayout.CENTER)

//                val textArea = MyRichTextArea()
//                add(textArea.asJComponent(), BorderLayout.CENTER)
//
//                add(JButton("read from view").apply {
//                    addActionListener {
//                        println("textArea.htmlText: \n" + textArea.htmlText)
//                    }
//                }, BorderLayout.NORTH)
//
//                add(JButton("write to view").apply {
//                    addActionListener {
//                        textArea.htmlText = "fuchur <b>war</b> da"
//                    }
//                }, BorderLayout.SOUTH)
            },
            Dimension(800, 500))
}
