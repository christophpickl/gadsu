package at.cpickl.gadsu.image

import at.cpickl.gadsu.GadsuException
import sun.awt.image.ToolkitImage
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

enum class ImageSize(private val _dimension: Dimension) {
    /** In picture tab when changing picture.*/
    BIG(Dimension(200, 200)),

    /** Usually displayed in detail views. */
    MEDIUM(Dimension(100, 100)),

    /** Rendered in lists. */
    LITTLE(Dimension(50, 50));

    val width: Int get() = _dimension.width
    val height: Int get() = _dimension.height

    fun toDimension(): Dimension = _dimension
}


object Images {
    val DEFAULT_PROFILE_MAN: MyImage = DefaultImage("/gadsu/images/profile_pic-default_man.jpg")
    val DEFAULT_PROFILE_WOMAN: MyImage = DefaultImage("/gadsu/images/profile_pic-default_woman.jpg")
}

fun ImageIcon.toMyImage(): MyImage = ImageIconImage(this)
fun BufferedImage.toMyImage(): MyImage = ImageIconImage(ImageIcon(this))
fun File.toMyImage(): MyImage = FileImage(this)
fun String.toMyImage(): MyImage = ClasspathImage(this)


interface MyImage {

    /**
     * @return null if default is still set (or user cleared picture, then it will fall back to default again)
     */
    fun toSaveRepresentation(): ByteArray?

    fun toViewBigRepresentation(): Icon
    fun toViewMedRepresentation(): Icon
    fun toViewLilRepresentation(): Icon

}


/**
 * Default if nothing is selected.
 */
private class DefaultImage(private val classpath: String) : MyImage {

    private val delegate = ClasspathImage(classpath)

    override fun toSaveRepresentation() = null // disable persisting default images

    override fun toViewBigRepresentation() = delegate.toViewBigRepresentation()
    override fun toViewMedRepresentation() = delegate.toViewMedRepresentation()
    override fun toViewLilRepresentation() = delegate.toViewLilRepresentation()

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


private abstract class IconifiedImage(private val original: ImageIcon) : MyImage {

    private val bytes: ByteArray
    private val big: ImageIcon
    private val med: ImageIcon
    private val lil: ImageIcon
    init {
        big = original.scale(ImageSize.BIG)
        med = original.scale(ImageSize.MEDIUM)
        lil = original.scale(ImageSize.LITTLE)

        bytes = big.toByteArray()
    }

    override fun toSaveRepresentation() = bytes

    override fun toViewBigRepresentation() = big
    override fun toViewMedRepresentation() = med
    override fun toViewLilRepresentation() = lil

}

private fun ImageIcon.toByteArray(): ByteArray {
    val bufferedImage: BufferedImage

    if (image is BufferedImage) {
        bufferedImage = image as BufferedImage
    } else if (image is ToolkitImage) {
        val tkImage = image as ToolkitImage
        bufferedImage = tkImage.bufferedImage
    } else {
        throw GadsuException("Converting image to byte array failed because of unhandled image type: ${image.javaClass.name}")
    }

    val outStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "jpg", outStream)
    outStream.flush()
    val imageInByte = outStream.toByteArray()
    outStream.close()

    return imageInByte
}

