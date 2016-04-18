package at.cpickl.gadsu.view

import at.cpickl.gadsu.view.components.SwingFactory
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.slf4j.LoggerFactory

class ViewModule : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.debug("configure()")


        bind(SwingFactory::class.java)

        // main window
        bind(MainWindow::class.java).to(SwingMainWindow::class.java).`in`(Scopes.SINGLETON)
        bind(MainWindowController::class.java).asEagerSingleton()

        // mac handling
        val isMacApp = System.getProperty("gadsu.isMacApp", "").equals("true")
        log.debug("isMacApp={}", isMacApp)
        bind(MacHandler::class.java).toInstance(if (isMacApp) ReflectiveMacHandler() else DisabledMacHandler() )

        // menu bar
        bind(GadsuMenuBar::class.java).asEagerSingleton()
        bind(GadsuMenuBarController::class.java).asEagerSingleton()

        // about
        install(AboutModule())
    }

}