package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.global.UserEvent
import com.google.common.base.MoreObjects


class ShowAcupunctureViewEvent : UserEvent()

class ShowAcupunctEvent(val punct: Acupunct) : UserEvent() {
    override fun toString() = MoreObjects.toStringHelper(this)
            .add("punct", punct)
            .toString()
}
