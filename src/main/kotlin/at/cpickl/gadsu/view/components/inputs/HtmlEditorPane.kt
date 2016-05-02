package at.cpickl.gadsu.view.components.inputs

import java.net.URL
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.event.HyperlinkEvent


class HtmlEditorPane : JEditorPane() {
    init {
        contentType = "text/html"
        putClientProperty(HONOR_DISPLAY_PROPERTIES, true);
        isEditable = false
        isOpaque = false
        // isEnabled = false // dont do this!
    }

    fun addOnUrlClickListener(listener: (URL) -> Unit) {
        addHyperlinkListener { e ->
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                listener(e.url)
            }
        }
    }

    fun changeLabelFont(size: Float) {
        font = JLabel().font.deriveFont(size)
    }

}
