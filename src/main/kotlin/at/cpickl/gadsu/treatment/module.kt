package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.treatment.dyn.DynTreatmentService
import at.cpickl.gadsu.treatment.dyn.DynTreatmentServiceImpl
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisJdbcRepository
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRepository
import at.cpickl.gadsu.treatment.inclient.TreatmentList
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientController
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.treatment.view.SwingTreatmentView
import at.cpickl.gadsu.treatment.view.TreatmentController
import at.cpickl.gadsu.treatment.view.TreatmentView
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder


class TreatmentModule : AbstractModule() {
    override fun configure() {

        bind(TreatmentRepository::class.java).to(TreatmentJdbcRepository::class.java).asEagerSingleton()
        bind(HaraDiagnosisRepository::class.java).to(HaraDiagnosisJdbcRepository::class.java).asEagerSingleton()
        bind(DynTreatmentService::class.java).to(DynTreatmentServiceImpl::class.java).asEagerSingleton()

        bind(TreatmentList::class.java).asEagerSingleton()

        // the table which is located in the client view
        bind(TreatmentsInClientView::class.java).asEagerSingleton()
        bind(TreatmentsInClientController::class.java).asEagerSingleton()

        // view will be created dynamically based on current client
        install(FactoryModuleBuilder()
                .implement(TreatmentView::class.java, SwingTreatmentView::class.java)
                .build(TreatmentViewFactory::class.java))

        bind(TreatmentController::class.java).asEagerSingleton()
        bind(TreatmentService::class.java).to(TreatmentServiceImpl::class.java).asEagerSingleton()

        bind(TreatmentGoalController::class.java).asEagerSingleton()

    }
}

interface TreatmentViewFactory {
    fun create(client: Client, treatment: Treatment): TreatmentView
}
