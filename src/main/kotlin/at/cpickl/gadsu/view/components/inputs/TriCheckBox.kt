package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.view.drawStringCentered
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Icon
import javax.swing.JCheckBox


/**
 * tri-state checkbox has 3 selection states:
 * 0 unselected
 * 1 mid-state selection
 * 2 fully selected
 */
open class TriCheckBox<out T>(
        val item: T,
        text: String? = null,
        preSelect: Int = STATE_NONE,
        private val enableAltSelection: Boolean = false
) :
        JCheckBox(text, preSelect > STATE_HALF), Icon, ActionListener {

    companion object {

        val STATE_NONE = 0
        val STATE_HALF = 1
        val STATE_FULL = 2

        private val CONSIDER_MID_AS_SELECTED = true
        private val MID_SEL_PROP = "SelectionState"
        private val ALT_SEL_PROP = "AlternateSelectionState"
        private val WIDTH: Int
        private val HEIGHT: Int

        init {
//            val icon = UIManager.getIcon("CheckBox.icon")!!
            WIDTH = 20//icon.iconWidth
            HEIGHT = 20//icon.iconHeight
        }
    }


    var altSelectionState: Boolean
        get() = if (getClientProperty(ALT_SEL_PROP) != null)
            getClientProperty(ALT_SEL_PROP) as Boolean
        else
            false
        set(value) {
            putClientProperty(ALT_SEL_PROP, value)
        }

    var selectionState: Int
        get() = if (getClientProperty(MID_SEL_PROP) != null)
            getClientProperty(MID_SEL_PROP) as Int
        else if (super.isSelected())
            STATE_FULL
        else
            STATE_NONE
        set(value) {
            when (value) {
                STATE_FULL -> isSelected = true
                STATE_HALF, STATE_NONE -> isSelected = false
                else -> throw IllegalArgumentException("selectionState = $value")
            }
            putClientProperty(MID_SEL_PROP, value)
        }


    init {
        selectionState = preSelect
        preferredSize = Dimension(WIDTH, HEIGHT)
        @Suppress("LeakingThis")
        icon = this
        @Suppress("LeakingThis")
        addActionListener(this)
    }

    override fun isSelected() =
            if (CONSIDER_MID_AS_SELECTED && selectionState > STATE_NONE)
                true
            else
                super.isSelected()

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {

        if (enableAltSelection && altSelectionState) {
            g.color = Color.YELLOW
        } else {
            g.color = Color.WHITE
        }
        g.fillRect(0, 0, WIDTH, HEIGHT)

        g.color = Color.BLACK
        g.drawRect(0, 0, WIDTH, HEIGHT)

        if (selectionState != 0) {
            g.color = Color.BLACK
            val oldFont = g.font
            g.font = Font("Arial", Font.BOLD, 20)
            g.drawStringCentered(if (selectionState == 1) "-" else "+", WIDTH, HEIGHT, 3)
            g.font = oldFont
        }

    }

    override fun getIconWidth() = WIDTH
    override fun getIconHeight() = HEIGHT

    override fun actionPerformed(e: ActionEvent) {
        if (enableAltSelection && (e.modifiers and (ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)) {
            altSelectionState = !altSelectionState
            // maybe fire changed event
            return
        }
        if (selectionState == STATE_NONE) isSelected = false
        selectionState = if (selectionState == STATE_FULL) STATE_NONE else selectionState + 1
    }

}
