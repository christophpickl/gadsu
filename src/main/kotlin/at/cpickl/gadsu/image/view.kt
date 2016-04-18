package at.cpickl.gadsu.image

import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.components.GridPanel
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory
import java.awt.Component
import java.io.File
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


interface ImagePicker {
    companion object {
        val VIEWNAME_SUFFIX_PANEL: String get() = "Panel"
        val VIEWNAME_SUFFIX_OPENBUTTON: String get() = "OpenButton"
    }

    fun asComponent(): Component
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

        val btnOpen = JButton(Labels.Buttons.OpenFile)
        btnOpen.name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_OPENBUTTON}"
        btnOpen.addActionListener { onOpenFile() }

        add(btnOpen)
    }

    private fun onOpenFile() {
        log.debug("onOpenFile()")

        val chooser = JFileChooser()
        chooser.currentDirectory = parentFolder
        chooser.dialogTitle = "Bild ausw\u00e4hlen ..."
        chooser.fileFilter = FileNameExtensionFilter("Bilddateien (*.jpg)", "jpg")

        val result = chooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = chooser.selectedFile

            if (!selectedFile.name.toLowerCase().endsWith("jpg")) {
                log.debug("Ignoring invalid file (this patches the JFileChooser default behaviour of allowing *.* files although extension filter is set)")
                return
            }

            bus.post(ImageSelectedEvent(viewNamePrefix, selectedFile))

        } else {
            log.debug("Choosing an image aborted by user.")
        }
    }


    override fun asComponent() = this

}
