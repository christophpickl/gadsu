package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.persistence.Persistable
import at.cpickl.gadsu.service.Current
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.clearMinutes
import at.cpickl.gadsu.service.ensureNoSeconds
import at.cpickl.gadsu.service.ensureQuarterMinute
import at.cpickl.gadsu.service.minutes
import com.google.common.base.MoreObjects
import com.google.common.collect.ComparisonChain
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import org.joda.time.Duration
import javax.inject.Inject


class CurrentTreatment @Inject constructor(bus: EventBus) :
        Current<Treatment?>(ID, bus, null) {
    companion object {
        val ID: String = "treatment"
    }
}
fun CurrentEvent.forTreatment(function: (Treatment?) -> Unit) {
    if (this.id == CurrentTreatment.ID) function(this.newData as Treatment?)
}


data class Treatment(
        override val id: String?,
        val clientId: String,
        val created: DateTime,
        val number: Int,
        val date: DateTime,
        val duration: Duration, // in minutes
        val aboutClient: String,
        val aboutTreatment: String,
        val aboutHomework: String,
        val note: String
) :
        Comparable<Treatment>, HasId, Persistable {

    init {
        date.ensureNoSeconds()
        date.ensureQuarterMinute()
    }

    companion object {

        // needed for static extension methods as well

        private val DEFAULT_DURATION = minutes(60)

        fun insertPrototype(clientId: String,
                            number: Int,
                            date: DateTime,
                            duration: Duration = DEFAULT_DURATION,
                            created: DateTime = DUMMY_CREATED, // created will be overridden anyway
                            aboutClient: String = "",
                            aboutTreatment: String = "",
                            aboutHomework: String = "",
                            note: String = ""
        ): Treatment {
            // created will be overridden anyway, so its ok to use no Clock here ;)
            return Treatment(
                    null, // id not yet set
                    clientId,
                    created,
                    number,
                    date.clearMinutes(),
                    duration,
                    aboutClient,
                    aboutTreatment,
                    aboutHomework,
                    note)
        }
    }
    val idComparator: (Treatment) -> Boolean
        get() = { that -> this.id.equals(that.id) }


    override val yetPersisted: Boolean
        get() = id != null

    override fun compareTo(other: Treatment): Int {
        return ComparisonChain.start()
//                .compare(this.clientId, other.clientId)
                // application ensures the number is unique within a client
                .compare(this.number, other.number)
                .result()
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("id", id)
                .add("clientId", clientId)
                .add("number", number)
                .add("date", date)
                .add("created", created)
                .toString()
    }
}
