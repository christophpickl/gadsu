package non_test._main_.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.detail.ClientTabTcm3
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.LiveSearchField
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.TreeSearcher
import at.cpickl.gadsu.view.tree.toTreeModel
import non_test.Framed
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane

fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize {
//        ClientTabTcm2(Client.REAL_DUMMY.copy(cprops = CProps.empty))
        ClientTabTcm3(Client.REAL_DUMMY.copy(cprops = CProps.empty), it.bus)
    }
}

private fun simpleTree(): JPanel {
    val searchField = LiveSearchField("")
    val tree: MyTree<XPropEnum, XPropEnumOpt> = MyTree(listOf(XProps.ChiStatus, XProps.BodyConception, XProps.Hungry).toTreeModel())
    tree.initSelected(setOf(XProps.ChiStatusOpts.Inside.opt))
    TreeSearcher(searchField, listOf(tree)) // will work async

    return JPanel().apply {
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
        }, BorderLayout.SOUTH)

    }
}

object NoopModificationChecker : ModificationChecker(NoopModificationAware)
object NoopModificationAware : ModificationAware {
    override fun isModified() = false
}
