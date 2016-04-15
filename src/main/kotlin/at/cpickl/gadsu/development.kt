package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCreatedEvent
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject


class Development {
    companion object {
        val ENABLED: Boolean = System.getProperty("gadsu.development", "").equals("true")
        init {
            if (ENABLED) {
                println("Development mode is enabled via '-Dgadsu.development=true'")
            }
        }
    }
}


class DevelopmentInsertClientEvent : UserEvent()

@Suppress("UNUSED_PARAMETER")
class DevelopmentController @Inject constructor(
        private val clientRepo: ClientRepository,
        private val clock: Clock,
        private val bus: EventBus
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onInsertClient(event: DevelopmentInsertClientEvent) {
        log.debug("onInsertClient(event)")
        val insertedClient = clientRepo.insert(Client(null, "devFoo", "devBar", clock.now()))
        bus.post(ClientCreatedEvent(insertedClient))
    }

}
