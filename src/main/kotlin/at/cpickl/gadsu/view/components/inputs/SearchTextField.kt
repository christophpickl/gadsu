package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.view.logic.addChangeListener
import javax.swing.JTextField

class SearchTextField : JTextField() {

    init {
        putClientProperty("JTextField.variant", "search")
    }

    fun addSearchListener(function: (String) -> Unit) {
        addChangeListener { function.invoke(text) }
    }

}

