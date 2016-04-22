package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.development.debugColor
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


object Pad {
    val ZERO   = Insets(0, 0, 0, 0)
    val TOP    = Insets(5, 0, 0, 0)
    val LEFT   = Insets(0, 5, 0, 0)
    val BOTTOM = Insets(0, 0, 5, 0)
    val RIGHT  = Insets(0, 0, 0, 5)
}


fun GridBagConstraints.fatComponent() {
    fill = GridBagConstraints.BOTH
    weightx = 1.0
    weighty = 1.0
}



open class GridPanel(viewName: String? = null, _debugColor: Color? = null) : JPanel() {

    val c = GridBagConstraints()

    init {
        if (viewName !== null) name = viewName
        if (_debugColor !== null) debugColor = _debugColor

        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        gridBagLayout.setConstraints(this, c)

        c.gridx = 0
        c.gridy = 0
    }

    override fun add(comp: Component): Component? {
        super.add(comp, c)
        return null
    }


    fun addColumned(vararg columns: Pair<Double, JComponent>) {
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        c.anchor = GridBagConstraints.NORTH
        var first = true
        columns.forEach {
            if (first) {
                first = false
            } else {
                c.insets = Pad.LEFT
            }

            c.weightx = it.first
            add(it.second)
            c.gridx++
        }
    }

}

open class FormPanel : GridPanel() {
    private val insetsCol1 = Insets(5, 0, 0, 4) // add a bit to the top, and 4 on the right to create a h-gap
    private val insetsCol2 = Insets(0, 0, 0, 0)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
    }

    fun addFormInput(label: String, input: Component) {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        c.insets = insetsCol1
        add(JLabel(label))

        c.gridx++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        c.insets = insetsCol2
        add(input)

        c.gridy++
        c.gridx = 0
    }

    fun addLastRowFilling() {
        c.gridwidth = 2
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(JLabel()) // some nice UI hack ;)
    }
}
