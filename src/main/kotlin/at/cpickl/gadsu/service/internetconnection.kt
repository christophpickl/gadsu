package at.cpickl.gadsu.service

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.currentActiveJFrame
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import javax.inject.Inject


class InternetConnectionLostEvent: AppEvent()
class InternetConnectionEstablishedEvent: AppEvent()

class NoInternetConnectionException(cause: Throwable) : RuntimeException(cause)

@Logged
open class InternetConnectionController @Inject constructor(
        private val dialogs: Dialogs
) {
    @Subscribe open fun onInternetConnectionLostEvent(event: InternetConnectionLostEvent) {
        dialogs.show(
                title = "Keine Internetverbindung",
                message = "Deine Internet Verbindung ist leider unterbrochen.",
                type = DialogType.ERROR,
                overrideOwner = currentActiveJFrame()
        )
    }
}

class InternetConnectionModule : AbstractModule() {
    override fun configure() {
        bind(InternetConnectionController::class.java).asEagerSingleton()
    }
}
