package at.cpickl.gadsu.view.components

import java.awt.Point
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.WindowConstants

open class MyDialog(owner: JFrame, myTitle: String) : JDialog(owner, myTitle, true) {
    init {
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    protected fun hideAndClose() {
        isVisible = false
        dispose()
    }
}

open class MyFrame(private val myTitle: String) : JFrame() {
    companion object {
        val BORDER_GAP = BorderFactory.createEmptyBorder(10, 15, 10, 15)
        private val FRAME_ICON = ImageIcon(MyFrame::class.java.getResource("/gadsu/logo100.png")).image
    }

    init {
        title = myTitle
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        iconImage = FRAME_ICON
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
