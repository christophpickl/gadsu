package non_test._main_

import at.cpickl.gadsu.acupuncture.AcupunctureController
import at.cpickl.gadsu.acupuncture.AcupunctureFrame
import at.cpickl.gadsu.acupuncture.AcupunctureList
import at.cpickl.gadsu.acupuncture.AcupunctureServiceImpl
import at.cpickl.gadsu.acupuncture.ShowAcupunctureViewEvent
import at.cpickl.gadsu.acupuncture.StaticAcupunctureRepository
import com.google.common.eventbus.EventBus


fun main(args: Array<String>) {
    val bus = EventBus()

    val repository = StaticAcupunctureRepository()
    val service = AcupunctureServiceImpl(repository)

    val list = AcupunctureList(bus)
    val frame = AcupunctureFrame(list)
    val controller = AcupunctureController(frame, service)
    bus.register(controller)
    bus.post(ShowAcupunctureViewEvent())

}
