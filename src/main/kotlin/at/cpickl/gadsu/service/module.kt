package at.cpickl.gadsu.service

import com.google.inject.AbstractModule

class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(Clock::class.java).to(RealClock::class.java)
    }
}
