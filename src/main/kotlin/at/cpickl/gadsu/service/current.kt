package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.global.AppEvent
import at.cpickl.gadsu.treatment.CurrentTreatment
import com.google.common.base.Objects
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class CurrentModule : AbstractModule() {
    override fun configure() {
        bind(CurrentClient::class.java).asEagerSingleton()
        bind(CurrentTreatment::class.java).asEagerSingleton()
    }
}

/**
 * Either because the data completely changed the reference, or just property/ies changed.
 */
interface CurrentEvent {
    val id: String
    val oldData: Any?
    val newData: Any?
}

abstract class AbstractChangedEvent(
        override val id: String,
        override val oldData: Any?,
        override val newData: Any?
) : AppEvent(), CurrentEvent {

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is AbstractChangedEvent) return false

        if (id != other.id) return false
        if (oldData != other.oldData) return false
        if (newData != other.newData) return false

        return true
    }

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
}
class CurrentPropertiesChangedEvent(id: String, oldData: Any?, newData: Any?) : AbstractChangedEvent(id, oldData, newData) {
    companion object {
        // for extensions
    }
}

abstract class Current<V : HasId?>(private val id: String, private val bus: EventBus, initialData: V) {
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

            val oldData = _data
            _data = value // must be done before dispatching event!
            if (dispatchChanged) {
                bus.post(CurrentChangedEvent(id, oldData, value))
            }
            if (dispatchProperties) {
                bus.post(CurrentPropertiesChangedEvent(id, oldData, value))
            }

        }

}
