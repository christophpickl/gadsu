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
        /** Unique UUID for database. */
        override val id: String?,
        /** Foreign UUID key. */
        val clientId: String,
        /** Date of initial persistence. */
        val created: DateTime,
        /** Sequence number displayed to the user. */
        val number: Int,
        /** Date when this treatment was held. */
        val date: DateTime,
        /** Duration of the treatment in minutes (instead of storing start & end date). */
        val duration: Duration,
        /** Text field for "Beschwerden". */
        val aboutDiscomfort: String,
        /** Text field for "Diagnose". */
        val aboutDiagnosis: String,
        /** Text field for "Inhalt". */
        val aboutContent: String,
        /** Text field for "Feedback". */
        val aboutFeedback: String,
        /** Text field for "Tips/Hausuebung". */
        val aboutHomework: String,
        /** Text field for "Naechste Behandlung". */
        val aboutUpcoming: String,
        /** Anything else goes here. */
        val note: String
) :
        Comparable<Treatment>, HasId, Persistable {

    init {
        date.ensureNoSeconds()
        date.ensureQuarterMinute()
    }

    companion object {

        // needed for static extension methods as well

        val DEFAULT_DURATION = minutes(60)

        fun insertPrototype(clientId: String,
                            number: Int,
                            date: DateTime,
                            duration: Duration = DEFAULT_DURATION,
                            created: DateTime = DUMMY_CREATED, // created will be overridden anyway
                            aboutDiscomfort: String = "",
                            aboutDiagnosis: String = "",
                            aboutContent: String = "",
                            aboutFeedback: String = "",
                            aboutHomework: String = "",
                            aboutUpcoming: String = "",
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
                    aboutDiscomfort,
                    aboutDiagnosis,
                    aboutContent,
                    aboutFeedback,
                    aboutHomework,
                    aboutUpcoming,
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
