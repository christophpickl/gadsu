package kotlin_playground

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    SwingUtilities.invokeLater { showFrame() }
}

private fun showFrame() {
    val frame = JFrame()
    val panel = JFXPanel()
    frame.add(panel)
    frame.setSize(300, 200)
    frame.isVisible = true
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    Platform.runLater {
        val root = Group()
        val scene = Scene(root, Color.ALICEBLUE)
        Text().apply {
            x = 40.0
            y = 100.0
            font = Font(25.0)
            text = "Hi ho duda"
            root.children.add(this)
        }

        panel.scene = scene
    }
}
