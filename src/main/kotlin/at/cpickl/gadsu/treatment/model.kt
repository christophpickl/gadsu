package at.cpickl.gadsu.treatment

import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime

data class Treatment(val id: String?, val clientId: String, val number: Int, val created: DateTime, val date: DateTime) : Comparable<Treatment> {
    companion object {
        // needed for static extension methods
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
