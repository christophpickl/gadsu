@file:Suppress("UNCHECKED_CAST")

package at.cpickl.gadsu.view.tree

import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import com.github.christophpickl.kpotpourri.common.enforceAllBranchesCovered
import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.awt.Component
import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

// TODO-s
// enableHoverListener

fun List<XPropEnum>.toTreeModel() = MyTreeModel(
        map { xpropCategory ->
            MyNode.MyGroupNode<XPropEnum, XPropEnumOpt>(xpropCategory, xpropCategory.label, xpropCategory.options.map { option ->
                MyNode.MyLeafNode<XPropEnum, XPropEnumOpt>(option, option.label)
            })
        }
)

class MyTree2<G, L>(myModel: MyTreeModel<G, L>) : JTree(myModel.toSwingModel) {
    private val log = LOG {}

    private val transientSelections = mutableMapOf<TreePath, List<TreePath>>()

    init {
        showsRootHandles = true
        isRootVisible = false
        // TODO doesnt work :( cellRenderer = TreeNodeRenderer<G, L>()

        val selectionModelOld = selectionModel
        selectionModel = MyTreeSelectionModel<G, L>(selectionModelOld)

        addTreeWillExpandListener(object : TreeWillExpandListener {
            override fun treeWillCollapse(event: TreeExpansionEvent) {
                log.trace { "treeWillCollapse(event)" }
                storeTransientSelections(event.path)
            }

            override fun treeWillExpand(event: TreeExpansionEvent) {
                log.trace { "treeWillExpand(event)" }
                restoreTransientSelection(event.path)
            }
        })
    }

    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>

    private fun storeTransientSelections(treePath: TreePath) {
        val treeNode = treePath.toMyTreeNode()
        val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>

        transientSelections[treePath] = groupNode.subNodes.filter { subNode ->
            isPathSelected(TreePath(subNode.treeNode.path))
        }.map { TreePath(it.treeNode.path) }
    }

    private fun restoreTransientSelection(path: TreePath) {
        val selections = transientSelections[path] ?: return
        selections.forEach { selectionPath ->
            addSelectionPath(selectionPath)
        }
    }

}

class MyTreeModel<G, L>(val nodes: List<MyNode<G, L>>) {
    val toSwingModel by lazy {
        DefaultTreeModel(DefaultMutableTreeNode("root ignored").apply {
            nodes.forEach { node ->
                add(node.toTreeNode())
            }
        })
    }

}

private class MyTreeSelectionModel<G, L>(private val oldModel: TreeSelectionModel) : TreeSelectionModel by oldModel {
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

private class TreeNodeRenderer<G, L> : DefaultTreeCellRenderer() {
    override fun getTreeCellRendererComponent(tree: JTree, value: Any, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean): Component {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        val node = (value as MyTreeNode<G, L>)
        text = node.label
        return this
    }
}
