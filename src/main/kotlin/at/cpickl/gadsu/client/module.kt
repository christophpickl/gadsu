package at.cpickl.gadsu.client

import com.google.inject.AbstractModule


class ClientModule : AbstractModule() {
    override fun configure() {
        bind(ClientViewController::class.java).asEagerSingleton()
    }
}
