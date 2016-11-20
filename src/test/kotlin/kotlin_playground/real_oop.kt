package kotlin_playground

import at.cpickl.gadsu.allGadsuModules
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javax.inject.Inject

fun main(args: Array<String>) {
    Guice.createInjector(RealOopModule())
            .getInstance(RealOopApp::class.java)
            .doWork()
}

class RealOopModule : AbstractModule() {
    override fun configure() {
        val databaseUrl = "jdbc:hsqldb:mem:realOOP"
        allGadsuModules(databaseUrl).forEach {
            install(it)
        }

        bind(Gd::class.java).asEagerSingleton()
        bind(RealOopApp::class.java).asEagerSingleton()
    }
}

interface GdClient {
    var firstName: String

    fun save()
}

data class GdClientImpl(
        var client: Client,
        private val clientService: ClientService
) : GdClient {
    override var firstName: String
        get() = client.firstName
        set(value) {
            client = client.copy(firstName = value)
        }


    override fun save() {

//      =======>
//      clientService.insertOrUpdate(this.client)
//      =======>

        println("save() => $client")
    }
}

class Gd @Inject constructor(
        private val clientService: ClientService
) {

    fun byClient(client: Client): GdClient {
        return GdClientImpl(client, clientService)
    }
}

class RealOopApp @Inject constructor(
        private val gd: Gd
) {
    fun doWork() {
        val client = gd.byClient(Client.unsavedValidInstance())

        println("client = $client")

        client.firstName = "new"
        client.save()

        println("client = $client")

    }

}
