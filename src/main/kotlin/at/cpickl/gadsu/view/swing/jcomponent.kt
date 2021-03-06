package at.cpickl.gadsu.view.swing

import at.cpickl.gadsu.global.SHORTCUT_MODIFIER
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.ViewNames
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.KeyboardFocusManager
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Timer
import java.util.TimerTask
import javax.swing.AbstractAction
import javax.swing.ActionMap
import javax.swing.BorderFactory
import javax.swing.InputMap
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.KeyStroke

class MyKeyListener(
        val keyStrokes: List<KeyStroke>,
        val actionCommand: String,
        val onTriggered: (e: ActionEvent) -> Unit
) {
    companion object {
        fun onEscape(actionCommand: String, onTriggered: (e: ActionEvent) -> Unit) =
                MyKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), actionCommand, onTriggered)
        fun onShortcutW(actionCommand: String, onTriggered: (e: ActionEvent) -> Unit) =
                MyKeyListener(KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MODIFIER), actionCommand, onTriggered)
        fun onEscapeOrShortcutW(actionCommand: String, onTriggered: (e: ActionEvent) -> Unit) =
                MyKeyListener(listOf(
                        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        KeyStroke.getKeyStroke(KeyEvent.VK_W, SHORTCUT_MODIFIER)
                ), actionCommand, onTriggered)
    }

    constructor(keyStroke: KeyStroke, actionCommand: String, onTriggered: (e: ActionEvent) -> Unit) : this(listOf(keyStroke), actionCommand, onTriggered)

    override fun toString(): String {
        return "MyKeyListener(actionCommand='$actionCommand', keyStrokes=$keyStrokes)"
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
        listener.keyStrokes.forEach {
            inputMap.put(it, listener.actionCommand)
        }
        actionMap.put(listener.actionCommand, object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                listener.onTriggered.invoke(e)
            }
        })
    }

    override fun deregisterYourself() {
        log.trace("deregisterYourself() ... listener={}", listener)
        listener.keyStrokes.forEach {
            inputMap.remove(it)
        }
        actionMap.remove(listener.actionCommand)
    }
}

fun JComponent.registerMyKeyListener(listener: MyKeyListener): RegisteredKeyListener =
        RegisteredKeyListenerImpl(listener, getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW), actionMap).apply {
            registerYourself()
        }

/**
 * @param hPolicy e.g.: ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS
 * @param vPolicy e.g.: ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
 */
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

fun <T : JComponent> T.enforceHeight(myHeight: Int): T {
    size = Dimension(size.width, myHeight)
    maximumSize = Dimension(maximumSize.width, myHeight)
    minimumSize = Dimension(minimumSize.width, myHeight)
    preferredSize = Dimension(preferredSize.width, myHeight)
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
    get() {
        return !isOpaque
    }
    set(value) {
        isOpaque = !value
    }

fun <T : JComponent> T.noBorder(): T {
    border = BorderFactory.createEmptyBorder()
    return this
}
fun <T : JComponent> T.transparent(): T {
    isTransparent = true
    return this
}

fun <T : JComponent> T.opaque(): T {
    isOpaque = true
    return this
}

fun JComponent.emptyBorderForDialogs() {
    border = BorderFactory.createEmptyBorder(10, 15, 10, 15)
}

fun <J : JComponent> J.focusTraversalWithTabs() = apply {
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null)
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null)
}

fun <J : JComponent> J.viewName(withNames: ViewNames.() -> String) = apply {
    name = withNames(ViewNames)
}
