package at.cpickl.gadsu.view.swing

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Point
import javax.swing.JList

val <T> JList<T>.log: Logger
    get() = LoggerFactory.getLogger(JList::class.java)



fun <T> JList<T>.myLocationToIndex(point: Point): Int {
    // uispec4j bug: https://github.com/UISpec4J/UISpec4J/issues/30
    return locationToIndex(point) // returns _closest_ index! :(
    //    for (i in 0.rangeTo(model.size - 1)) {
    //        val bounds = getCellBounds(i, i)
    //        if (point.y <= bounds.y + bounds.height) {
    //            return i
    //        }
    //    }
    //    log.debug("No cell found for given point: {}", point)
    //    return -1
}

fun <T> JList<T>.elementAtPoint(point: Point): Pair<Int, T>? {
    log.trace("elementAtIndex(point={})", point)
    val index = myLocationToIndex(point)
    if (index === -1) {
        log.warn("Could not determine index for point: {}", point)
        return null
    }
    return Pair(index, model.getElementAt(index))
}

