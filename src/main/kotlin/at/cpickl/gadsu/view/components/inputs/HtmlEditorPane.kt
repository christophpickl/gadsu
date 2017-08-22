package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.view.swing.transparent
import java.net.URL
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.event.HyperlinkEvent


class HtmlEditorPane(initText: String = "") : JEditorPane() {
    init {
        text = initText
        contentType = "text/html"
        putClientProperty(HONOR_DISPLAY_PROPERTIES, true)
        isEditable = false
        transparent()
        // isEnabled = false // dont do this!
    }

    fun addOnUrlClickListener(listener: (URL) -> Unit) {
        addHyperlinkListener { e ->
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.eventType)) {
                listener(e.url)
            }
        }
    }

    fun changeLabelFontSize(size: Float): HtmlEditorPane {
        font = JLabel().font.deriveFont(size)
        return this
    }

}
