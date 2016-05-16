package at.cpickl.gadsu.view

import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.enableSmallWindowStyle
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JProgressBar
import javax.swing.SwingUtilities
import javax.swing.SwingWorker
import javax.swing.WindowConstants


fun main(args: Array<String>) {
    AsyncDialog(AsyncDialogSettings("Wait for it", "Wait for something long to be done."), JFrame()).isVisible = true
}

interface AsyncWorker {
    fun <T> doInBackground(settings: AsyncDialogSettings?, backgroundTask: () -> T, doneTask: (T) -> Unit)

}

data class AsyncDialogSettings(val title: String, val message: String)


class AsyncSwingWorker : AsyncWorker {

    override fun <T> doInBackground(settings: AsyncDialogSettings?, backgroundTask: () -> T, doneTask: (T) -> Unit) {

        val dialog = if (settings == null) {
            null
        } else {
            AsyncDialog(settings, currentActiveJFrame()).apply { SwingUtilities.invokeLater { this.isVisible = true } }
        }

        KotlinSwingWorker.executeAsync(backgroundTask, { result ->
            dialog?.isVisible = false
            dialog?.dispose()
            SwingUtilities.invokeLater { doneTask(result) }
        })
    }


}
class AsyncDialog(settings: AsyncDialogSettings, owner: JFrame?) : JDialog(owner, if (owner != null) true else false) {
    init {
        title = settings.title
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        rootPane.enableSmallWindowStyle()

        val panel = GridPanel()
        panel.emptyBorderForDialogs()
        val progress = JProgressBar()
        progress.isIndeterminate = true

        with (panel.c) {
            fill = GridBagConstraints.BOTH
            weightx = 1.0
            weighty = 1.0
            panel.add(JLabel(settings.message))

            gridy++
            weighty = 0.0
            insets = Pad.top(10)
            panel.add(progress)

            // MINOR UI cancel button for async dialog
        }

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)
        isResizable = false
    }
}

object KotlinSwingWorker {
    fun <T> executeAsync(backgroundTask: () -> T, doneTask: (T) -> Unit) {
        val worker = object : SwingWorker<T, Void>() {
            override fun doInBackground(): T {
                return backgroundTask()
            }
            override fun done() {
                doneTask(get())
            }
        }
        worker.execute()
    }
}