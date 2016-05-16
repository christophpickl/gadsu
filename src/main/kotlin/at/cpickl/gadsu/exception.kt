package at.cpickl.gadsu

import at.cpickl.gadsu.service.GADSU_LOG_FILE
import at.cpickl.gadsu.service.SwingWebPageOpener
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.addCloseListener
import at.cpickl.gadsu.view.swing.weightxy
import com.google.common.eventbus.Subscribe
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
    PanicDialog(null, {}).showIt()
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
        PanicDialog(mainFrame, { System.exit(1) }).showIt()
    }

    fun exceptionSafe(action: () -> Unit) {
        try {
            action.invoke()
        } catch(e: Exception) {
            GlobalExceptionHandler.showDialogAndDie(e)
        }
    }


    fun startThread(action: () -> Unit) {
        Thread(Runnable {
            log.trace("Started new thread.")
            exceptionSafe(action)
        }).start()
    }

}

class PanicDialog(_owner: JFrame?, onClose: () -> Unit) : JDialog(_owner, true) {
    init {
        title = "Ujeeeee!"
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        addCloseListener { dispose(); onClose() }
        val content = GridPanel()

        with (content.c) {

            gridheight = 2
            insets = Pad.right(20)
            anchor = GridBagConstraints.NORTH
            fill = GridBagConstraints.NONE
            weightxy(0.0)
            content.add(JLabel(UIManager.getIcon("OptionPane.errorIcon")))

            gridx++
            weightxy(1.0)
            fill = GridBagConstraints.BOTH
            gridheight = 1
            insets = Pad.bottom(10)
            val message = HtmlEditorPane("""Das tut mir jetzt aber wirklich, wirklich sehr leid, aber ein unerwarteter Fehler ist aufgetreten.<br>
                <br>
                Kontaktiere am besten doch gleich bitte Christoph und erstelle einen Fehlerbericht:<br/>
                <a href="https://github.com/christophpickl/gadsu/issues/new">https://github.com/christophpickl/gadsu/issues/new</a><br/>
                <br/>
                Am besten fügst du auch gleich die Programmlogs hinzu. Du findest sie unter:<br/>
                <tt>${GADSU_LOG_FILE.absolutePath}</tt>
            """)
            message.addOnUrlClickListener { SwingWebPageOpener().silentlyTryToOpen(it) }
            content.add(message)

            gridy++
            gridwidth = 2
            weightxy(0.0)
            fill = GridBagConstraints.NONE
            insets = Pad.ZERO
            anchor = GridBagConstraints.EAST
            val btnQuit = JButton("Programm schlie\u00dfen")
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

