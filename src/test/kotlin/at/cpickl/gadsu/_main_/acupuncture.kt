package at.cpickl.gadsu._main_

import at.cpickl.gadsu.acupuncture.*
import at.cpickl.gadsu.tcm.model.StaticAcupunctureRepository
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
