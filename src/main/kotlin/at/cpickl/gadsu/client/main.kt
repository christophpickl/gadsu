package at.cpickl.gadsu.client

import com.google.inject.AbstractModule

class ClientModule : AbstractModule() {
    override fun configure() {

    }
}

data class Client(
        val firstName: String,
        val lastName: String
)
