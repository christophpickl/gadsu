package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientController
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.treatment.view.SwingTreatmentView
import at.cpickl.gadsu.treatment.view.TreatmentController
import at.cpickl.gadsu.treatment.view.TreatmentView
import com.google.inject.AbstractModule
import com.google.inject.Scopes


class TreatmentModule : AbstractModule() {
    override fun configure() {

        // persistence
        bind(TreatmentRepository::class.java).to(TreatmentSpringJdbcRepository::class.java).`in`(Scopes.SINGLETON)

        // inclient
        bind(TreatmentsInClientView::class.java).`in`(Scopes.SINGLETON)
        bind(TreatmentsInClientController::class.java).asEagerSingleton()

        // view
        bind(TreatmentView::class.java).to(SwingTreatmentView::class.java).`in`(Scopes.SINGLETON)
        bind(TreatmentController::class.java).asEagerSingleton()

    }
}
