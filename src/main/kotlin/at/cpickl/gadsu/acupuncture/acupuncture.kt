package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.tcm.model.StaticAcupunctureRepository
import com.google.common.eventbus.EventBus


fun main(args: Array<String>) {
    val bus = EventBus()

    val repository = StaticAcupunctureRepository()
    val service = AcupunctureServiceImpl(repository)

    val list = AcupunctureList(bus)
    val frame = AcupunctureFrame(bus, list)
    val controller = AcupunctureController(frame, service)
    bus.register(controller)
    bus.post(ShopAcupunctureViewEvent())

}
