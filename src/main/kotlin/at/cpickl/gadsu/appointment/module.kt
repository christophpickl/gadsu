package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.appointment.gcal.GCalModule
import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.appointment.view.AppoinmentsInClientViewController
import at.cpickl.gadsu.appointment.view.AppointmentList
import at.cpickl.gadsu.appointment.view.AppointmentWindow
import at.cpickl.gadsu.appointment.view.SwingAppointmentWindow
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.binder.AnnotatedBindingBuilder
import com.google.inject.binder.ScopedBindingBuilder

class AppointmentModule : AbstractModule() {

    override fun configure() {

        bind(AppointmentRepository::class.java).to(AppointmentJdbcRepository::class.java)
        bind(AppoinmentsInClientView::class.java).asEagerSingleton()
        bind(AppointmentService::class.java).to(AppointmentServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(AppointmentController::class.java).to(AppointmentControllerImpl::class.java).asEagerSingleton()

        bind(AppointmentWindow::class.java).to(SwingAppointmentWindow::class.java).`in`(Scopes.SINGLETON)

        bind(AppointmentList::class.java)
        bind(AppoinmentsInClientViewController::class.java).asEagerSingleton()

        install(GCalModule())
    }

}
