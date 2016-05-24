package at.cpickl.gadsu.appointments

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import javax.inject.Inject

interface AppointmentService {
    fun findAllFutureFor(client: Client): List<Appointment>
    fun insertOrUpdate(appointment: Appointment): Appointment
    fun deleteAll(client: Client)
    fun delete(appointment: Appointment)
}

class AppointmentServiceImpl @Inject constructor(
        private val repository: AppointmentRepository,
        private val bus: EventBus,
        private val clock: Clock
) : AppointmentService {
    override fun findAllFutureFor(client: Client): List<Appointment> {
        return repository.findAllStartAfter(clock.now(), client)
    }

    override fun insertOrUpdate(appointment: Appointment): Appointment {
        return if (appointment.yetPersisted) {
            repository.update(appointment)
            bus.post(AppointmentChangedEvent(appointment))
            appointment
        } else {
            val savedAppointment = repository.insert(appointment)
            bus.post(AppointmentSavedEvent(savedAppointment))
            savedAppointment
        }
    }

    override fun delete(appointment: Appointment) {
        repository.delete(appointment)
    }

    override fun deleteAll(client: Client) {
        repository.deleteAll(client)
    }

}
