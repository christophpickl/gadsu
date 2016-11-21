package at.cpickl.gadsu.appointment.gcal.sync

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class GCalSyncModule : AbstractModule() {
    override fun configure() {

        bind(MatchClients::class.java).to(MatchClientsInDb::class.java).`in`(Scopes.SINGLETON)
        bind(SyncService::class.java).to(GCalSyncService::class.java).`in`(Scopes.SINGLETON)
        bind(GCalSyncer::class.java).to(GCalSyncerImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GCalControllerImpl::class.java).asEagerSingleton()

    }
}
