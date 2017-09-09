package at.cpickl.gadsu.testinfra.ui

import at.cpickl.gadsu.global.Event
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientDriver
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentDriver
import at.cpickl.gadsu.view.MenuBarDriver
import at.cpickl.gadsu.view.SwingMainFrame
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.slf4j.LoggerFactory
import org.uispec4j.Trigger
import org.uispec4j.UIComponent
import org.uispec4j.Window
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor


class MainDriver(
        test: UiTest,
        window: Window,
        val menuBarDriver: MenuBarDriver,
        val clientDriver: ClientDriver,
        val treatmentDriver: TreatmentDriver
        ) : BaseDriver(test, window) {

    val log = LoggerFactory.getLogger(javaClass)

    fun createClientAndTreatment(client: Client, treatment: Treatment, returnToClientView: Boolean = false) {
        clientDriver.saveBasicClient(client)

        treatmentDriver.save(treatment, returnToClientView)
    }

    fun openPreferencesWindow(): Window {
        log.debug("openPreferencesWindow()")
        val window = WindowInterceptor.run(menuBarDriver.menuItemPreferences.triggerClick())
        MatcherAssert.assertThat(window.getName(), Matchers.equalTo(at.cpickl.gadsu.view.ViewNames.Preferences.Window))
        return window
    }


    // and some via extension methods
}

abstract class BaseDriver(val test: UiTest, val window: Window) {

    fun triggerDialogAndClick(functionToOpenDialog: () -> Unit, buttonLabelToClick: String) {
        WindowInterceptor
                .init({ functionToOpenDialog() })
                .process(object : WindowHandler() {
                    override fun process(dialog: Window): Trigger {
                        return dialog.getButton(buttonLabelToClick).triggerClick();
                    }
                })
                .run()
    }

    fun assertHasFocus(component: UIComponent, hasFocus: Boolean = true) {
        Thread.sleep(500)
        assertThat("Expected component '${component}' to " + (if(!hasFocus) "not " else "") + "have focus!",
                component.awtComponent.hasFocus(), equalTo(hasFocus))
    }

    fun postEvent(event: Event) {
        val swingWindow = window.awtComponent as SwingMainFrame
        swingWindow.bus.post(event)
    }

}
