package at.cpickl.gadsu.view.swing

import at.cpickl.gadsu.service.LOG
import java.awt.*
import java.awt.event.*
import java.util.*
import java.util.Timer
import javax.swing.*

class MyKeyListener(
        val actionCommand: String,
        val keyStroke: KeyStroke,
        val onTriggered: (e: ActionEvent) -> Unit
) {
    companion object {
        fun onEscape(actionCommand: String, onTriggered: (e: ActionEvent) -> Unit) = MyKeyListener(actionCommand, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), onTriggered)
    }

    override fun toString(): String{
        return "MyKeyListener(actionCommand='$actionCommand', keyStroke=$keyStroke)"
    }

}

interface RegisteredKeyListener {
    fun deregisterYourself()
}

class RegisteredKeyListenerImpl(
        private val listener: MyKeyListener, private val inputMap: InputMap, private val actionMap: ActionMap
) : RegisteredKeyListener {

    private val log = LOG(javaClass)
    fun registerYourself() {
        log.trace("registerYourself() ... listener={}", listener)
        inputMap.put(listener.keyStroke, listener.actionCommand)
        actionMap.put(listener.actionCommand, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                listener.onTriggered.invoke(e)
            }
        })
    }

    override fun deregisterYourself() {
        log.trace("deregisterYourself() ... listener={}", listener)
        inputMap.remove(listener.keyStroke)
        actionMap.remove(listener.actionCommand)
    }
}

fun JComponent.registerMyKeyListener(listener: MyKeyListener): RegisteredKeyListener =
        RegisteredKeyListenerImpl(listener, getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), actionMap).apply {
    registerYourself()
}

fun JComponent.scrolled(hPolicy: Int? = null, vPolicy: Int? = null): JScrollPane {
    return JScrollPane(this).apply {
        if (hPolicy != null) horizontalScrollBarPolicy = hPolicy
        if (vPolicy != null) verticalScrollBarPolicy = vPolicy
    }
}


fun <T : JComponent> T.bold(): T {
    font = font.deriveFont(Font.BOLD)
    return this
}

fun <T : JComponent> T.italic(): T {
    font = font.deriveFont(Font.ITALIC)
    return this
}


fun JComponent.changeBackgroundForASec(tempBackground: Color) {
    val originalBackground = background
    background = tempBackground

    Timer("bg-color-blinking", true).schedule(object : TimerTask() {
        override fun run() {
            background = originalBackground
        }
    }, 1000L)
}


// MINOR @UI - this is some kind of a hacky method (height calculation does not work properly!)
fun <T : JComponent> T.enforceWidth(myWidth: Int): T {
    size = Dimension(myWidth, size.height)
    maximumSize = Dimension(myWidth, maximumSize.height)
    minimumSize = Dimension(myWidth, minimumSize.height)
    preferredSize = Dimension(myWidth, preferredSize.height)
    return this
}
fun <T : JComponent> T.enforceSize(myWidth: Int, myHeight: Int): T {
    size = Dimension(myWidth, size.height)
    maximumSize = Dimension(myWidth, myHeight)
    minimumSize = Dimension(myWidth, myHeight)
    preferredSize = Dimension(myWidth, myHeight)
    return this
}


fun Component.addSingleLeftClickListener(function: (Point) -> Unit) {
    addMouseListener(object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            val isLeftClick = (e.modifiers and InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK
            if (e.clickCount == 1 && isLeftClick) {
                function.invoke(e.point)
            }
        }
    })
}

var JComponent.isTransparent: Boolean
    get() { return !isOpaque }
    set(value) { isOpaque = !value }


fun <T : JComponent> T.transparent(): T {
    isTransparent = true
    return this
}

fun <T : JComponent> T.opaque(): T {
    isOpaque = true
    return this
}
