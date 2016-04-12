package at.cpickl.gadsu.service

import com.google.inject.AbstractModule
import org.joda.time.DateTime

class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(Clock::class.java).to(RealClock::class.java)
    }
}

interface Clock {
    // TODO myshiatsu got some nice extension methods for formatting joda date time ;)
    fun now(): DateTime
}

class RealClock : Clock {
    override fun now() = DateTime.now()
}
