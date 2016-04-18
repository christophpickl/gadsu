package at.cpickl.gadsu.service

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.treatment.Treatment
import com.google.common.base.Objects
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import org.slf4j.LoggerFactory
import javax.inject.Inject

// FIXME use current infrastructure (also for treatment)
class CurrentModule : AbstractModule() {
    override fun configure() {
        bind(CurrentClient::class.java).asEagerSingleton()
        bind(CurrentTreatment::class.java).asEagerSingleton()
    }
}

abstract class AbstractChangedEvent(val id: String, val oldData: Any?, val newData: Any?) : AppEvent() {
    override fun hashCode(): Int{
        var result = id.hashCode()
        result += 31 * result + (oldData?.hashCode() ?: 0)
        result += 31 * result + (newData?.hashCode() ?: 0)
        return result
    }
    override fun toString(): String{
        return "${javaClass.simpleName}(id='$id', oldData=$oldData, newData=$newData)"
    }
}
class CurrentChangedEvent(id: String, oldData: Any?, newData: Any?) : AbstractChangedEvent(id, oldData, newData) {
    companion object {
        // for extensions
    }
    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is CurrentChangedEvent) return false

        if (id != other.id) return false
        if (oldData != other.oldData) return false
        if (newData != other.newData) return false

        return true
    }
}
class CurrentPropertiesChangedEvent(id: String, oldData: Any?, newData: Any?) : AbstractChangedEvent(id, oldData, newData) {
    companion object {
        // for extensions
    }
    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is CurrentPropertiesChangedEvent) return false

        if (id != other.id) return false
        if (oldData != other.oldData) return false
        if (newData != other.newData) return false

        return true
    }
}

abstract class Current<V : HasId?>(private val id: String, private val bus: EventBus, initialData: V) {
    private val log = LoggerFactory.getLogger(javaClass)
    private var _data: V = initialData

    var data: V
        get() = _data
        set(value) {
            if (value === _data) return

            var dispatchChanged = false
            var dispatchProperties = false

            if ((value !== null && _data === null) ||
                (value === null && _data !== null)) {
                dispatchChanged = true
            }

            if (!dispatchChanged /* meaning neither is null*/ && !Objects.equal(value!!.id, _data!!.id)) {
                dispatchChanged = true
            }

            if (!dispatchChanged && !Objects.equal(value, _data)) {
                dispatchProperties = true
            }

            if (dispatchChanged) {
                bus.post(CurrentChangedEvent(id, _data, value))
            }
            if (dispatchProperties) {
                bus.post(CurrentPropertiesChangedEvent(id, _data, value))
            }

            _data = value // MINOR or should this be done before dispatching event?!
        }

}

class CurrentClient @Inject constructor(bus: EventBus) :
        Current<Client>(ID, bus, INITIAL_VALUE) {
    companion object {
        val ID: String = "client"
        val INITIAL_VALUE = Client.INSERT_PROTOTYPE
    }
}
val CurrentChangedEvent.Companion.ID_Client: String get() = CurrentClient.ID
val CurrentPropertiesChangedEvent.Companion.ID_Client: String get() = CurrentClient.ID


class CurrentTreatment @Inject constructor(bus: EventBus) :
        Current<Treatment?>(ID, bus, null) {
    companion object {
        val ID: String = "treatment"
    }
}
val CurrentChangedEvent.Companion.ID_Treatment: String get() = CurrentTreatment.ID
val CurrentPropertiesChangedEvent.Companion.ID_Treatment: String get() = CurrentTreatment.ID
