package at.cpickl.gadsu.view

import at.cpickl.gadsu.service.LOG
import javax.imageio.ImageIO
import javax.swing.ImageIcon

object Images {

    private val log = LOG(javaClass)

    fun loadFromClasspath(path: String): ImageIcon {
        log.debug("loadFromClasspath(path='{}')", path)
        val stream = Images::class.java.getResourceAsStream(path)
        try {
            val image = ImageIO.read(stream)
            return ImageIcon(image)
        } finally {
            stream.close()
        }
    }
}