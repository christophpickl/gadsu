package at.cpickl.gadsu.service

import com.google.inject.AbstractModule

class ServiceModule(private val nodePrefsFqn: String?) : AbstractModule() {

    override fun configure() {
        bind(Clock::class.java).to(RealClock::class.java)
        bind(IdGenerator::class.java).to(UuidGenerator::class.java)

        bind(MetaInf::class.java).toProvider(MetaInfLoader::class.java)

        val nodeClass = if (nodePrefsFqn == null) JavaPrefs::class.java else Class.forName(nodePrefsFqn)
        bind(Prefs::class.java).toInstance(JavaPrefs(nodeClass))
    }

}
