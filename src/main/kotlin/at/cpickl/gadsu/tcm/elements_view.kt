package at.cpickl.gadsu.tcm

import at.cpickl.gadsu.Event
import at.cpickl.gadsu.view.brighterIfTrue
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.addSingleLeftClickListener
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.HashMap
import java.util.LinkedHashMap
import javax.swing.JPanel

fun main(args: Array<String>) {
    val bus = EventBus()
    bus.register(object: Any() {
        @Subscribe fun onAny(event: Any) {
            println("====> Event dispatched: $event")
        }
    })
    Framed.show(ElementsStarView(bus), Dimension(450, 420))
}


abstract class ElementsEvent : Event()
class ElementsOverEvent(val element: Element) : ElementsEvent() {
    override fun toString() = "ElementsOverEvent(element=$element)"
}
class ElementsOutEvent(val element: Element) : ElementsEvent() {
    override fun toString() = "ElementsOutEvent(element=$element)"
}
class ElementClickedEvent(val element: Element) : ElementsEvent() {
    override fun toString() = "ElementClickedEvent(element=$element)"
}

fun <K, V> Iterable<Pair<K, V>>.toMutableMap(): HashMap<K, V> {
    val immutableMap = toMap()
    val map = HashMap<K, V>(immutableMap.size)
    map.putAll(immutableMap)
    return map
}

class ElementsStarView(
        val bus: EventBus,
        // MINOR FUTURE LUXURY pass values for each element, indicating their size from -1.0, 0.0 (normal) up to +1.0 (max size)
        val CIRCLE_DIAMETER: Int = 140
) : JPanel() {

    private val log = LoggerFactory.getLogger(javaClass)

    val FRAME_PADDING = 10
    val CIRCLE_RADIUS = CIRCLE_DIAMETER / 2
    val CIRCLE_PADDING = FRAME_PADDING + CIRCLE_RADIUS

    private var coordinates = calcCoordinates()
    private var overs: HashMap<Element, Boolean> = Element.values().map { Pair(it, false) }.toMutableMap()

    init {
        isOpaque = true
        addSingleLeftClickListener({ onMouseClicked(it) })
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                onMouseMoved(e.point)
            }
        })
    }

    override fun paintComponent(rawGraphics: Graphics) {
//        println("paintComponent() size=$size")
        super.paintComponent(rawGraphics)

        val g = rawGraphics as Graphics2D

        coordinates = calcCoordinates()

        drawBackground(g)
        drawLines(g)
        drawCircle(g)
    }

    private fun drawBackground(g: Graphics2D) {
        g.color = Color.LIGHT_GRAY
        g.fillRect(FRAME_PADDING, FRAME_PADDING, width - (FRAME_PADDING *2), height - (FRAME_PADDING *2))
    }

    private fun onMouseClicked(point: Point) {
        log.trace("onMouseClicked(point={})", point)
        val overElement = overs.filterValues { it == true }.keys.firstOrNull() ?: return
        bus.post(ElementClickedEvent(overElement))
    }

    private fun onMouseMoved(point: Point) {
//        println("onMouseMoved(point=$point)")
        val g = graphics as Graphics2D
        val mouseArea = Rectangle(point.x, point.y, 1, 1)

        var repaintRequired = false
        coordinates.coordinateByElement.forEach { element, coordinate ->
            if (g.hit(coordinate.hitArea, mouseArea, false)) {
                if (overs.get(element) == false) {
//                    println("hit on [$element] at $point")
                    overs.put(element, true)
                    repaintRequired = true
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    bus.post(ElementsOverEvent(element))
                }
            } else {
                if (overs.get(element) == true) {
                    overs.put(element, false)
                    repaintRequired = true
                    cursor = Cursor.getDefaultCursor()
                    bus.post(ElementsOutEvent(element))
                }
            }
        }
        if (repaintRequired) {
            repaint()
        }
    }

    private fun drawLines(g: Graphics2D) {
        g.color = Color.BLACK
        g.stroke = BasicStroke(2.0F)

        coordinates.coordinatesArray.forEachIndexed { i, pair ->
            val point = pair.second.center
            val nextElementIndex = if (i == 4) 0 else i + 1
            val nextPoint = coordinates.coordinatesArray[nextElementIndex].second.center
            g.drawLine(point.x, point.y, nextPoint.x, nextPoint.y)
        }
    }

    private fun drawCircle(g: Graphics2D) {
        coordinates.coordinateByElement.forEach {
            g.fillCircleAt(it.value.center, CIRCLE_DIAMETER, it.key.color.brighterIfTrue(overs.get(it.key)!!))
        }
    }

    private fun calcCoordinates(): ElementsCoordinates {
        val verticalGap = (height - CIRCLE_PADDING * 2) / 2
        val horizontalGap = (width / 2 - CIRCLE_PADDING) / 2

        val map = LinkedHashMap<Element, Coordinate>(5)
        map.put(Element.Wood, ensuredPoint(CIRCLE_PADDING, verticalGap))
        map.put(Element.Fire, ensuredPoint(width / 2, CIRCLE_PADDING))
        map.put(Element.Earth, ensuredPoint(width - CIRCLE_PADDING, verticalGap))
        map.put(Element.Metal, ensuredPoint(width - CIRCLE_PADDING - horizontalGap, height - CIRCLE_PADDING))
        map.put(Element.Water, ensuredPoint(    0 + CIRCLE_PADDING + horizontalGap, height - CIRCLE_PADDING))
        return ElementsCoordinates(map)
    }

    private fun ensuredPoint(x: Int, y: Int):Coordinate {
        val center = Point(
                Math.min(Math.max(x, CIRCLE_PADDING), width - CIRCLE_PADDING),
                Math.min(Math.max(y, CIRCLE_PADDING), height - CIRCLE_PADDING)
        )
        val hitArea = Rectangle(center.x - CIRCLE_RADIUS, center.y - CIRCLE_RADIUS, CIRCLE_DIAMETER, CIRCLE_DIAMETER)
        return Coordinate(center, hitArea)
    }
}

data class Coordinate(val center: Point, val hitArea: Rectangle)

private data class ElementsCoordinates(
        val coordinateByElement: LinkedHashMap<Element, Coordinate>
) {
    val coordinatesArray: Array<Pair<Element, Coordinate>> = coordinateByElement.map { Pair(it.key, it.value) }.toTypedArray()
}






/**
 * Given point location will be the center of the drawed circle (java defaults to top left corner).
 */
fun Graphics2D.fillCircleAt(point: Point, diameter: Int, color: Color? = null) {
    if (color != null) {
        this.color = color
    }
    val radius = diameter / 2.0
    val x = point.x - radius
    val y = point.y - radius
    fillOval(x.toInt(), y.toInt(), diameter, diameter)
}


