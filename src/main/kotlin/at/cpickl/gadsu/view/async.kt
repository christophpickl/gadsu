package at.cpickl.gadsu.view

import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.emptyBorderForDialogs
import at.cpickl.gadsu.view.swing.enableSmallWindowStyle
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.*


fun main(args: Array<String>) {
    AsyncDialog(AsyncDialogSettings("Wait for it", "Wait for something long to be done."), JFrame()).isVisible = true
}

interface AsyncWorker {
    fun <T> doInBackground(settings: AsyncDialogSettings?, backgroundTask: () -> T, doneTask: (T?) -> Unit, exceptionTask: (Exception) -> Unit)

}

data class AsyncDialogSettings(
        val title: String,
        val message: String,
        val parentFrame: JFrame? = null
)


class AsyncSwingWorker : AsyncWorker {

    override fun <T> doInBackground(settings: AsyncDialogSettings?, backgroundTask: () -> T, doneTask: (T?) -> Unit, exceptionTask: (Exception) -> Unit) {
        val dialog = if (settings == null) {
            null
        } else {
            AsyncDialog(settings, settings.parentFrame ?: currentActiveJFrame())
                    .apply { SwingUtilities.invokeLater { this.isVisible = true } }
        }
        val closeDialog = {
            dialog?.isVisible = false
            dialog?.dispose()
        }
        KotlinSwingWorker.executeAsync(backgroundTask, { result ->
            closeDialog()
            SwingUtilities.invokeLater { doneTask(result) }
        }, { e ->
            closeDialog()
            exceptionTask(e)
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
        }

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)
        pack()
        setLocationRelativeTo(owner)
        isResizable = false
    }
}

object KotlinSwingWorker {
    fun <T> executeAsync(backgroundTask: () -> T, doneTask: (T?) -> Unit, exceptionTask: (Exception) -> Unit) {
        val worker = object : SwingWorker<T?, Void>() {

            private var thrownException: Exception? = null

            override fun doInBackground(): T? {
                try {
                    return backgroundTask()
                } catch(e: Exception) {
                    thrownException = e
                    return null
                }
            }

            override fun done() {
                if (thrownException == null) {
                    doneTask(get())
                } else {
                    exceptionTask(thrownException!!)
                }
            }
        }
        worker.execute()
    }
}
