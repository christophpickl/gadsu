package at.cpickl.gadsu.image

import at.cpickl.gadsu.view.components.panels.GridPanel
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
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
    fun create(viewNamePrefix: String): ImagePicker
}

class SwingImagePicker @Inject constructor (
        @Assisted private val viewNamePrefix: String
) : GridPanel(), ImagePicker {

    init {
        name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_PANEL}"

        val btnOpen = JButton("Bild w\u00e4hlen")
        btnOpen.name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_OPENBUTTON}"
        btnOpen.addActionListener { SelectImageEvent() }

        add(btnOpen)
    }


    override fun asComponent() = this

}
