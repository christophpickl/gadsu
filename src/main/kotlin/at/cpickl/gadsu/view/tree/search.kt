package at.cpickl.gadsu.view.tree

import java.awt.Component
import javax.swing.JTextField


class LiveSearchField {
    private val field = JTextField()
    // add listener

    fun asComponent(): Component = field
}

class SearchableTree {

}
