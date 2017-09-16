package non_test._main_.view

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyNode
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.MyTreeModel
import non_test.Framed
import java.awt.BorderLayout
import javax.swing.JPanel

// TODOs
// - group nodes NOT selectable
// - select changes selection like list
fun main(args: Array<String>) {
    val searchField = LiveSearchField()
    val tree = MyTree(listOf(XProps.ChiStatus, XProps.BodyConception).toTreeModel())

    Framed.showWithContextDefaultSize {
        JPanel().apply {
            layout = BorderLayout()
            add(searchField.asComponent(), BorderLayout.NORTH)
            add(tree.asComponent(), BorderLayout.CENTER)
        }
    }
}

fun List<XPropEnum>.toTreeModel() = MyTreeModel(
        map { xpropCategory ->
            MyNode.MyGroupNode<XPropEnum, XPropEnumOpt>(xpropCategory, xpropCategory.label, xpropCategory.options.map { option ->
                MyNode.MyLeafNode<XPropEnum, XPropEnumOpt>(option, option.label)
            })
        }
)

private fun createIntTree() = MyTree<Int, Int>(MyTreeModel(listOf(
        MyNode.MyGroupNode(1337, "Atmung", listOf(
                MyNode.MyLeafNode(101, "Husten"),
                MyNode.MyLeafNode(102, "Asthma")
        )),
        MyNode.MyGroupNode(1338, "Schmerzen", listOf(
                MyNode.MyLeafNode(201, "Kopfschmerzen (seitlich)"),
                MyNode.MyLeafNode(201, "Brustkorb Schmerzen"),
                MyNode.MyLeafNode(202, "LWS Schmerzen")
        ))
)))
