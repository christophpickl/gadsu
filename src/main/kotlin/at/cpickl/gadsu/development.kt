package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject

class DevelopmentInsertClientEvent : UserEvent()

class DevelopmentController @Inject constructor(
        private val clientRepo: ClientRepository,
        private val clock: Clock
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onInsertClient(event: DevelopmentInsertClientEvent) {
        log.debug("onInsertClient(event)")
        clientRepo.insert(Client(null, "devFoo", "devBar", clock.now()))
    }

}
