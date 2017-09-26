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

interface Tree<G, L> {
    val myModel: MyTreeModel<G, L>
    fun setModel2(model: MyTreeModel<G, L>)

    fun isExpanded(it: MyNode.MyGroupNode<G, L>): Boolean
    fun expand(it: MyNode.MyGroupNode<G, L>)

    fun isPathSelected(toPath: TreePath): Boolean
    fun addSelectionNode(node: MyNode<G, L>)
    fun addSelectionPath(selectionPath: TreePath)
    fun addTreeWillExpandListener(listener: TreeWillExpandListener)
}

class MyTree<G, L>(
        override var myModel: MyTreeModel<G, L>,
        viewName: String? = null
) : JTree(myModel.swingModel), Tree<G, L> {

    override fun setModel2(newMyModel: MyTreeModel<G, L>) {
        myModel = newMyModel
        model = newMyModel.swingModel
    }

    init {
        if (viewName != null) name = viewName
        showsRootHandles = true
        isRootVisible = false
        // TODO doesnt work :( cellRenderer = TreeNodeRenderer<G, L>()

        val selectionModelOld = selectionModel
        selectionModel = MyTreeSelectionModel<G, L>(selectionModelOld)
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

    override fun isExpanded(node: MyNode.MyGroupNode<G, L>) = isExpanded(node.toPath())

    override fun expand(node: MyNode.MyGroupNode<G, L>) = expandPath(node.toPath())

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

    override fun addSelectionNode(node: MyNode<G, L>) {
        addSelectionPath(node.toPath())
    }

    private fun MyNode<G, L>.isSelected() = isPathSelected(toPath())

}

class MyTreeModel<G, L>(val nodes: List<MyNode<G, L>>) {

    // expect the top nodes all to be group nodes (no leaves at lvl 1)
    val groupNodes = nodes.map { it as MyNode.MyGroupNode<G, L> }

    val swingModel by lazy {
        DefaultTreeModel(DefaultMutableTreeNode("root ignored").apply {
            nodes.forEach { node ->
                add(node.toTreeNode())
            }
        })
    }

}

class RetainableSelectionsTree<G, L>(val tree: Tree<G, L>) : Tree<G, L> by tree {

    private val log = LOG {}
    private val retainedSelections: MutableMap<L, Boolean> = HashMap()
    private val transientSelections = mutableMapOf<TreePath, List<TreePath>>()

    init {
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

    override fun setModel2(model: MyTreeModel<G, L>) {
        storeAllSelections()
        tree.setModel2(model)
        restoreAllSelections()
    }

    private fun storeAllSelections() {
        myModel.groupNodes.forEach { group ->
            retainedSelections.putAll(
                    group.subNodes.associate {
                        it.entity to it.isSelected()
                    }
            )
        }
    }

    private fun restoreAllSelections() {
        myModel.groupNodes.forEach {
            it.subNodes
                    .filter { retainedSelections[it.entity] == true }
                    .forEach { addSelectionNode(it) }
        }
    }

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


    private fun MyNode<G, L>.isSelected() = isPathSelected(toPath())
    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>

}

class RetainableExpansionsTree<G, L>(val tree: Tree<G, L>) : Tree<G, L> by tree {

    private val retainedExpansions: MutableMap<G, Boolean> = HashMap()

    override fun setModel2(model: MyTreeModel<G, L>) {
        storeExpansions()
        tree.setModel2(model)
        resetRetainedExpansions()
    }

    private fun storeExpansions() {
        retainedExpansions.putAll(
                myModel.groupNodes.associate { it.entity to tree.isExpanded(it) }
        )
    }

    private fun resetRetainedExpansions() {
        myModel.nodes
                .map { it as MyNode.MyGroupNode<G, L> } // TODO rely on this data structure internally (copynpaste much)
                .filter { retainedExpansions[it.entity] == true }
                .forEach { expand(it) }
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
