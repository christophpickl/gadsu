package at.cpickl.gadsu.view.components.panels

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.development.Development
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.increaseLeft
import at.cpickl.gadsu.view.swing.opaque
import at.cpickl.gadsu.view.swing.transparent
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JLabel


open class FormPanel : GridPanel() {
    private val insetsCol1 = Insets(5, 0, 0, 4) // add a bit to the top, and 4 on the right to create a h-gap
    private val insetsCol2 = Insets(0, 0, 0, 0)
    private val insetsCol2leftIncreased = insetsCol2.increaseLeft(5)

    init {
        if (Development.COLOR_ENABLED) {
            opaque()
        } else {
            transparent()
        }
        c.anchor = GridBagConstraints.NORTHWEST
    }

    fun addFormInput(label: String, input: Component, fillType: GridBagFill = GridBagFill.Horizontal) {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.NORTHEAST
        c.insets = insetsCol1
        add(JLabel(label).bold())

        c.gridx++
        c.weightx = 1.0
        c.fill = fillType.swingId
        c.anchor = GridBagConstraints.SOUTHWEST
        if (input is JLabel) {
            c.insets = insetsCol2leftIncreased
        } else {
            c.insets = insetsCol2
        }
        add(input)

        c.gridy++
        c.gridx = 0
    }

    fun addLastColumnsFilled(fillType: GridBagFill = GridBagFill.Both) {
        c.gridwidth = 2
        addLastRowFilled(fillType)
    }
}
