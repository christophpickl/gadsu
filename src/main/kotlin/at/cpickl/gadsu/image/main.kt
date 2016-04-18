package at.cpickl.gadsu.image

import at.cpickl.gadsu.UserEvent
import javax.swing.ImageIcon

interface MyImage {

}


class ImageSelectedEvent(
        val viewNamePrefix: String, // in order to identify the correct one, as
        val image: ImageIcon
) : UserEvent()
