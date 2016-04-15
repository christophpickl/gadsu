package at.cpickl.gadsu.service

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class ServiceModule : AbstractModule() {

    override fun configure() {
        bind(Clock::class.java).to(RealClock::class.java)
        bind(IdGenerator::class.java).to(UuidGenerator::class.java)
        bind(MetaInfLoader::class.java).`in`(Scopes.SINGLETON)
    }

}
