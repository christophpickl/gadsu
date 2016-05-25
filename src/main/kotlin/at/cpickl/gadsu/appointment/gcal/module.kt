package at.cpickl.gadsu.appointment.gcal

import com.google.inject.AbstractModule
import com.google.inject.Scopes

// https://developers.google.com/google-apps/calendar/quickstart/java
// https://developers.google.com/google-apps/calendar/v3/reference/

class GCalModule : AbstractModule() {

    override fun configure() {
        bind(GCalConnector::class.java).to(GCalConnectorImpl::class.java)
        bind(GCalService::class.java).to(InternetConnectionAwareGCalService::class.java).`in`(Scopes.SINGLETON)
    }
}