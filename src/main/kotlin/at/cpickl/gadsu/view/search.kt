package at.cpickl.gadsu.view

import at.cpickl.gadsu.isEscape
import at.cpickl.gadsu.view.swing.TextChangeDispatcher
import at.cpickl.gadsu.view.swing.TextChangeListener
import java.awt.Component
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JTextField


class LiveSearchField(viewName: String) {

    private val field = JTextField().apply {
        name = viewName
    }
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
