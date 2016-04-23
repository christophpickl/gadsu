package at.cpickl.gadsu.image

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import org.hsqldb.jdbc.JDBCBlob
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.SqlParameterValue
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.sql.Types
import javax.imageio.ImageIO
import javax.swing.ImageIcon

private val LOG_image = LoggerFactory.getLogger("at.cpickl.gadsu.image")

// --------------------------------------------------------------------------- events


// UI request events
class SelectImageEvent : UserEvent()
class DeleteImageEvent(val client: Client): UserEvent()

// Something was selected by the client.
class ImageSelectedEvent(
        val viewNamePrefix: String, // in order to identify the correct one, as
        val imageFile: File
) : UserEvent()


// --------------------------------------------------------------------------- extension methods

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

fun ByteArray.readBufferedImage() = _safeReadBufferedImage {
    ImageIO.read(ByteArrayInputStream(this))
}

fun MyImage.toSqlBlob(): SqlParameterValue? {
    val bytes = toSaveRepresentation() ?: return null
    return SqlParameterValue(Types.BLOB, JDBCBlob(bytes))
}

fun ImageIcon.size() = Dimension(image.getWidth(imageObserver), image.getHeight(imageObserver))

// --------------------------------------------------------------------------- internal

private fun _safeReadImageIcon(function: () -> BufferedImage): ImageIcon {
    return ImageIcon(_safeReadBufferedImage(function));
}

private fun _safeReadBufferedImage(function: () -> BufferedImage?): BufferedImage {
    val buffered: BufferedImage?
    try {
        // we could do this in some background thread, but nah, its fast anyway ;)
        buffered = function()
    } catch(e: IOException) {
        throw GadsuException("Failed to read image icon!", e) // TODO @EXCEPTION - show error dialog instead
    }
    return buffered ?: throw GadsuException("Reading image failed (no details available, it is just null)!")
}

