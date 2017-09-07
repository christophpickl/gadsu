package non_test._main_.view

import at.cpickl.gadsu.treatment.TreatmentGoalView
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.inputs.NumberField
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

fun main(args: Array<String>) {
    Framed.show(JPanel().apply {
        val view = TreatmentGoalView(10, 9)
        layout = BorderLayout()
        add(JPanel().apply {
            val txt = NumberField(3).apply { numberValue = 9 }
            add(JLabel("Current: "))
            add(txt)

            add(JButton("Update").apply { addActionListener { view.updateCount(txt.numberValue) } })
        }, BorderLayout.NORTH)

        add(view, BorderLayout.CENTER)
    }, Dimension(400, 150))
}
