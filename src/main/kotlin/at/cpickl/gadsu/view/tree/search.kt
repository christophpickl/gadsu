package at.cpickl.gadsu.view.tree

import at.cpickl.gadsu.isEscape
import at.cpickl.gadsu.view.swing.TextChangeDispatcher
import at.cpickl.gadsu.view.swing.TextChangeListener
import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.awt.Component
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JTextField

// controller logic
class TreeSearcher<G, L>(
        private val searchField: LiveSearchField,
        simpleTrees: List<MyTree<G, L>>
) {

    private val log = LOG {}
    private val searchTrees: List<SearchableTree<G, L>> = simpleTrees.map { SearchableTree(it) }

    init {
        searchField.addListener { searchText ->
            val search = searchText.trim()
            if (search.isEmpty()) {
                clearSearch()
            } else {
                doSearch(search.split(" "))
            }
        }
    }

    private fun doSearch(terms: List<String>) {
        log.debug { "doSearch(terms=$terms)" }
        searchTrees.forEach { tree ->
            tree.search(terms)
        }
    }

    private fun clearSearch() {
        log.debug { "clearSearch()" }
        searchTrees.forEach { tree ->
            tree.restoreOriginalData()
        }
    }

}

class LiveSearchField() {

    private val field = JTextField()
    private val dispatcher = TextChangeDispatcher(field)

    init {
        field.addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                if (e.isEscape) {
                    field.text = ""
                }
            }
        })
    }

    fun addListener(listener: TextChangeListener) {
        dispatcher.addListener(listener)
    }

    fun asComponent(): Component = field

}

class SearchableTree<G, L>(val tree: MyTree<G, L>) {

    private val originalData: MyTreeModel<G, L> = tree.myModel

    fun restoreOriginalData() {
        tree.model = originalData.swingModel
    }

    fun search(terms: List<String>) {
        // FIXME do it for leaves!
        val filtered: List<MyNode<G, L>> = originalData.nodes.filter { it.label.matchSearch(terms) }
        tree.model = MyTreeModel<G, L>(filtered).swingModel
    }

    private fun String.matchSearch(terms: List<String>): Boolean {
        val lowerThis = this.toLowerCase()
        return terms.any { term -> lowerThis.contains(term.toLowerCase()) }
    }


}
