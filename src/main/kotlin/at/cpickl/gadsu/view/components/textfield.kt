package at.cpickl.gadsu.view.components

import javax.swing.JTextField

class SearchTextField : JTextField() {

    init {
        putClientProperty("JTextField.variant", "search")
    }

    fun addSearchListener(function: (String) -> Unit) {
        addChangeListener { function.invoke(text) }
    }

}
