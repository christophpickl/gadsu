package at.cpickl.gadsu.view.components

import java.awt.event.InputEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


fun <E> MyTable<E>.registerDoubleClicked(listener: (Int, E) -> Unit) {
    addMouseListener(object : DoubleClickMouseListener() {
        override fun mouseDoubleClicked(event: MouseEvent) {
            val point = event.getPoint()
            val row = rowAtPoint(point)
            val entity = getEntityAt(row)
            log.debug("Row at {} double clicked: {}", row, entity)
            listener.invoke(row, entity)
        }


    })
}

abstract class DoubleClickMouseListener() : MouseAdapter() {

    private var previousWasRight = false

    abstract fun mouseDoubleClicked(event: MouseEvent)

    override fun mousePressed(event: MouseEvent) {
        if (!isProperDoubleClick(event)) return
        mouseDoubleClicked(event)
    }

    private fun isProperDoubleClick(event: MouseEvent): Boolean {
        val isRightButton = (event.getModifiers() and InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK
        val result = event.getClickCount() == 2 && !previousWasRight && !isRightButton
        previousWasRight = isRightButton
        return result
    }
}
