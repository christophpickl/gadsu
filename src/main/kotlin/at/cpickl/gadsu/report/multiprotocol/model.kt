package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.persistence.Persistable
import at.cpickl.gadsu.service.HasId
import org.joda.time.DateTime


data class MultiProtocol(
        override val id: String?, // it is null if not yet persisted
        val created: DateTime,
        val description: String,
        val treatmentIds: List<String>
) : HasId, Persistable {

    override val yetPersisted: Boolean get() = id != null

}
