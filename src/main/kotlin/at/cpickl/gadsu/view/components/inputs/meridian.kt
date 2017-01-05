package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ChangeAware
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JCheckBox
import javax.swing.JComponent

enum class MeridianSelectorLayout {
    Horizontal,
    Vertical
}

class MeridianSelector(
        private val renderLayout: MeridianSelectorLayout = MeridianSelectorLayout.Vertical
) : ChangeAware {

    private val checkboxes = Meridian.values().map(::MeridianCheckBox)

    var selectedMeridians: List<Meridian>
        get() = checkboxes.filter { it.isSelected }.map { it.meridian }
        set(value) {
            checkboxes.forEach {
                it.isSelected = value.contains(it.meridian)
            }
        }

    val component: JComponent by lazy {
        GridPanel().apply {
            checkboxes.forEachIndexed { i, checkbox ->
                val isTopOrLeft = i % 2 == 0

                if (isTopOrLeft) {
                    if (i != 0) {
                        if (renderLayout == MeridianSelectorLayout.Horizontal) {
                            c.gridx++
                            c.gridy = 0
                        } else {
                            c.gridx = 0
                            c.gridy++
                        }
                    }
                } else {
                    if (renderLayout == MeridianSelectorLayout.Horizontal) {
                        c.gridy++
                    } else {
                        c.gridx++
                    }
                }
                add(checkbox)
            }
        }
    }

    override fun onChange(changeListener: () -> Unit) {
        checkboxes.forEach {
            it.addActionListener { changeListener() }
        }
    }

    fun  isAnySelectedMeridianDifferentFrom(treatedMeridians: List<Meridian>) =
            selectedMeridians != treatedMeridians

}

private class MeridianCheckBox(val meridian: Meridian) : JCheckBox(), Icon {
    companion object {
        private val SIZE = 40
    }

    private var isOver = false

    init {
        isFocusable = false
        preferredSize = Dimension(SIZE, SIZE)
        @Suppress("LeakingThis")
        icon = this

        addMouseListener(object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                isOver = true
                repaint()
            }

            override fun mouseExited(e: MouseEvent) {
                isOver = false
                repaint()
            }
        })
    }

    override fun getIconWidth() = SIZE
    override fun getIconHeight() = SIZE

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        val baseColor = meridian.element.color
        g.color = if (isSelected) {
            baseColor.darker()
        } else if (isOver) {
            baseColor
        } else {
            baseColor.brighter()
        }
        g.fillRect(0, 0, SIZE, SIZE)

        g.color = if (isSelected) {
            Color.WHITE
        } else if (isOver) {
            Color.GRAY
        } else {
            Color.GRAY.darker()
        }
        g.font = g.font.deriveFont(Font.BOLD, 14.0F)
        g.drawString(meridian.labelShort, 10, 25)
    }

}

