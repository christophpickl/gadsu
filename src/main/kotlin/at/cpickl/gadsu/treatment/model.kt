package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.service.HasId
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime

data class Treatment(override val id: String?, val clientId: String, val number: Int, val created: DateTime, val date: DateTime) :
        Comparable<Treatment>, HasId {
    companion object {
        // needed for static extension methods
        fun insertPrototype(clientId: String, number: Int, date: DateTime): Treatment {
            // created will be overridden anyway, so its ok to use no Clock here ;)
            return Treatment(null, clientId, number, DateTime.now(), date)
        }
    }

    val yetPersisted: Boolean
        get() = id != null

    override fun compareTo(other: Treatment): Int {
        return ComparisonChain.start()
                .compare(this.clientId, other.clientId)
                .compare(this.number, other.number)
                .result()
    }

    val idComparator: (Treatment) -> Boolean
        get() = { that -> this.id.equals(that.id) }

}
