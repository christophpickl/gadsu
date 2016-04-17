package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory
import java.util.Objects

// FIXME use current infrastructure (also for treatment)
class CurrentModule : AbstractModule() {
    override fun configure() {
        bind(CurrentClient::class.java).asEagerSingleton()
    }
}

data class CurrentChangedEvent(val id: String, val oldData: Any, val newData: Any)

interface HasId {
    val id: String?
}

abstract class Current<V : HasId>(private val id: String, initialData: V, private val bus: EventBus) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var _data: V = initialData

    var data: V
        get() = _data
        set(value) {
            if (value == data) {
                return
            }
            if (!Objects.equals(value.id, data.id)) {
                log.trace("Current value changed from ({}) to ({}).", _data, value)
                bus.post(CurrentChangedEvent(id, data, value))
            }
            _data = value // MINOR or should this be done before dispatching event?!
        }

}

class CurrentClient(initialData: Client, bus: EventBus) : Current<Client>("client", initialData, bus)
