package at.cpickl.gadsu.image

import at.cpickl.gadsu.view.components.GridPanel
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory
import java.io.File
import javax.swing.JButton
import javax.swing.JComponent


interface ImagePicker {
    companion object {
        val VIEWNAME_SUFFIX_PANEL: String get() = "Panel"
        val VIEWNAME_SUFFIX_OPENBUTTON: String get() = "OpenButton"
    }

    fun asComponent(): JComponent
}

interface ImagePickerFactory {
    fun create(viewNamePrefix: String, parentFolder: File): ImagePicker
}

class SwingImagePicker @Inject constructor (
        private val bus: EventBus,
        @Assisted private val viewNamePrefix: String,
        @Assisted private val parentFolder: File
) : GridPanel(), ImagePicker {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_PANEL}"

        val btnOpen = JButton("Bild w\u00e4hlen")
        btnOpen.name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_OPENBUTTON}"
        btnOpen.addActionListener { SelectImageEvent() }

        add(btnOpen)
    }


    override fun asComponent() = this

}
