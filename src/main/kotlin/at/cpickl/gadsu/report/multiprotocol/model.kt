package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.persistence.Persistable
import at.cpickl.gadsu.service.HasId
import org.joda.time.DateTime


class MultiProtocol(
        override val id: String?, // it is null if not yet persisted
        override val yetPersisted: Boolean,
        val created: DateTime,
        val someText: String
) : HasId, Persistable {

}
