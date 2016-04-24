package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.QuitEvent
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject



open class AcupunctureController @Inject constructor(
        private val frame: AcupunctureFrame,
        private val service: AcupunctureService
) {

    private val log = LoggerFactory.getLogger(javaClass)


    init {
        Search.setup(frame.inpSearch, frame.list, service)

        frame.addCloseListener {
            log.trace("frame closing")
            frame.close()
        }

    }

    @Subscribe open fun onShopAcupunctureViewEvent(event: ShopAcupunctureViewEvent) {
        frame.start()
    }

    @Subscribe open fun onQuitEvent(event: QuitEvent) {
        frame.destroy()
    }

}
