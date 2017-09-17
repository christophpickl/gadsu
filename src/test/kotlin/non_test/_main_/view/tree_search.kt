package non_test._main_.view

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.toTreeModel
import non_test.Framed
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane


fun main(args: Array<String>) {
    val searchField = LiveSearchField()
    val tree: MyTree<XPropEnum, XPropEnumOpt> = MyTree(listOf(XProps.ChiStatus, XProps.BodyConception, XProps.Hungry).toTreeModel())
    tree.initSelected(setOf(XProps.ChiStatusOpts.Inside.opt))

    Framed.showWithContextDefaultSize {
        JPanel().apply {
            layout = BorderLayout()
            add(searchField.asComponent(), BorderLayout.NORTH)
            add(JScrollPane(tree), BorderLayout.CENTER)
            add(JPanel().apply {
                layout = FlowLayout()
                add(JButton("Read selected").apply {
                    addActionListener {
                        println("selected: ${tree.readSelected()}")
                    }
                })
            }, BorderLayout.NORTH)

        }
    }
}
