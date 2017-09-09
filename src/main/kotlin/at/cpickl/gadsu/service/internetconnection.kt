package at.cpickl.gadsu.service

import at.cpickl.gadsu.global.AppEvent
import at.cpickl.gadsu.global.GADSU_LATEST_VERSION_URL
import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.view.AsyncDialogSettings
import at.cpickl.gadsu.view.AsyncWorker
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import at.cpickl.gadsu.view.currentActiveJFrame
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.common.io.Resources
import com.google.inject.AbstractModule
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject


class InternetConnectionLostEvent: AppEvent()
class InternetConnectionEstablishedEvent: AppEvent()
class InternetConnectionStateChangedEvent(val isConnected: Boolean): AppEvent()

class ReconnectInternetConnectionEvent : UserEvent()

class NoInternetConnectionException(cause: Throwable? = null) : RuntimeException(cause)

interface InternetConnectionController {
    val isConnected: Boolean
}

@Logged
open class InternetConnectionControllerImpl @Inject constructor(
        private val bus: EventBus,
        private val asyncWorker: AsyncWorker,
        private val dialogs: Dialogs
) : InternetConnectionController {
    companion object {
        private val HOST_TO_CHECK = "www.google.com"
        private val TIMEOUT_IN_MS = 4000
    }

    private val log = LOG(javaClass)
    private var _isConnected = true
    override val isConnected: Boolean get() = _isConnected

    @Subscribe open fun onInternetConnectionLostEvent(event: InternetConnectionLostEvent) {
        dialogs.show(
                title = "Keine Internetverbindung",
                message = "Deine Internet Verbindung ist leider unterbrochen.",
                type = DialogType.ERROR,
                overrideOwner = currentActiveJFrame()
        )
        _isConnected = false
        bus.post(InternetConnectionStateChangedEvent(_isConnected))
    }

    @Subscribe open fun onInternetConnectionEstablishedEvent(event: InternetConnectionEstablishedEvent) {
        _isConnected = true
        bus.post(InternetConnectionStateChangedEvent(_isConnected))
    }

    private fun checkIsConnected(): Boolean {
//        try {
//            log.info("Checking for host: {}", HOST_TO_CHECK)
//            val connected = InetAddress.getByName(HOST_TO_CHECK).isReachable(TIMEOUT_IN_MS)
//            if (!connected) log.info("Host was not reachable.")
//            return connected
//        } catch(e: UnknownHostException) {
//            log.debug("Got an UnknownHostException.")
//            return false
//        }
        try {
            // just test some URL which should be there
            Resources.toString(GADSU_LATEST_VERSION_URL, Charsets.UTF_8).trim()
            return true
        } catch (e: UnknownHostException) {
            return false
        } catch (e: SocketException) {
            return false
        }
    }

    @Subscribe open fun onReconnectInternetConnectionEvent(event: ReconnectInternetConnectionEvent) {
        if (_isConnected == true) {
            throw IllegalStateException("Already connected!")
        }

        asyncWorker.doInBackground(settings = AsyncDialogSettings(
                title = "Bitte Warten",
                message = "Pr\u00fcfe die Internet Verbindung"
        ), backgroundTask =  {
            checkIsConnected()
        }, doneTask = { connectedResult ->
            onIsConnected(connectedResult!!)
        }, exceptionTask = { e -> throw e })
    }

    private fun onIsConnected(connectedResult: Boolean) {
        if (connectedResult) {
            bus.post(InternetConnectionEstablishedEvent())
            dialogs.show(
                    title = "Internetverbindung",
                    message = "Die Internet Verbindung wurde wieder hergestellt.",
                    type = DialogType.INFO,
                    overrideOwner = currentActiveJFrame()
            )
        } else {
            dialogs.show(
                    title = "Internetverbindung",
                    message = "Die Internet Verbindung ist nach wie vor unterbrochen.",
                    type = DialogType.WARN,
                    overrideOwner = currentActiveJFrame()
            )
        }
    }

}

class InternetConnectionModule : AbstractModule() {
    override fun configure() {
        bind(InternetConnectionController::class.java).to(InternetConnectionControllerImpl::class.java).asEagerSingleton()
    }
}
