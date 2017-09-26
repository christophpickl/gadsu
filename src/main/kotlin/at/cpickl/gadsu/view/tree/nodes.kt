package at.cpickl.gadsu.view.tree

import com.google.common.base.MoreObjects
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreePath

data class MyTreeNode<G, L>(
        val myNode: MyNode<G, L>,
        val label: String
) : DefaultMutableTreeNode(label) {
    init {
        // back reference
        myNode.treeNode = this
    }

    override fun toString() = label // tree cell render hack ;)
}

sealed class MyNode<G, L> {
    abstract val label: String
    abstract val subNodes: List<MyNode<G, L>>
    abstract fun toTreeNode(): MutableTreeNode

    lateinit var treeNode: MyTreeNode<G, L>

    fun toPath() = TreePath(treeNode.path)

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

        fun copy(subNodes: List<MyLeafNode<G, L>>) = MyGroupNode(entity = entity, label = label, subNodes = subNodes)
    }

    class MyLeafNode<G, L>(
            val entity: L,
            override val label: String
    ) : MyNode<G, L>() {
        override fun toTreeNode() = MyTreeNode(this, label)

        override val subNodes: List<MyLeafNode<G, L>> = emptyList()
    }

}
