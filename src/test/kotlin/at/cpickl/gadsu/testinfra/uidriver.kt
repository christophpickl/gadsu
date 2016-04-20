package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.Event
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientDriver
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.treatment.TreatmentDriver
import at.cpickl.gadsu.view.MenuBarDriver
import at.cpickl.gadsu.view.SwingMainWindow
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.uispec4j.Button
import org.uispec4j.ListBox
import org.uispec4j.MenuItem
import org.uispec4j.TextBox
import org.uispec4j.Trigger
import org.uispec4j.UIComponent
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
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

    fun createClientAndTreatment(client: Client, treatment: Treatment) {
        clientDriver.saveNewClient(client)

        treatmentDriver.save(treatment)
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
        val swingWindow = window.awtComponent as SwingMainWindow
        swingWindow.bus.post(event)
    }

}

// --------------------------------------------------------------------------- extension methods

var TextBox.text: String
    get() = getText()
    set(value) {
        setText(value, false)
    }

fun MenuItem.clickAndDisposeDialog(buttonLabelToClick: String, expectedTitle: String? = null) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick(), expectedTitle)
}

fun Button.clickAndDisposeDialog(buttonLabelToClick: String, expectedTitle: String? = null) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick(), expectedTitle)
}

private fun _clickAndDisposeDialog(buttonLabelToClick: String, trigger: Trigger, expectedTitle: String? = null) {
    WindowInterceptor
            .init(trigger)
            .process(object : WindowHandler() {
                override fun process(dialog: Window): Trigger {
                    if (expectedTitle != null) {
                        Assert.assertTrue(dialog.titleEquals(expectedTitle).isTrue,
                                "Expected dialog title to be equals with '$expectedTitle' but was: '${dialog.title}'!")
                    }
                    return dialog.getButton(buttonLabelToClick).triggerClick();
                }
            })
            .run()
}


fun ListBox.deleteAtRow(index: Int) {
    val popup = PopupMenuInterceptor.run(this.triggerRightClick(index))
    val popupMenuItemDelete = popup.getSubMenu("L\u00F6schen")
    popupMenuItemDelete.clickAndDisposeDialog("L\u00F6schen")
}
