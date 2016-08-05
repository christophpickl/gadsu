package at.cpickl.gadsu.image

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Gender
import sun.awt.image.ToolkitImage
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon


interface MyImage {
    companion object {
        val DEFAULT_PROFILE_MAN: MyImage = DefaultImage("/gadsu/images/profile_pic-default_man.png")
        val DEFAULT_PROFILE_WOMAN: MyImage = DefaultImage("/gadsu/images/profile_pic-default_woman.png")
        val DEFAULT_PROFILE_ALIEN: MyImage = DefaultImage("/gadsu/images/profile_pic-default_alien.png")

        fun byIcon(icon: ImageIcon) = icon.toMyImage()
        fun byBuffered(buffered: BufferedImage) = buffered.toMyImage()
        fun byFile(file: File) = file.toMyImage()
        fun byClasspath(classpath: String) = classpath.toMyImage()
        fun byByteArray(bytes: ByteArray) = bytes.toMyImage()

    }

    /**
     * @return null if default is still set (or user cleared picture, then it will fall back to default again)
     */
    fun toSaveRepresentation(): ByteArray?

    /**
     * Runs coherent to #toSaveRepresentation
     */
    val isUnsavedDefaultPicture: Boolean

    fun toViewBigRepresentation(): Icon
    fun toViewMedRepresentation(): Icon
    fun toViewLilRepresentation(): Icon


}


// extension methods
fun ImageIcon.toMyImage(): MyImage = ImageIconImage(this)
fun BufferedImage.toMyImage(): MyImage = ImageIconImage(ImageIcon(this))
fun File.toMyImage(): MyImage = FileImage(this)
fun String.toMyImage(): MyImage = ClasspathImage(this)
fun ByteArray.toMyImage(): MyImage = this.readBufferedImage().toMyImage()

val Gender.defaultImage: MyImage get() =
    when(this) {
        Gender.MALE -> MyImage.DEFAULT_PROFILE_MAN
        Gender.FEMALE -> MyImage.DEFAULT_PROFILE_WOMAN
        else -> MyImage.DEFAULT_PROFILE_ALIEN
    }

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



/**
 * Default if nothing is selected.
 */
private class DefaultImage(classpath: String) : MyImage {
    private val delegate = ClasspathImage(classpath)

    override fun toSaveRepresentation() = null // disable persisting default images
    override val isUnsavedDefaultPicture = true

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
private class ClasspathImage(classpath: String) : IconifiedImage(classpath.readImageIconFromClasspath())

/**
 * Client selected.
 */
private class FileImage(file: File) : IconifiedImage(file.readImageIcon())


private abstract class IconifiedImage(original: ImageIcon) : MyImage {

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
    override val isUnsavedDefaultPicture = false

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

