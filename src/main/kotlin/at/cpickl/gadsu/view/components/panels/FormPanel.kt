package at.cpickl.gadsu.view.components.panels

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.increaseLeft
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JLabel


open class FormPanel(private val labelAnchor: Int = GridBagConstraints.NORTHEAST) : GridPanel() {
    private val insetsCol1 = Insets(5, 0, 0, 4) // add a bit to the top, and 4 on the right to create a h-gap
    private val insetsCol2 = Insets(0, 0, 0, 0)
    private val insetsCol2leftIncreased = insetsCol2.increaseLeft(5)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
    }

    open fun addFormInput(label: String, input: Component, fillType: GridBagFill = GridBagFill.Horizontal) {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        c.anchor = labelAnchor
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

    open fun addLastColumnsFilled(fillType: GridBagFill = GridBagFill.Both) {
        c.gridwidth = 2
        addLastRowFilled(fillType)
    }
}


/**
 * Like Form, but vertically aligned and vertical space is evenly distributed
 */
class VFillFormPanel : FormPanel() {
    init {
        c.anchor = GridBagConstraints.NORTHWEST
    }
    override fun addFormInput(label: String, input: Component, fillType: GridBagFill) {
        c.weightx = 0.0
        c.weighty = 0.0
        c.fill = GridBagConstraints.NONE
        c.insets = Pad.TOP
        add(JLabel(label).bold())

        c.gridy++
//        c.fill = fillType.swingId
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 0.3 // dont know exactly...
        c.insets = Pad.NONE
        add(input)

        c.gridy++
    }

    override fun addLastColumnsFilled(fillType: GridBagFill) {
        addLastRowFilled(fillType)
    }
}
