package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.Persistable
import at.cpickl.gadsu.service.clearSeconds
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime

data class Treatment(
        override val id: String?,
        val clientId: String,
        val created: DateTime,
        val number: Int,
        val date: DateTime,
        val note: String // TODO exclude from toString
) :
        Comparable<Treatment>, HasId, Persistable {

    companion object {
        // needed for static extension methods
        fun insertPrototype(clientId: String,
                            number: Int,
                            date: DateTime,
                            created: DateTime = DUMMY_CREATED, // created will be overridden anyway
                            note: String = ""
        ): Treatment {
            // created will be overridden anyway, so its ok to use no Clock here ;)
            return Treatment(
                    null, // id not yet set
                    clientId,
                    created,
                    number,
                    date.clearSeconds(),
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
                .compare(this.number, other.number) // application ensures this is unique within a client
                .result()
    }
}
