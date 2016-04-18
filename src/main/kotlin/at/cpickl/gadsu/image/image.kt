package at.cpickl.gadsu.image

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon


object Images {
    val DEFAULT_PROFILE_MAN: MyImage = DefaultImage("/gadsu/images/profile_pic_default_man.jpg")
    val DEFAULT_PROFILE_WOMAN: MyImage = DefaultImage("/gadsu/images/profile_pic_default_woman.jpg")
}

fun ImageIcon.toMyImage(): MyImage = ImageIconImage(this)
fun BufferedImage.toMyImage(): MyImage = ImageIconImage(ImageIcon(this))
fun File.toMyImage(): MyImage = FileImage(this)
fun String.toMyImage(): MyImage = ClasspathImage(this)


interface MyImage {

    val size: Dimension

    fun toViewRepresentation(): Icon

    /**
     * @return null if default is still set (or user cleared picture, then it will fall back to default again)
     */
    fun toSaveRepresentation(): ByteArray?

}


/**
 * Default if nothing is selected.
 */
private class DefaultImage(private val classpath: String) : MyImage {

    private val delegate = ClasspathImage(classpath)

    override val size: Dimension get() = delegate.size
    override fun toViewRepresentation() = delegate.toViewRepresentation()
    override fun toSaveRepresentation() = null // disable persisting default images

}

/**
 * Selected by user.
 */
private class ImageIconImage(icon: ImageIcon) : IconifiedImage(icon)


/**
 * Internally used.
 */
private class ClasspathImage(private val classpath: String) : IconifiedImage(classpath.readImageIconFromClasspath())


/**
 * Client selected.
 */
private class FileImage(file: File) : IconifiedImage(file.readImageIcon())


private abstract class IconifiedImage(private val icon: ImageIcon) : MyImage {
    override fun toViewRepresentation() = icon
    override fun toSaveRepresentation() = convertJpgBytes(icon)

    private val _size = Dimension(icon.image.getWidth(icon.imageObserver), icon.image.getHeight(icon.imageObserver))
    override val size: Dimension get() = _size
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

