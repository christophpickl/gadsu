package at.cpickl.gadsu

import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory

open class GadsuException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Marker interface marking an exception to be caught and presented to the client.
 */
interface GadsuUserException



object GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)
    fun register() {
        log.debug("Registering global exception handler.")

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.error("Uncaught exception in thread (${thread.name})", throwable)
            Dialogs(null).show(
                    title = "Fehler",
                    message = "Ein unerwarteter Fehler ist aufgetreten! Siehe Programmlogs f\u00fcr mehr Details.",
                    buttonLabels = arrayOf("Programm schlie\u00dfen"),
                    type = DialogType.ERROR
            )
            System.exit(1)
        }
    }
}

class AllMightyEventCatcher {
    private val log = LoggerFactory.getLogger(javaClass)

    @Subscribe fun onEvent(event: Any) {
        log.trace("Event has been dispatched on EventBus: {}", event)
    }

    // EITHER - OR

    //    @Subscribe fun onDeadEvent(event: DeadEvent) {
    //        throw GadsuException("Event (${event.event}) was not handled by anyone! (source: ${event.source})")
    //    }

}

