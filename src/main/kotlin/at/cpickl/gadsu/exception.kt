package at.cpickl.gadsu

import at.cpickl.gadsu.persistence.DatabaseLockedException
import at.cpickl.gadsu.service.GADSU_LOG_FILE
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.service.SwingWebPageOpener
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.enforceSize
import at.cpickl.gadsu.view.swing.weightxy
import com.google.common.eventbus.Subscribe
import com.google.inject.CreationException
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.UIManager
import javax.swing.WindowConstants


fun main(args: Array<String>) {
    PanicDialog(Exception(), null, {}).showIt()
}


open class GadsuException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

object GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    var mainFrame: JFrame? = null

    fun register() {
        log.debug("Registering global exception handler.")

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.error("Uncaught exception in thread '${thread.name}'!")
            showDialogAndDie(throwable)
        }
    }

    fun showDialogAndDie(throwable: Throwable) {
        log.error("Uncaught exception, going to die!", throwable)
        PanicDialog(throwable, mainFrame, { System.exit(1) }).showIt()
    }

    fun exceptionSafe(action: () -> Unit) {
        try {
            action.invoke()
        } catch(e: Exception) {
            GlobalExceptionHandler.showDialogAndDie(e)
        }
    }


    fun startThread(action: () -> Unit) {
        startThread(name = null, action = action)
    }

    fun startThread(name: String?, action: () -> Unit) {
        val runnable = Runnable {
            log.trace("Started new thread.")
            exceptionSafe(action)
        }
        val thread = if (name == null) Thread(runnable) else Thread(runnable, name)
        thread.start()
    }

}

class PanicDialog(throwable: Throwable, _owner: JFrame?, onClose: () -> Unit) : JDialog(_owner, true) {

    private val log = LOG(javaClass)

    init {
        title = "Unerwarteter Fehler!"
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addCloseListener { dispose(); onClose() }
        val content = GridPanel()

        with (content.c) {

            weightxy(0.0)
            fill = GridBagConstraints.NONE
            insets = Pad.right(20)
            anchor = GridBagConstraints.NORTH
            content.add(JLabel(UIManager.getIcon("OptionPane.errorIcon")))

            gridx++
            weightxy(1.0)
            fill = GridBagConstraints.BOTH
            insets = Pad.bottom(10)
            val message = HtmlEditorPane("""${buildMessage(throwable)}<br>
                <br>
                Kontaktiere am besten doch gleich bitte Christoph und erstelle einen Fehlerbericht:<br/>
                <a href="https://github.com/christophpickl/gadsu/issues/new">https://github.com/christophpickl/gadsu/issues/new</a><br/>
                <br/>
                Am besten fügst du auch gleich die Programmlogs hinzu. Du findest sie unter:<br/>
                <tt>${GADSU_LOG_FILE.absolutePath}</tt>
            """)
            message.enforceSize(400, 200)
            message.addOnUrlClickListener { SwingWebPageOpener().silentlyTryToOpen(it) }
            content.add(message)

            gridx = 0
            gridy++
            gridwidth = 2
            weightxy(0.0)
            fill = GridBagConstraints.NONE
            insets = Pad.ZERO
            anchor = GridBagConstraints.EAST
            val btnQuit = JButton("Programm beenden")
            btnQuit.addActionListener { dispose(); onClose() }
            rootPane.defaultButton = btnQuit
            content.add(btnQuit)
        }

        content.border = BorderFactory.createEmptyBorder(15, 15, 10, 15)
        contentPane.layout = BorderLayout()
        contentPane.add(content, BorderLayout.CENTER)
        isResizable = false
        pack()
        setLocationRelativeTo(null)
    }

    fun showIt() {
        isVisible = true
    }

    private fun buildMessage(throwable: Throwable): String {
        val prefix = "Das tut mir jetzt aber wirklich, wirklich sehr leid, aber "
        var suffix = "ein unerwarteter Fehler ist aufgetreten"
        if (throwable is CreationException) {
            log.error("exception was thrown during startup while guice was created")

            if (throwable.cause is DatabaseLockedException) {
                suffix = "es scheint als wäre die Datenbank bereits in Verwendung"
            }
        }
        return prefix + suffix + "."
    }
}


class AllMightyEventCatcher {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onEvent(event: Any) {
        log.trace("Event has been dispatched on EventBus: {}", event)
    }

    // EITHER - OR

    //    @Subscribe fun onDeadEvent(event: DeadEvent) {
    //        throw GadsuException("Event (${event.event}) was not handled by anyone! (source: ${event.source})")
    //    }

}

