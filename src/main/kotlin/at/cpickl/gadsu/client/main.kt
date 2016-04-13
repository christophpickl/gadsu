package at.cpickl.gadsu.client

import org.joda.time.DateTime

data class Client(
        val id: String?, // it is null if not yet persisted
        val firstName: String,
        val lastName: String,
        val created: DateTime
) {
    companion object { } // needed for static extension methods

    fun withId(newId: String) = Client(newId, firstName, lastName, created)

}
