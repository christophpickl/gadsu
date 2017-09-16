package non_test._main_.view

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyNode
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.MyTreeModel
import at.cpickl.gadsu.view.tree.MyTreeNode
import com.github.christophpickl.kpotpourri.common.enforceAllBranchesCovered
import com.github.christophpickl.kpotpourri.common.logging.LOG
import non_test.Framed
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

// TODOs
// - group nodes NOT selectable
// - select changes selection like list
// enableHoverListener

private class MyTreeModel<G, L>(private val oldModel: TreeSelectionModel) : TreeSelectionModel by oldModel {
    private val log = LOG {}

    // toggle selection on click
    override fun setSelectionPath(path: TreePath) {
        log.trace { "setSelectionPath(path=$path)" }

        if (isPathSelected(path)) {
            removeSelectionPath(path)
        } else {
            addSelectionPath(path)
        }
    }

    override fun addSelectionPath(path: TreePath) {
        val treeNode = path.toMyTreeNode()
        when (treeNode.myNode) {
            is MyNode.MyGroupNode -> {
                // TODO toggleOpenClosedGroup(path) ... otherwise have to double click the entry
                log.trace { "addSelectionPath(path=$path) ... suppressed for group nodes." }
                return
            }
            is MyNode.MyLeafNode -> {
                log.trace { "addSelectionPath(path=$path)" }
                oldModel.addSelectionPath(path)
            }
        }.enforceAllBranchesCovered
    }

    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>
}


private class MyTree2<G, L>(private val model: MyTreeModel<G, L>) : JTree(model.toSwingModel) {
//private class MyTree2<G, L>(root: DefaultMutableTreeNode) : JTree(root) {
    private val log = LOG {}

    init {
        showsRootHandles = true
        isRootVisible = false

        val selectionModelOld = selectionModel
        selectionModel = non_test._main_.view.MyTreeModel<G, L>(selectionModelOld)
    }

}

fun main(args: Array<String>) {
    val searchField = LiveSearchField()
    val tree = MyTree(listOf(XProps.ChiStatus, XProps.BodyConception).toTreeModel())

    Framed.showWithContextDefaultSize {
        JPanel().apply {
            layout = BorderLayout()
            add(searchField.asComponent(), BorderLayout.NORTH)
//            add(tree.asComponent(), BorderLayout.CENTER)
            add(JScrollPane(MyTree2(listOf(XProps.ChiStatus, XProps.BodyConception).toTreeModel())))

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
