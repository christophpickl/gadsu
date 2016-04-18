package at.cpickl.gadsu.image

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.SwingFactory
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.google.inject.assistedinject.Assisted
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTextField
import javax.swing.filechooser.FileNameExtensionFilter

interface ImagePicker {
    fun asComponent(): Component
}

class SwingImagePicker @Inject constructor (
        private val bus: EventBus,
        private val swing: SwingFactory,
        @Assisted private val viewNamePrefix: String
) : GridPanel(), ImagePicker {

    companion object {
        private val VIEWNAME_SUFFIX_PANEL = ".Panel"
        private val VIEWNAME_SUFFIX_PATH = ".PathTextBox"
        private val VIEWNAME_SUFFIX_OPENBUTTON = ".OpenButton"
    }
    private val log = LoggerFactory.getLogger(javaClass)

    private val txtPath = JTextField()

    init {
        name = "$viewNamePrefix.${VIEWNAME_SUFFIX_PANEL}"
        txtPath.name = "$viewNamePrefix.${VIEWNAME_SUFFIX_PATH}"

        txtPath.isEditable = false

        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtPath)

        c.gridx++
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        val btnOpen = JButton(Labels.Buttons.OpenFile)
        btnOpen.name = "$viewNamePrefix.${VIEWNAME_SUFFIX_OPENBUTTON}"
        btnOpen.addActionListener { onOpenFile() }
        add(btnOpen)
    }

    private fun onOpenFile() {
        log.debug("onOpenFile()")

        val chooser = JFileChooser()
        chooser.dialogTitle = "Bild ausw\u00e4hlen ..."
        chooser.fileFilter = FileNameExtensionFilter("Bilddateien (*.jpg)", "jpg")

        val result = chooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = chooser.selectedFile

            if (!selectedFile.name.toLowerCase().endsWith("jpg")) {
                log.debug("Ignoring invalid file (this patches the JFileChooser default behaviour of allowing *.* files although extension filter is set)")
                return
            }

            onFileSelected(selectedFile)
        } else {
            log.debug("Choosing an image aborted by user.")
        }
    }

    private fun onFileSelected(file: File) {
        log.debug("File selected '{}'. Going to read as image.", file.absolutePath)
        val image: BufferedImage
        try {
            // we could do this in some background thread, but nah, its fast anyway ;)
            image = ImageIO.read(file)
        } catch(e: IOException) {
            throw GadsuException("Failed to read image from: '${file.absolutePath}'!", e) // TODO show error dialog instead
        }

        val imageIcon = ImageIcon(image);
        bus.post(ImageSelectedEvent(viewNamePrefix, imageIcon))
    }

    override fun asComponent() = this

}
