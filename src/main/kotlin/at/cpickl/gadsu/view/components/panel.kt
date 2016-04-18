package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.debugColor
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JPanel

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
}
