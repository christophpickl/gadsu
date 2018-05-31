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

    private val log = LOG {}

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

    override fun setModel2(model: MyTreeModel<G, L>) {
        log.trace { "setModel2(model)" }
        myModel = model
        this.model = model.swingModel
    }

    override fun addSelectionPath(selectionPath: TreePath) {
        log.trace { "addSelectionPath(path=$selectionPath)" }
        super.addSelectionPath(selectionPath)
    }

    override fun isExpanded(it: MyNode.MyGroupNode<G, L>) = isExpanded(it.toPath())

    override fun expand(it: MyNode.MyGroupNode<G, L>) = expandPath(it.toPath().apply { log.debug { "expand(node.path=$this)" } })

    fun readSelected(): List<L> = allLeafNodes().filter { it.isSelected() }.map { it.entity }

    fun expandAll() {
        myModel.nodes.forEach { categoryNode ->
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

    companion object {
        val rootContent = "ROOT"
    }

    // expect the top nodes all to be group nodes (no leaves at lvl 1)
    val groupNodes = nodes.map { it as MyNode.MyGroupNode<G, L> }

    val swingModel by lazy {
        DefaultTreeModel(DefaultMutableTreeNode(rootContent).apply {
            nodes.forEach { node ->
                add(node.toTreeNode())
            }
        })
    }

}

class RetainableSelectionsTree<G, L>(val tree: Tree<G, L>) : Tree<G, L> by tree {

    private val log = LOG {}
    private val collapseSelections = mutableMapOf<TreePath, List<TreePath>>()
    private val modelSelections: MutableMap<L, Boolean> = HashMap()

    init {
        addTreeWillExpandListener(object : TreeWillExpandListener {
            override fun treeWillCollapse(event: TreeExpansionEvent) {
                storeCollapseSelections(event.path)
            }

            override fun treeWillExpand(event: TreeExpansionEvent) {
                restoreCollapseSelection(event.path)
            }
        })
    }

    override fun setModel2(model: MyTreeModel<G, L>) {
        storeModelSelections()
        tree.setModel2(model)
        restoreModelSelections()
    }

    private fun storeCollapseSelections(path: TreePath) {
        val treeNode = path.toMyTreeNode()
        log.trace { "storeCollapseSelections(node=${treeNode.label})" }
        val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>

        collapseSelections[path] = groupNode.subNodes.filter { subNode ->
            subNode.isSelected()
        }.map { it.toPath().apply { log.trace { "stored collapsed selection: ${it.label}" } } }
    }

    private fun restoreCollapseSelection(path: TreePath) {
        log.trace { "restoreCollapseSelection(path=$path) because of expand: ${collapseSelections.values.joinToString("; ") {
            it.joinToString(",") { it.toMyTreeNode().label }
        }}}" }
        val selections = collapseSelections[path] ?: return
        selections.forEach { selectionPath ->
            addSelectionPath(selectionPath)
        }
    }

    private fun storeModelSelections() {
        log.trace { "storeModelSelections()" }
        myModel.groupNodes.forEach { group ->
            modelSelections.putAll(
                    group.subNodes.associate {
                        Pair(it.entity, it.isSelected()).apply { if (second) log.trace { "store selection for: $first" } }
                    }
            )
        }
    }

    private fun restoreModelSelections() {
        log.trace { "restoreModelSelections(): selection count = ${modelSelections.values.count { it }}" }
        myModel.groupNodes.forEach {
            it.subNodes
                    .filter { modelSelections[it.entity] == true }
                    .forEach { addSelectionNode(it); log.trace { "restore model selection for: ${it.entity}" } }
        }
    }

    private fun MyNode<G, L>.isSelected() = isPathSelected(toPath())
    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>

}

class RetainableExpansionsTree<G, L>(val tree: Tree<G, L>) : Tree<G, L> by tree {

    private val log = LOG {}
    private val retainedExpansions: MutableMap<G, Boolean> = HashMap()

    override fun setModel2(model: MyTreeModel<G, L>) {
        log.trace { "setModel2(model)" }
        storeExpansions()
        tree.setModel2(model)
        resetRetainedExpansions()
    }

    private fun storeExpansions() {
        log.trace { "storeExpansions()" }
        retainedExpansions.putAll(
                myModel.groupNodes.associate { it.entity to tree.isExpanded(it) }
        )
    }

    private fun resetRetainedExpansions() {
        log.trace { "storeExpansions()" }
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
        if (isPathSelected(path)) {
            log.trace { "setSelectionPath(path=$path) ... removing" }
            removeSelectionPath(path)
        } else {
            log.trace { "setSelectionPath(path=$path) ... adding" }
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
