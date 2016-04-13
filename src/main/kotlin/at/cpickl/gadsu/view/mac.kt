package at.cpickl.gadsu.view

import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


// https://developer.apple.com/legacy/library/samplecode/OSXAdapter/Listings/src_OSXAdapter_java.html
interface MacHandler {
    fun isEnabled(): Boolean

    fun registerAbout(onAbout: () -> Unit)
    fun registerPreferences(onPreferences: () -> Unit)
    fun registerQuit(onQuit: () -> Unit)
}

class DisabledMacHandler : MacHandler {
    override fun isEnabled() = false
    override fun registerAbout(onAbout: () -> Unit) {
        throw UnsupportedOperationException()
    }
    override fun registerPreferences(onPreferences: () -> Unit) {
        throw UnsupportedOperationException()
    }
    override fun registerQuit(onQuit: () -> Unit) {
        throw UnsupportedOperationException()
    }
}

class ReflectiveMacHandler : MacHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    private var macApp: Any
    private val aboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler")
    private val preferencesHandlerClass = Class.forName("com.apple.eawt.PreferencesHandler")
    private val quitHandlerClass = Class.forName("com.apple.eawt.QuitHandler")

    init {
        val macClass = Class.forName("com.apple.eawt.Application")
        val staticFactoryMethod = macClass.getDeclaredMethod("getApplication")
        macApp = staticFactoryMethod.invoke(null)
    }

    override fun isEnabled() = true

    override fun registerAbout(onAbout: () -> Unit) {
        log.debug("registerAbout()")
        val aboutHandler = proxyFor(aboutHandlerClass, "handleAbout", { args -> onAbout.invoke() })
        macAppMethod("setAboutHandler", aboutHandlerClass, aboutHandler)
    }

    override fun registerPreferences(onPreferences: () -> Unit) {
        log.debug("registerPreferences()")
        val preferencesHandler = proxyFor(preferencesHandlerClass, "handlePreferences", { args -> onPreferences.invoke() })
        macAppMethod("setPreferencesHandler", preferencesHandlerClass, preferencesHandler)
    }

    override fun registerQuit(onQuit: () -> Unit) {
        log.debug("registerQuit()")
        // fun handleQuitRequestWith(var1:QuitEvent, var2:QuitResponse)
        val quitHandler = proxyFor(quitHandlerClass, "handleQuitRequestWith", { args ->
            val quitResponse = args[1]
            quitResponse.javaClass.getDeclaredMethod("cancelQuit").invoke(quitResponse)

            onQuit.invoke()
        })
        macAppMethod("setQuitHandler", quitHandlerClass, quitHandler)
    }

    private fun proxyFor(proxyType: Class<*>, methodName: String, callback: (Array<out Any>) -> Unit): Any {
        return Proxy.newProxyInstance(javaClass.getClassLoader(), arrayOf(proxyType), object : InvocationHandler {
            override fun invoke(proxy: Any?, method: Method, args: Array<out Any>): Any? {
                if (method.getName().equals(methodName)) {
                    log.info("{}#{}() invoked on proxy.", proxyType.getName(), methodName)
                    callback.invoke(args)
                    return 1
                }
                log.warn("Unhandled proxy method: {}#{}", proxyType.getName(), methodName)
                return -1
            }
        })
    }

    private fun macAppMethod(methodName: String, parameterTypes: Class<*>, parameter: Any) {
        log.trace("macAppMethod(methodName='{}', ...)", methodName)
        val method = macApp.javaClass.getDeclaredMethod(methodName, parameterTypes)
        method.invoke(macApp, parameter)
    }
}
