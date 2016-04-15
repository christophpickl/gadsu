package at.cpickl.gadsu.client

import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime

data class Client(
        val id: String?, // it is null if not yet persisted
        val firstName: String,
        val lastName: String,
        val created: DateTime
) : Comparable<Client> {
    companion object {
        // needed for static extension methods

        val INSERT_PROTOTYPE = Client(null, "", "", DateTime.now()) // created will be overridden anyway, so its ok to use no Clock here ;)
    }

    val yetPersisted: Boolean
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

}

/*

use copy instead

class ClientBuilder(client: Client? = null) {
    private var id: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var created: DateTime? = null

    init {
        if (client != null) {
            id = client.id
            firstName = client.firstName
            lastName = client.lastName
            created = client.created
        }
    }

    fun Client.builder() = ClientBuilder(this)

    fun id(value: String?): ClientBuilder {
        id = value
        return this
    }

    fun firstName(value: String): ClientBuilder {
        firstName = value
        return this
    }

    fun lastName(value: String): ClientBuilder {
        lastName = value
        return this
    }

    fun created(value: DateTime): ClientBuilder {
        created = value
        return this
    }

    fun build() = Client(id, firstName!!, lastName!!, created!!)

}

 */
