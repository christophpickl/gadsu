package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.view.logic.addChangeListener
import at.cpickl.gadsu.view.swing.enableSearchVariant
import javax.swing.JTextField

class SearchTextField : JTextField() {

    init {
        enableSearchVariant()
    }

    fun addSearchListener(function: (String) -> Unit) {
        addChangeListener { function.invoke(text) }
    }

}
