package at.cpickl.gadsu.view.components

import javax.swing.JButton
import javax.swing.JPanel

interface ExpandCollapseListener {
    fun onExpand()
    fun onCollapse()
}

class ExpandCollapsePanel(listener: ExpandCollapseListener) : JPanel() {
    init {
        add(JButton("+").apply {
            addActionListener {
                listener.onExpand()
            }
        })
        add(JButton("-").apply {
            addActionListener {
                listener.onCollapse()
            }
        })
    }
}
