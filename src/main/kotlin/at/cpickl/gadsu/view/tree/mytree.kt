package at.cpickl.gadsu.view.tree

import java.awt.Component
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.event.TreeSelectionEvent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode


sealed class MyNode<G, L> {
    abstract val label: String
    abstract val subNodes: List<MyNode<G, L>>

    abstract fun toTreeNode(): MutableTreeNode

    data class MyGroupNode<G, L>(
            val entity: G,
            override val label: String,
            override val subNodes: List<MyNode<G, L>>
    ) : MyNode<G, L>() {
        override fun toTreeNode() = DefaultMutableTreeNode(label).apply {
            subNodes.forEach { subNode ->
                add(subNode.toTreeNode())
            }
        }
    }

    data class MyLeafNode<G, L>(
            val entity: L,
            override val label: String
    ) : MyNode<G, L>() {
        override fun toTreeNode() = DefaultMutableTreeNode(label)

        override val subNodes: List<MyLeafNode<G, L>> = emptyList()
    }

}

class MyTreeModel<G, L>(private val nodes: List<MyNode<G, L>>) {
    val toSwingModel by lazy {
        DefaultTreeModel(DefaultMutableTreeNode("root ignored").apply {
            nodes.forEach { node ->
                add(node.toTreeNode())
            }
        })
    }

}

// https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html#create
class MyTree<G, L>(private val model: MyTreeModel<G, L>) {

    private val tree = JTree()

    init {
        updateModel(model)

        tree.showsRootHandles = true
        tree.isRootVisible = false
/*
 tree.setCellRenderer(new DefaultTreeCellRenderer() {
      public Component getTreeCellRendererComponent(JTree tree,
          Object value, boolean sel, boolean expanded, boolean leaf,
          int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded,
            leaf, row, hasFocus);
        if (!((InvisibleNode) value).isVisible()) {
          setForeground(Color.yellow);
        }
        return this;
      }
    });
 */

        // TODO selection just as for MyList
        // tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        tree.addTreeSelectionListener { event: TreeSelectionEvent ->
            val node = tree.getLastSelectedPathComponent() as DefaultMutableTreeNode?
            if (node == null) {
                // nothing selected
            } else {
                println("selected: $node")
//                if (node.isLeaf) {
//                    val leafNode = node.userObject as MyNode.MyLeafNode<G, L>
//                    println(leafNode)
//                } else {
//                    val groupNode = node.userObject as MyNode.MyGroupNode<G, L>
//                    println(groupNode)
//                }
            }
        }
    }

    fun asComponent(): Component = JScrollPane(tree)

    private fun updateModel(model: MyTreeModel<G, L>) {
        val root = DefaultMutableTreeNode("root")
        root.add(DefaultMutableTreeNode("sub1 aaa"))
        tree.model = model.toSwingModel
    }

}
