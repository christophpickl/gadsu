package at.cpickl.gadsu.service

import com.google.inject.AbstractModule

class ServiceModule : AbstractModule() {

    override fun configure() {
        bind(Clock::class.java).to(RealClock::class.java)
        bind(IdGenerator::class.java).to(UuidGenerator::class.java)

        bind(MetaInf::class.java).toProvider(MetaInfLoader::class.java)
        bind(Prefs::class.java).to(JavaPrefs::class.java)
    }

}
