package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.testinfra.ui.BaseDriver
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyNode
import at.cpickl.gadsu.view.tree.MyTree
import at.cpickl.gadsu.view.tree.MyTreeModel
import at.cpickl.gadsu.view.tree.TreeSearcher
import com.github.christophpickl.kpotpourri.test4k.skip
import non_test.Framed
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import javax.swing.JPanel

@Test
class TreeSearcherUiTest : SimpleUiTest() {

    private lateinit var driver: TreeDriver
    private val tree get() = driver.tree
    private val searchField get() = driver.searchField

    override fun postInit(window: Window) {
        driver = TreeDriver(this, window)
    }

    override fun newMainClassAdapter() = MainClassAdapter(TreeSearchStarter::class.java)



    fun `Given sub1-1 is selected, When search for 'a' and clear search again, Then sub1-1 should still be selected`() {
        tree.select(Node.Sub11_Path)

        searchField.appendText("a")
        searchField.setText("", false)

        assertTrue(tree.selectionEquals(Node.Sub11_Path))
    }

    //<editor-fold desc="Description">
    fun `Given sub1-1 is selected, When search for 'a', Then sub1-1 should still be selected`() {
        skip("WIP")
        tree.select(Node.Sub11_Path)

        searchField.appendText("a")

        assertTrue(tree.selectionEquals(Node.Sub11_Path))
    }
    //</editor-fold>

}


object TreeSearchStarter {

    val viewNameSearchField = "viewNameSearchField"
    val viewNameTree = "viewNameTree"

    @JvmStatic
    fun main(cliArgs: Array<String>) {
        Framed.showWithContextDefaultSize {
            // ClientTabTcm2(Client.REAL_DUMMY, NoopModificationChecker, it.bus)
            JPanel().apply {
                val searchField = LiveSearchField(viewNameSearchField)
                val trees = listOf(MyTree(MyTreeModel(listOf(
                        createGroup(Node.Group1, Node.Sub11, Node.Sub12),
                        createGroup(Node.Group2, Node.Sub21)
                )), viewNameTree))
                TreeSearcher(searchField, trees)

                add(searchField.asComponent())
                trees.forEach { add(it) }
            }
        }
    }

    private fun createGroup(key: String, vararg subs: String) =
            MyNode.MyGroupNode<String, String>(key, key, subs.map { MyNode.MyLeafNode<String, String>(it, it) })

}

class TreeDriver(test: TreeSearcherUiTest, window: Window) : BaseDriver<TreeSearcherUiTest>(test, window) {
    val searchField = window.getInputTextBox(TreeSearchStarter.viewNameSearchField)!!
    val tree = window.getTree(TreeSearchStarter.viewNameTree)!!
}

private object Node {
    val Group1 = "group1"
    val Sub11 = "sub1-1 aaa"
    val Sub11_Path = "$Group1/$Sub11"
    val Sub12 = "sub1-2 aaa"
    val Sub12_Path = "$Group1/$Sub12"

    val Group2 = "group2"
    val Sub21 = "sub2-1 äöü"
    val Sub21_Path = "$Group1/$Sub21"
}
