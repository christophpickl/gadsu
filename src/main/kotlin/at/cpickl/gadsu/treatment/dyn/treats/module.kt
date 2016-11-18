package at.cpickl.gadsu.treatment.dyn.treats

import at.cpickl.gadsu.treatment.TreatmentJdbcRepository
import at.cpickl.gadsu.treatment.TreatmentRepository
import com.google.inject.AbstractModule


class DynTreatmentModule : AbstractModule() {
    override fun configure() {

        bind(TreatmentRepository::class.java).to(TreatmentJdbcRepository::class.java).asEagerSingleton()
        bind(HaraDiagnosisRepository::class.java).to(HaraDiagnosisJdbcRepository::class.java).asEagerSingleton()
        bind(BloodPressureRepository::class.java).to(BloodPressureJdbcRepository::class.java).asEagerSingleton()
        bind(TongueDiagnosisRepository::class.java).to(TongueDiagnosisJdbcRepository::class.java).asEagerSingleton()
        bind(PulseDiagnosisRepository::class.java).to(PulseDiagnosisJdbcRepository::class.java).asEagerSingleton()

    }
}
