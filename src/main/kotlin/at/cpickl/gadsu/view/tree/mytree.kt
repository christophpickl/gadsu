package at.cpickl.gadsu.view.tree

import com.github.christophpickl.kpotpourri.common.enforceAllBranchesCovered
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.common.base.MoreObjects
import java.awt.Component
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultTreeSelectionModel
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel

data class MyTreeNode<G, L>(
        val myNode: MyNode<G, L>,
        val label: String
) : DefaultMutableTreeNode(label) {
    init {
        // back reference
        myNode.treeNode = this
    }
}

sealed class MyNode<G, L> {
    abstract val label: String
    abstract val subNodes: List<MyNode<G, L>>
    abstract fun toTreeNode(): MutableTreeNode

    lateinit var treeNode: MyTreeNode<G, L>

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("label", label)
            .toString()

    class MyGroupNode<G, L>(
            val entity: G,
            override val label: String,
            override val subNodes: List<MyLeafNode<G, L>>
    ) : MyNode<G, L>() {
        override fun toTreeNode() = MyTreeNode(this, label).apply {
            subNodes.forEach { subNode ->
                add(subNode.toTreeNode())
            }
        }
    }

    class MyLeafNode<G, L>(
            val entity: L,
            override val label: String
    ) : MyNode<G, L>() {
        override fun toTreeNode() = MyTreeNode(this, label)

        override val subNodes: List<MyLeafNode<G, L>> = emptyList()
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

private class TreeNodeRenderer<G, L> : DefaultTreeCellRenderer() {
    override fun getTreeCellRendererComponent(tree: JTree, value: Any, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean): Component {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        val node = (value as MyTreeNode<G, L>)
        text = node.label
        return this
    }
}

// https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html#create
class MyTree<G, L>(private val model: MyTreeModel<G, L>) {

    private val tree = object : JTree() {
        override fun fireValueChanged(e: TreeSelectionEvent) {
            // suppress selection of group node when it is collapsed
            if (isValid) {
                super.fireValueChanged(e)
            } else {
                super.clearSelection()
            }
        }
    }

    private val log = LOG {}

    private fun TreePath.toMyTreeNode() = lastPathComponent as MyTreeNode<G, L>

    private val transientSelections = mutableMapOf<TreePath, List<TreePath>>()

    fun asComponent(): Component = JScrollPane(tree)

    private fun printSelected() {
        model.nodes.forEach { node ->
            val treeNode = node.treeNode
            val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>

            groupNode.subNodes.forEach { subNode ->
                val pathToSubNode = TreePath(subNode.treeNode.path)
                val isSelected = tree.isPathSelected(pathToSubNode)
                println("${node.label} => ${subNode.label} => selected: $isSelected")
            }
        }
    }

    private fun storeTransientSelections() {
        model.nodes.forEach { node ->
            val treeNode = node.treeNode
            val treePath = TreePath(treeNode.path)
            val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>

            // store selection transiently
            transientSelections[treePath] = groupNode.subNodes.filter { subNode ->
                val pathToSubNode = TreePath(subNode.treeNode.path)
                val isSelected = tree.isPathSelected(pathToSubNode)
//                println("${node.label} => ${subNode.label} => selected: $isSelected")
                isSelected
            }.map { TreePath(it.treeNode.path) }
        }
    }

    private fun restoreTransientSelections() {
        transientSelections.forEach { _, selectedPaths ->
            selectedPaths.forEach { selectedPath ->
                tree.addSelectionPath(selectedPath)
            }
        }
    }

    init {
        tree.model = model.toSwingModel

        tree.showsRootHandles = true
        tree.isRootVisible = false
        tree.cellRenderer = TreeNodeRenderer<G, L>()

        tree.addTreeWillExpandListener(object : TreeWillExpandListener {
            override fun treeWillCollapse(event: TreeExpansionEvent) {
                log.trace { "treeWillCollapse(event)" }
                storeTransientSelections()

//                val treeNode = event.path.toMyTreeNode()
//                val groupNode = treeNode.myNode as MyNode.MyGroupNode<G, L>
//
//                // store selection transiently
//                transientSelections[event.path] = groupNode.subNodes.filter { subNode ->
//                    val pathToSubNode = TreePath(subNode.treeNode.path)
//                    val isSelected = tree.isPathSelected(pathToSubNode)
//                    isSelected
//                }.map { TreePath(it.treeNode.path) }
            }

            override fun treeWillExpand(event: TreeExpansionEvent) {
                log.trace { "treeWillExpand(event)" }
                val selections = transientSelections[event.path] ?: return
                selections.forEach { selectionPath ->
                    tree.addSelectionPath(selectionPath)
                }
            }
        })

        tree.addTreeExpansionListener(object : TreeExpansionListener {
            override fun treeExpanded(event: TreeExpansionEvent) {
                log.trace { "treeExpanded(event)" }
            }

            override fun treeCollapsed(event: TreeExpansionEvent) {
                log.trace { "treeCollapsed(event)" }
            }
        })

        tree.selectionModel = object : DefaultTreeSelectionModel() {
            init {
                selectionMode = TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION
            }

            override fun addSelectionPath(path: TreePath) {

                val treeNode = path.toMyTreeNode()

                when (treeNode.myNode) {
                    is MyNode.MyGroupNode -> {
                        // TODO toggleOpenClosedGroup(path) ... otherwise have to double click the entry
                        printSelected()
                        restoreTransientSelections()
                        printSelected()
                        log.trace { "addSelectionPath(path=$path) ... suppressed for group nodes." }
                        return
                    }
                    is MyNode.MyLeafNode -> {
                        log.trace { "addSelectionPath(path=$path)" }
                        super.addSelectionPath(path)
                    }
                }.enforceAllBranchesCovered
            }

            private fun toggleOpenClosedGroup(path: TreePath) {
                log.trace { "toggleOpenClosedGroup(path=$path) tree.isExpanded(path)=${tree.isExpanded(path)}" }
                val treeNode = path.toMyTreeNode()
                when (treeNode.myNode) {
                    is MyNode.MyLeafNode<G, L> -> return
                    is MyNode.MyGroupNode<G, L> -> if (tree.isExpanded(path)) tree.collapsePath(path) else tree.expandPath(path)
                }.enforceAllBranchesCovered
            }

            override fun setSelectionPath(path: TreePath) {
                log.trace { "setSelectionPath(path=$path)" }

                if (isPathSelected(path)) {
                    removeSelectionPath(path)
                } else {
                    addSelectionPath(path)
                }
//                treeNode.myNode.entity for Group/Leaf
            }

            override fun setSelectionPaths(path: Array<out TreePath>) {
                log.trace { "setSelectionPaths(path=${path.contentToString()})" }
                super.setSelectionPaths(path)
            }

            override fun removeSelectionPaths(paths: Array<out TreePath>) {
                log.trace { "removeSelectionPaths(path=${paths.contentToString()})" }
                super.removeSelectionPaths(paths)
            }

            override fun removeSelectionPath(path: TreePath) {
                log.trace { "removeSelectionPath(path=$path)" }
                super.removeSelectionPath(path)
            }

        }

        tree.addTreeSelectionListener { event: TreeSelectionEvent ->
            val node = tree.getLastSelectedPathComponent() as DefaultMutableTreeNode?
            log.trace {"tree selected: $node"}
            if (node == null) {
                // nothing selected
            } else {
            }
        }
    }

}
