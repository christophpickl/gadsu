package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.appointment.gcal.sync.GCalControllerImpl
import at.cpickl.gadsu.appointment.gcal.sync.GCalSyncModule
import at.cpickl.gadsu.appointment.gcal.sync.MatchClients
import at.cpickl.gadsu.appointment.gcal.sync.MatchClientsInDb
import com.google.inject.AbstractModule
import com.google.inject.Scopes

// https://developers.google.com/google-apps/calendar/quickstart/java
// https://developers.google.com/google-apps/calendar/v3/reference/

class GCalModule : AbstractModule() {
    override fun configure() {
        install(GCalSyncModule())

        bind(GCalService::class.java).to(InternetConnectionAwareGCalService::class.java).`in`(Scopes.SINGLETON)
    }
}
