package at.cpickl.gadsu.appointment

import at.cpickl.gadsu.appointment.gcal.GCalEvent
import at.cpickl.gadsu.appointment.gcal.GCalService
import at.cpickl.gadsu.appointment.gcal.GCalUpdateEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.LOG
import com.google.common.eventbus.EventBus
import javax.inject.Inject

//fun main(args: Array<String>) {
////    val kotlinHack = when (validate()) {
////        is ValidationResult.Ok -> println("Ok")
////        is ValidationResult.TooLong -> println("Nope")
////        is ValidationResult.MissFormat -> println("Nope")
////    }
//    val result = validate()
//    val kotlinHack = when (result) {
//        is ValidationResult.Ok -> println("Ok")
//        is ValidationResult.MissFormat -> println("Nope, format: ${result.input}")
//
//        is ValidationResult.TooLong -> println("Nope, too long: ${result.length}") // must come before abstract
//        is ValidationResult.TooShort -> println("Nope, too short: ${result.length}")
//        is ValidationResult.SizeInvalid -> throw RuntimeException("not reachable!")
//    }
//}
//fun validate(): ValidationResult {
//    return ValidationResult.TooLong(42)
//}
//sealed class ValidationResult {
//    class Ok : ValidationResult()
//    abstract class SizeInvalid(val length: Int) : ValidationResult()
//    class TooLong(length: Int) : SizeInvalid(length)
//    class TooShort(length: Int) : SizeInvalid(length)
//    class MissFormat(val input: String) : ValidationResult()
//}

interface AppointmentService {
    fun findAllFutureFor(client: Client): List<Appointment>
    fun insertOrUpdate(appointment: Appointment): Appointment
    fun deleteAll(client: Client)
    fun delete(appointment: Appointment)

    fun upcomingAppointmentFor(client: Client): Appointment?
    fun upcomingAppointmentFor(clientId: String): Appointment?
}

class AppointmentServiceImpl @Inject constructor(
        private val repository: AppointmentRepository,
        private val bus: EventBus,
        private val clock: Clock,
        private val gcal: GCalService,
        private val clientRepository: ClientRepository
) : AppointmentService {

    private val log = LOG(javaClass)

    override fun findAllFutureFor(client: Client): List<Appointment> {
        log.debug("findAllFutureFor(client={})", client)
        return repository.findAllStartAfter(clock.now(), client)
    }

    override fun insertOrUpdate(appointment: Appointment): Appointment {
        log.debug("insertOrUpdate(appointment={})", appointment)
        return if (appointment.yetPersisted) {
            repository.update(appointment)
            if (appointment.gcalId != null) {
                gcal.updateEvent(GCalUpdateEvent(
                        id = appointment.gcalId,
                        summary = appointment.note,
                        start = appointment.start,
                        end = appointment.end
                ))
            }
            bus.post(AppointmentChangedEvent(appointment))
            appointment
        } else {
            val client = clientRepository.findById(appointment.clientId)
            val maybeGcalId = gcal.createEvent(GCalEvent(
                    summary = client.fullName,
                    description = appointment.note,
                    start = appointment.start,
                    end = appointment.end
            ))
            val savedAppointment = repository.insert(appointment.copy(gcalId = maybeGcalId?.id, gcalUrl = maybeGcalId?.url))
            bus.post(AppointmentSavedEvent(savedAppointment))
            savedAppointment
        }
    }

    override fun delete(appointment: Appointment) {
        log.debug("delete(appointment={})", appointment)
        repository.delete(appointment)
        if (appointment.gcalId != null) {
            gcal.deleteEvent(appointment.gcalId)
        }
        bus.post(AppointmentDeletedEvent(appointment))
    }

    override fun deleteAll(client: Client) {
        log.debug("deleteAll(client={})", client)
        repository.deleteAll(client)
    }

    override fun upcomingAppointmentFor(client: Client) = upcomingAppointmentFor(client.id!!)
    override fun upcomingAppointmentFor(clientId: String): Appointment? {
        log.debug("upcomingAppointmentFor(clientId={})", clientId)
        return repository.findAllStartAfter(clock.now(), clientId).sorted().firstOrNull()
    }

}
