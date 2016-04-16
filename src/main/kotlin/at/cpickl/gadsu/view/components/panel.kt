package at.cpickl.gadsu.view.components

import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

open class GridPanel : JPanel() {
    val c = GridBagConstraints()
    init {
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
    init {
        c.anchor = GridBagConstraints.WEST
    }

    fun addFormInput(label: String, input: Component) {
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(JLabel(label))

        c.gridx++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(input)

        c.gridy++
        c.gridx = 0
    }
}
