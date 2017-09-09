package at.cpickl.gadsu.view

import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.service.MetaInf
import at.cpickl.gadsu.service.OpenWebpageEvent
import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.version.Version
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.enableSmallWindowStyle
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.joda.time.DateTime
import java.awt.BorderLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

fun main(args: Array<String>) {
    AboutWindow(MetaInf(Version.DUMMY, DateTime.now()), null, EventBus()).isVisible = true
}

class ShowAboutDialogEvent : UserEvent() {}

class AboutModule : AbstractModule() {
    override fun configure() {
        bind(AboutController::class.java).asEagerSingleton()
        bind(AboutWindow::class.java).`in`(Scopes.SINGLETON)
    }
}


@Logged
open class AboutController @Inject constructor(
        private val window: AboutWindow
) {

    @Subscribe open fun onShowAboutDialogEvent(@Suppress("UNUSED_PARAMETER") event: ShowAboutDialogEvent) {
        window.isVisible = true
    }

    @Subscribe open fun onQuitEvent(@Suppress("UNUSED_PARAMETER") event: QuitEvent) {
        window.isVisible = false
        window.dispose()
    }
}


class AboutWindow @Inject constructor(
        metaInf: MetaInf,
        mainFrame: MainFrame?,
        bus: EventBus
) : JFrame() {

    private val HGAP_WINDOW = 35

    init {
        title = ""
        rootPane.enableSmallWindowStyle()
        isAlwaysOnTop = true

        val panel = GridPanel()

        panel.border = BorderFactory.createEmptyBorder(10, HGAP_WINDOW, 10, HGAP_WINDOW)
        panel.c.anchor = GridBagConstraints.CENTER
        panel.c.insets = Insets(0, 0, 10, 0)
        panel.add(JLabel(ImageIcon(javaClass.getResource("/gadsu/logo100.png"))))

        panel.c.gridy++
        val title = JLabel("Gadsu")
        title.font = title.font.deriveFont(17.0F).deriveFont(Font.BOLD)
        panel.add(title)

        panel.c.gridy++
        val aboutText = HtmlEditorPane()
        panel.c.weightx = 1.0
        panel.c.fill = GridBagConstraints.HORIZONTAL
        aboutText.changeLabelFontSize(10.0F)
        aboutText.text =
                "<div style='text-align:center;'>" +
                "Version ${metaInf.applicationVersion.toLabel()}<br>" +
                "(${metaInf.built.formatDateTime()})<br>" +
                "by Christoph Pickl<br>" +
                "<br>" +
                """Visit the <a href="https://github.com/christophpickl/gadsu">Website</a>"""
        aboutText.addOnUrlClickListener { bus.post(OpenWebpageEvent(it)) }
        panel.add(aboutText)

        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)
        pack()
        isResizable = false
        setLocationRelativeTo(mainFrame?.asJFrame())
    }
}
