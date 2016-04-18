package at.cpickl.gadsu.image

import java.awt.Image
import javax.swing.ImageIcon

fun ImageIcon.scale(targetSize: ImageSize): ImageIcon {
    return ImageIcon(this.image.getScaledInstance(targetSize.width, targetSize.height, Image.SCALE_DEFAULT)) // Image.SCALE_FAST
}
