package at.cpickl.gadsu.view.components

import org.slf4j.LoggerFactory
import java.awt.Point
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.WindowConstants


open class MyFrame(private val myTitle: String) : JFrame() {
    companion object {
        val BORDER_GAP = BorderFactory.createEmptyBorder(10, 15, 10, 15)
    }

    private val log = LoggerFactory.getLogger(javaClass)



    init {
        title = myTitle
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    final val dockPositionRight: Point
        get() = Point(location.x + size.width + 10, location.y)

    fun addCloseListener(body: () -> Unit) {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                log.trace("windowClosing(event)")
                body()
            }
        })
    }

    //    protected fun packAndShow(locationRelativeTo: Component? = null) {
    //        pack()
    //        setLocationRelativeTo(locationRelativeTo)
    //        setVisible(true)
    //    }

    protected fun hideAndClose() {
        setVisible(false)
        dispose()
    }

}

fun JFrame.packCenterAndShow() {
    pack()
    setLocationRelativeTo(null)
    setVisible(true)
}
