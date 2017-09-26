package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.testinfra.ui.TestContainer
import at.cpickl.gadsu.testinfra.ui.TestContainerStarter
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyNode
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.MyTreeModel
import at.cpickl.gadsu.view.tree.MyTreeNode
import at.cpickl.gadsu.view.tree.TreeSearcher
import com.natpryce.hamkrest.equalTo
import org.testng.annotations.Test
import org.uispec4j.Tree
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

@Test
class TreeSearcherUiTest : SimpleUiTest() {

    private val viewNameSearchField = "viewNameSearchField"
    private val viewNameTree = "viewNameTree"

    private val container get() = window!!.getPanel(TestContainer.viewName)!!.awtComponent as TestContainer
    private val tree get() = window!!.getTree(viewNameTree)!!
    private val searchField get() = window!!.getInputTextBox(viewNameSearchField)!!

    override fun postInit(window: Window) {}
    override fun newMainClassAdapter() = MainClassAdapter(TestContainerStarter::class.java)

    //<editor-fold desc="Search">

    fun `Given 'wal' and 'biene', When seach for 'a', Then content should only disply 'wal'`() {
        initContainer(listOf(
                createGroup("x", "wal", "biene")
        ))

        searchFor("a")

        tree.assertContentEquals(listOf("wal"))
    }

    fun `Given subnode 'kaelte', When search for 'kalt', Then display umlaut-insensitive 'kaelte'`() {
        initContainer(singleTreeItem("kälte"))

        searchFor("kalt")

        tree.assertContentEquals(listOf("kälte"))
    }

    fun `Given 'zunge kalt' and 'zunge heiss', When search for 'zu kalt', Then should only show 'zunge kalt'`() {
        initContainer(listOf(
                createGroup("x", "zunge kalt", "zunge heiss")
        ))

        searchFor("zu kalt")

        tree.assertContentEquals(listOf("zunge kalt"))
    }

    //</editor-fold>

    //<editor-fold desc="Expand/collapse">

    fun `Given 'a' is expanded and 'a1' selected, When collapse and expand 'a', Then 'a1' is still selected`() {
        initContainer(listOf(
                createGroup("a", "a1")
        ))
        tree.expand("a")
        tree.select("a/a1")

        tree.collapse("a")
        tree.expand("a")

        assertTrue(tree.selectionEquals("a/a1"))
    }

    //</editor-fold>

    //<editor-fold desc="Search and selection">

    fun `Given nothing selected, When search for 'a' and select 'a1' and clear search, Then 'a1' is still selected`() {
        initContainer(listOf(
                createGroup("a", "a1")
        ))

        searchFor("a")
        tree.click("a")
        tree.click("a/a1")
        resetSearch()

        assertTrue(tree.selectionEquals("a/a1"))
    }


    fun `Given 'a1' is selected, When search for 'a' and clear search again, Then 'a1' is still selected`() {
        initContainer(listOf(
                createGroup("a", "a1")
        ))
        tree.expand("a")
        tree.select("a/a1")

        searchFor("a")
        resetSearch()

        assertTrue(tree.selectionEquals("a/a1"))
    }


    fun `Given searched for 'a' and 'a1' selected, When reset search, Then 'a1' is still selected`() {
        initContainer(listOf(
                createGroup("a", "a1")
        ))
        searchFor("a")
        tree.expand("a")
        tree.select("a/a1")

        resetSearch()

        assertTrue(tree.selectionEquals("a/a1"))
    }

    //</editor-fold>


    //<editor-fold desc="Test infrastructure">


    private fun searchFor(term: String) {
        searchField.appendText(term)
    }

    private fun resetSearch() {
        searchField.setText("", false)
    }

    private fun initContainer(nodes: List<MyNode<String, String>>) {
        container.setView(
                JPanel().apply {
                    val searchField = LiveSearchField(viewNameSearchField)
                    val trees = listOf(MyTree(MyTreeModel(nodes), viewNameTree))
                    TreeSearcher(searchField, trees)

                    add(searchField.asComponent())
                    trees.forEach { add(it) }
                }
        )
    }

    private fun createGroup(groupLabel: String, vararg subLabels: String) =
            MyNode.MyGroupNode<String, String>(groupLabel, groupLabel, subLabels.map { MyNode.MyLeafNode<String, String>(it, it) })

    private fun singleTreeItem(label: String) = listOf(createGroup("ignore", label))

    @Suppress("UNCHECKED_CAST")
    private fun Tree.assertContentEquals(vararg expected: List<String>) {
        val actual = mutableListOf<List<String>>()
        val rootNode = jTree.model.root as DefaultMutableTreeNode
        1.rangeTo(rootNode.childCount).forEach { i ->
            val groupValues = mutableListOf<String>()
            val groupNode = rootNode.getChildAt(i - 1) as MyTreeNode<String, String>
//          groupValues += groupNode.label ... nope
            1.rangeTo(groupNode.childCount).forEach { j ->
                val subNode = groupNode.getChildAt(j - 1) as MyTreeNode<String, String>
                groupValues += subNode.label
            }
            actual += groupValues
        }
        com.natpryce.hamkrest.assertion.assertThat("Tree content does not match!", actual, equalTo(expected.toList()))
    }

    private fun Tree.expand(path: String) {
        expand(path, true)
    }

    private fun Tree.collapse(path: String) {
        expand(path, false)
    }

    //</editor-fold>
}
