package at.cpickl.gadsu.view.tree

import at.cpickl.gadsu.view.LiveSearchField
import com.github.christophpickl.kpotpourri.common.logging.LOG

// controller logic
class TreeSearcher<G, L>(
        private val searchField: LiveSearchField,
        simpleTrees: List<MyTree<G, L>>
) {

    private val log = LOG {}
    private val searchTrees: List<SearchableTree<G, L>> = simpleTrees.map {
        SearchableTree(
                RetainableSelectionsTree(
                        RetainableExpansionsTree(
                                it
                        )
                )
        )
    }

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


// TODO during search selection doesnt work
class SearchableTree<G, L>(private val tree: Tree<G, L>) : Tree<G, L> by tree {

    private val log = LOG {}
    private val originalData: MyTreeModel<G, L> = tree.myModel

    fun restoreOriginalData() {
        log.debug { "restoreOriginalData()" }
        tree.setModel2(originalData)
    }

    fun search(terms: List<String>) {
        // MINOR or also search in group node itself for label?!
        @Suppress("UNCHECKED_CAST")
        val filtered = originalData.nodes
                .map { groupNode -> Pair(groupNode, groupNode.subNodes.filter { it.label.matchSearch(terms) }) }
                .filter { (_, subs) -> subs.isNotEmpty() }
                .map { (it.first as MyNode.MyGroupNode<G, L>).copy(it.second as List<MyNode.MyLeafNode<G, L>>) }
        tree.setModel2(MyTreeModel<G, L>(filtered))
    }

    private fun String.matchSearch(terms: List<String>): Boolean {
        val lowerThis = this.toLowerCase().replaceUmlauts()
        return terms.all { term -> lowerThis.contains(term.toLowerCase()) }
    }

}

fun String.replaceUmlauts()
        = replace("ä", "a").
        replace("ö", "o").
        replace("ü", "u")

