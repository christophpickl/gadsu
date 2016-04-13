package at.cpickl.gadsu.client

import at.cpickl.gadsu.UserEvent
import com.google.common.base.MoreObjects
import org.joda.time.DateTime

data class Client(
        val id: String?, // it is null if not yet persisted
        val firstName: String,
        val lastName: String,
        val created: DateTime
) {
    companion object { } // needed for static extension methods

    val fullName: String
        get() = firstName + " " + lastName

    fun withId(newId: String) = Client(newId, firstName, lastName, created)

}

class ClientSelectedEvent(val client: Client) : UserEvent() {
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
            .add("client", client)
            .toString()
    }
}
