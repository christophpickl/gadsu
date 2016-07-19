package at.cpickl.gadsu.view.datepicker

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon


class JNextIcon(
        width: Int, height: Int,
        private var doubleArrow: Boolean, // was originally public
        private val enabled: Boolean
) : Icon {

    private var width: Int = 0
    private var height: Int = 0

    private val xPoints = IntArray(3)
    private val yPoints = IntArray(3)

    init {
        setDimension(width, height)
    }

    override fun getIconWidth(): Int {
        return width
    }

    override fun getIconHeight(): Int {
        return height
    }

    private fun setDimension(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        if (enabled) {
            g.color = Color.BLACK
        } else {
            g.color = Color.GRAY
        }

        if (doubleArrow) {
            xPoints[0] = x + width / 2
            yPoints[0] = y + height / 2

            xPoints[1] = x
            yPoints[1] = y - 1

            xPoints[2] = x
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)

            xPoints[0] = x + width
            yPoints[0] = y + height / 2

            xPoints[1] = x + width / 2
            yPoints[1] = y - 1

            xPoints[2] = x + width / 2
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)
        } else {
            xPoints[0] = x + width
            yPoints[0] = y + height / 2

            xPoints[1] = x
            yPoints[1] = y - 1

            xPoints[2] = x
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)
        }
    }

}


class JPreviousIcon(
        width: Int, height: Int,
        private val doubleArrow: Boolean,
        private val enabled: Boolean
) : Icon {

    private var width: Int = 0
    private var height: Int = 0

    private val xPoints = IntArray(3)
    private val yPoints = IntArray(3)

    init {
        setDimension(width, height)
    }

    override fun getIconWidth(): Int {
        return width
    }

    override fun getIconHeight(): Int {
        return height
    }

    fun setDimension(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        if (enabled) {
            g.color = Color.BLACK
        } else {
            g.color = Color.GRAY
        }

        if (doubleArrow) {
            xPoints[0] = x
            yPoints[0] = y + height / 2

            xPoints[1] = x + width / 2
            yPoints[1] = y - 1

            xPoints[2] = x + width / 2
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)

            xPoints[0] = x + width / 2
            yPoints[0] = y + height / 2

            xPoints[1] = x + width
            yPoints[1] = y - 1

            xPoints[2] = x + width
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)
        } else {
            xPoints[0] = x
            yPoints[0] = y + height / 2

            xPoints[1] = x + width
            yPoints[1] = y - 1

            xPoints[2] = x + width
            yPoints[2] = y + height

            g.fillPolygon(xPoints, yPoints, 3)
        }
    }

}

