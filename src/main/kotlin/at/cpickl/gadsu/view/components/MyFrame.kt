package at.cpickl.gadsu.view.components

import java.awt.Point
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.WindowConstants


open class MyFrame(private val myTitle: String) : JFrame() {
    companion object {
        val BORDER_GAP = BorderFactory.createEmptyBorder(10, 15, 10, 15)
    }

    init {
        title = myTitle
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    final val dockPositionRight: Point
        get() = Point(location.x + size.width + 10, location.y)

    //    protected fun packAndShow(locationRelativeTo: Component? = null) {
    //        pack()
    //        setLocationRelativeTo(locationRelativeTo)
    //        setVisible(true)
    //    }

    protected fun hideAndClose() {
        isVisible = false
        dispose()
    }

}
