package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.treatment.view.TreatmentTable
import at.cpickl.gadsu.treatment.view.TreatmentTableController
import com.google.inject.AbstractModule
import com.google.inject.Scopes


class TreatmentModule : AbstractModule() {
    override fun configure() {

        bind(TreatmentRepository::class.java).to(TreatmentSpringJdbcRepository::class.java)

        bind(TreatmentTableController::class.java).asEagerSingleton()
        bind(TreatmentTable::class.java).`in`(Scopes.SINGLETON)
    }

}
