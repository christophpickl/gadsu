package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.net.URL


class OpenWebpageEvent(val url: URL) : UserEvent()

interface WebPageOpener {
    val isSupported: Boolean
    fun open(url: URL)
}

class SwingWebPageOpener : WebPageOpener {
    private val log = LoggerFactory.getLogger(javaClass)

    private var _isSupported: Boolean? = null
    private var desktop: Desktop? = null
    override val isSupported: Boolean
        get() {
            if (_isSupported == null) {
                desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                _isSupported = desktop != null && desktop!!.isSupported(Desktop.Action.BROWSE)
            }
            return _isSupported!!
        }

    override fun open(url: URL) {
        log.info("open(url='{}')", url)
        if (isSupported == false) {
            throw GadsuException("Opening links is not supported on your machine!")
        }
        desktop!!.browse(url.toURI())
    }

    @Subscribe fun onOpenWebpageEvent(event: OpenWebpageEvent) {
        log.debug("onOpenWebpageEvent(event)")
        if (isSupported == false) {
            Dialogs(null).show(
                    title = "Ups",
                    message = "Dein System unterst\u00fctzt nicht das \u00d6ffnen von Links!",
                    buttonLabels = arrayOf("Was solls"),
                    type = DialogType.WARN
            )
            return
        }
        open(event.url)
    }

}
