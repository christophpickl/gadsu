package at.cpickl.gadsu.image

import at.cpickl.gadsu.UserEvent
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon


class ImageSelectedEvent(
        val viewNamePrefix: String, // in order to identify the correct one, as
        val image: ImageIcon
) : UserEvent()


interface MyImage {

    fun toViewRepresentation(): Icon
    fun toSaveRepresentation(): ByteArray?

}

object Images {
    val DEFAULT_PROFILE_MAN: MyImage = DefaultImage("/gadsu/images/profile_pic_default_man.jpg")
    val DEFAULT_PROFILE_WOMAN: MyImage = DefaultImage("/gadsu/images/profile_pic_default_woman.jpg")

    fun readFromImageIcon(icon: ImageIcon): MyImage {
        return ImageIconImage(icon)
    }

    fun readFromBufferedImage(buffered: BufferedImage): MyImage {
        return ImageIconImage(ImageIcon(buffered))
    }

    fun readFromClasspath(classpath: String): MyImage {
        return ClasspathImage(classpath)
    }
}

/**
 * Default if nothing is selected.
 */
private class DefaultImage(private val classpath: String) : MyImage {

    private val delegate = ClasspathImage(classpath)

    override fun toViewRepresentation() = delegate.toViewRepresentation()
    override fun toSaveRepresentation() = null // disable persisting default images

}

/**
 * Selected by user.
 */
private class ImageIconImage(private val icon: ImageIcon) : MyImage {

    override fun toViewRepresentation() = icon
    override fun toSaveRepresentation() = convertJpgBytes(icon)

}

/**
 * Internally used.
 */
private class ClasspathImage(private val classpath: String) : MyImage {

    private val icon: ImageIcon
    init {
        icon = classpath.readImageIconFromClasspath()
    }

    override fun toViewRepresentation() = icon
    override fun toSaveRepresentation() = convertJpgBytes(icon)

}

private fun convertJpgBytes(icon: ImageIcon): ByteArray {
    val bufferedImage = icon.image as BufferedImage

    val baos = ByteArrayOutputStream()
    ImageIO.write( bufferedImage, "jpg", baos);
    baos.flush()
    val imageInByte = baos.toByteArray()
    baos.close()

    return imageInByte
}

