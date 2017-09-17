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

fun buildTree(xpropCategories: List<XPropEnum>) =
        MyTree<XPropEnum, XPropEnumOpt>(xpropCategories.toTreeModel())

fun List<XPropEnum>.toTreeModel() = MyTreeModel(
        map { xpropCategory ->
            MyNode.MyGroupNode<XPropEnum, XPropEnumOpt>(xpropCategory, xpropCategory.label, xpropCategory.options.map { option ->
                MyNode.MyLeafNode<XPropEnum, XPropEnumOpt>(option, option.label)
            })
        }
)

class MyTree<G, L>(var myModel: MyTreeModel<G, L>) : JTree(myModel.swingModel) {
    private val log = LOG {}

    private val transientSelections = mutableMapOf<TreePath, List<TreePath>>()

    fun setModel2(newMyModel: MyTreeModel<G, L>) {
        myModel = newMyModel
        model = newMyModel.swingModel
    }

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

    // can even pass leaf entities which are not contained as nodes, and won't complain :)
    fun initSelected(preselected: Set<L>) {
        clearSelection()
        allLeafNodes().forEach { node ->
            if (preselected.contains(node.entity)) {
                addSelectionNode(node)
            }
        }
    }

    fun readSelected(): List<L> = allLeafNodes().filter { it.isSelected() }.map { it.entity }

    fun expandAll() {
        myModel.nodes.forEach { categoryNode ->
            // FIXME doesnt work
            expandPath(categoryNode.toPath())
        }
    }

    fun collapseAll() {
        myModel.nodes.forEach { categoryNode ->
            collapsePath(categoryNode.toPath())
        }
    }

    private fun allLeafNodes(): List<MyNode.MyLeafNode<G, L>> {
        val nodes = mutableListOf<MyNode.MyLeafNode<G, L>>()
        myModel.nodes.forEach { categoryNode ->
            categoryNode.subNodes.map { it as MyNode.MyLeafNode<G, L> }.forEach { symptomNode ->
                nodes.add(symptomNode)
            }
        }
        return nodes
    }

    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>

    private fun storeTransientSelections(treePath: TreePath) {
        val treeNode = treePath.toMyTreeNode()
        val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>

        transientSelections[treePath] = groupNode.subNodes.filter { subNode ->
            subNode.isSelected()
        }.map { it.toPath() }
    }

    private fun restoreTransientSelection(path: TreePath) {
        val selections = transientSelections[path] ?: return
        selections.forEach { selectionPath ->
            addSelectionPath(selectionPath)
        }
    }

    private fun addSelectionNode(node: MyNode<G, L>) {
        addSelectionPath(node.toPath())
    }

    private fun MyNode<G, L>.isSelected() = isPathSelected(toPath())

}

class MyTreeModel<G, L>(val nodes: List<MyNode<G, L>>) {
    val swingModel by lazy {
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
