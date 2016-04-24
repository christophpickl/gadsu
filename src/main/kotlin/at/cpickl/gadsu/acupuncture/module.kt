package at.cpickl.gadsu.acupuncture

import com.google.inject.AbstractModule
import com.google.inject.Scopes


class AcupunctureModule : AbstractModule() {
    override fun configure() {
        bind(AcupunctureRepository::class.java).to(InMemoryAcupunctureRepository::class.java).`in`(Scopes.SINGLETON)
        bind(AcupunctureController::class.java).asEagerSingleton()
        bind(AcupunctureService::class.java).to(AcupunctureServiceImpl::class.java).`in`(Scopes.SINGLETON)
    }
}
