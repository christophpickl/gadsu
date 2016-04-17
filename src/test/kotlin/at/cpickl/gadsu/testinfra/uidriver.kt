package at.cpickl.gadsu.testinfra

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.slf4j.LoggerFactory
import org.uispec4j.Button
import org.uispec4j.MenuItem
import org.uispec4j.TextBox
import org.uispec4j.Trigger
import org.uispec4j.UIComponent
import org.uispec4j.Window
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor


class MainDriver(test: UiTest, window: Window) : BaseDriver(test, window) {
    val log = LoggerFactory.getLogger(javaClass)
    // via extension methods
}

abstract class BaseDriver(val test: UiTest, val window: Window) {

    fun assertHasFocus(component: UIComponent, hasFocus: Boolean = true) {
        assertThat("Expected component '${component}' to " + (if(!hasFocus) "not " else "") + "have focus!",
                component.awtComponent.hasFocus(), equalTo(hasFocus))
    }

}

var TextBox.text: String
    get() = getText()
    set(value) {
        setText(value, false)
    }

fun MenuItem.clickAndDisposeDialog(buttonLabelToClick: String) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick())
}

fun Button.clickAndDisposeDialog(buttonLabelToClick: String) {
    _clickAndDisposeDialog(buttonLabelToClick, triggerClick())
}

private fun _clickAndDisposeDialog(buttonLabelToClick: String, trigger: Trigger) {
    WindowInterceptor
            .init(trigger)
            .process(object : WindowHandler() {
                override fun process(dialog: Window): Trigger {
                    return dialog.getButton(buttonLabelToClick).triggerClick();
                }
            })
            .run()
}
