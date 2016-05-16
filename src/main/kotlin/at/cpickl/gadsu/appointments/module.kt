package at.cpickl.gadsu.appointments

import com.google.inject.AbstractModule

class AppointmentModule : AbstractModule() {

    override fun configure() {
        bind(AppointmentController::class.java).to(AppointmentControllerImpl::class.java).asEagerSingleton()
        bind(AppointmentRepository::class.java).to(AppointmentJdbcRepository::class.java)
    }

}
