package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.Persistable
import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.service.minutes
import com.google.common.base.MoreObjects
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime
import org.joda.time.Duration

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
                    date.clearSeconds(),
                    duration,
                    aboutClient,
                    aboutTreatment,
                    aboutHomework,
                    note)
        }
    }
    val idComparator: (Treatment) -> Boolean
        get() = { that -> this.id.equals(that.id) }

    init {
        if (!date.equals(date.clearSeconds())) {
            throw GadsuException("Internal state violation: Treatment.date must not have seconds or milliseconds set!")
        }
    }



    override val yetPersisted: Boolean
        get() = id != null

    override fun compareTo(other: Treatment): Int {
        return ComparisonChain.start()
//                .compare(this.clientId, other.clientId)

                // application ensures the number is unique within a client; reverse other and this for DESC sorting
                .compare(other.number, this.number)

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
