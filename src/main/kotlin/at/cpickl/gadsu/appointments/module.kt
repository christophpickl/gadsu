package at.cpickl.gadsu.appointments

import at.cpickl.gadsu.appointments.view.AppoinmentsInClientView
import at.cpickl.gadsu.appointments.view.AppoinmentsInClientViewController
import at.cpickl.gadsu.appointments.view.AppointmentList
import at.cpickl.gadsu.appointments.view.AppointmentWindow
import at.cpickl.gadsu.appointments.view.SwingAppointmentWindow
import com.google.inject.AbstractModule
import com.google.inject.Scopes

class AppointmentModule : AbstractModule() {

    override fun configure() {

        bind(AppointmentRepository::class.java).to(AppointmentJdbcRepository::class.java)
        bind(AppoinmentsInClientView::class.java).asEagerSingleton()
        bind(AppointmentService::class.java).to(AppointmentServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(AppointmentController::class.java).to(AppointmentControllerImpl::class.java).asEagerSingleton()
        bind(AppointmentWindow::class.java).to(SwingAppointmentWindow::class.java).`in`(Scopes.SINGLETON)

        bind(AppointmentList::class.java)
        bind(AppoinmentsInClientViewController::class.java).asEagerSingleton()
    }

}
