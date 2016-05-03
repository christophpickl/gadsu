package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.tcm.model.AcupunctureRepository
import at.cpickl.gadsu.tcm.model.StaticAcupunctureRepository
import com.google.inject.AbstractModule
import com.google.inject.Scopes


class AcupunctureModule : AbstractModule() {
    override fun configure() {
        bind(AcupunctureRepository::class.java).to(StaticAcupunctureRepository::class.java).`in`(Scopes.SINGLETON)
        bind(AcupunctureController::class.java).asEagerSingleton()
        bind(AcupunctureService::class.java).to(AcupunctureServiceImpl::class.java).`in`(Scopes.SINGLETON)
    }
}
