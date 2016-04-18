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
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

private val LOG_image = LoggerFactory.getLogger("at.cpickl.gadsu.image")

interface ImagePicker {
    companion object {
        val VIEWNAME_SUFFIX_PANEL: String get() = "Panel"
//        val VIEWNAME_SUFFIX_PATH: String get() = "PathTextBox"
        val VIEWNAME_SUFFIX_OPENBUTTON: String get() = "OpenButton"
    }


    fun asComponent(): Component

}

class SwingImagePicker @Inject constructor (
        private val bus: EventBus,
        private val swing: SwingFactory,
        @Assisted private val viewNamePrefix: String
) : GridPanel(), ImagePicker {

    private val log = LoggerFactory.getLogger(javaClass)

//    private val txtPath = JTextField()

    init {
        name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_PANEL}"
//        txtPath.name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_PATH}"
//        txtPath.isEditable = false

//        c.weightx = 1.0
//        c.fill = GridBagConstraints.HORIZONTAL
//        add(txtPath)

//        c.weightx = 0.0
//        c.fill = GridBagConstraints.NONE

        val btnOpen = JButton(Labels.Buttons.OpenFile)
        btnOpen.name = "$viewNamePrefix.${ImagePicker.VIEWNAME_SUFFIX_OPENBUTTON}"
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
            bus.post(ImageSelectedEvent(viewNamePrefix, selectedFile.readImageIcon()))

        } else {
            log.debug("Choosing an image aborted by user.")
        }
    }

    override fun asComponent() = this

}

fun File.readImageIcon() = _safeReadImageIcon {
    LOG_image.debug("File#readImageIcon('{}')", this.absolutePath)
    ImageIO.read(this)
}

fun String.readImageIconFromClasspath(): ImageIcon {
    return _safeReadImageIcon {
        LOG_image.debug("String#readImageIconFromClasspath('{}')", this)
        ImageIO.read(SwingImagePicker::class.java.getResource(this))
    }
}

//fun ByteArray.readImageIcon() = _safeReadImageIcon {
//    LOG_image.debug("ByteArray#readImageIcon('{}')")
//    ImageIO.read(ByteArrayInputStream(this))
//}

fun ByteArray.readBufferedImage() = _safeReadBufferedImage {
    ImageIO.read(ByteArrayInputStream(this)) // ... NO! this will simply return null!

//    val inputStream = ByteArrayInputStream(this)
//    ImageIO.read(MemoryCacheImageInputStream(inputStream))

    // NO! getWidth(null) will return -1 and fail
//    val img: Image = Toolkit.getDefaultToolkit().createImage(this)
//    val buffered = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
//    val graphics = buffered.createGraphics()
//    graphics.drawImage(img, 0, 0, null);
//    graphics.dispose()
//    buffered


}

/*
ByteArrayOutputStream baos = new ByteArrayOutputStream();
ImageIO.write(bufferedImage, "jpg", baos);
InputStream is = new ByteArrayInputStream(baos.toByteArray());
 */

private fun _safeReadImageIcon(function: () -> BufferedImage): ImageIcon {
    return ImageIcon(_safeReadBufferedImage(function));
}

private fun _safeReadBufferedImage(function: () -> BufferedImage?): BufferedImage {
    val buffered: BufferedImage?
    try {
        // we could do this in some background thread, but nah, its fast anyway ;)
        buffered = function()
    } catch(e: IOException) {
        throw GadsuException("Failed to read image icon!", e) // TODO show error dialog instead
    }
    return buffered ?: throw GadsuException("Reading image failed (no details available, it is just null)!")
}

