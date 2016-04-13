package at.cpickl.gadsu.client

import com.google.inject.AbstractModule


class ClientModule : AbstractModule() {
    override fun configure() {
        bind(ClientRepository::class.java).to(ClientSpringJdbcRepository::class.java)

        bind(ClientViewController::class.java).asEagerSingleton()
    }
}
