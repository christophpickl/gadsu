package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.Persistable
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime

data class Client(
        override val id: String?, // it is null if not yet persisted
        val created: DateTime,
        val firstName: String,
        val lastName: String,
        val picture: MyImage
) : Comparable<Client>, HasId, Persistable {
    companion object {
        // needed for static extension methods

        // created will be overridden anyway
        val INSERT_PROTOTYPE = Client(null, DateFormats.DATE_TIME.parseDateTime("01.01.2000 00:00:00"), "", "", Images.DEFAULT_PROFILE_MAN)
    }

    override val yetPersisted: Boolean
        get() = id != null

    val fullName: String
        get() {
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                return firstName + " " + lastName
            }
            return firstName + lastName
        }

    override fun compareTo(other: Client): Int {
        return ComparisonChain.start()
                .compare(this.lastName, other.lastName)
                .compare(this.firstName, other.firstName)
                .result()
    }

    val idComparator: (Client) -> Boolean
        get() = { that -> this.id.equals(that.id) }

}
