package at.cpickl.gadsu.testinfra.ui

import at.cpickl.gadsu.global.IS_OS_MAC
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.view.UiTestEnabler
import at.cpickl.gadsu.view.components.RichFormat
import at.cpickl.gadsu.view.components.RichTextArea
import at.cpickl.gadsu.view.datepicker.view.JDatePanel
import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import at.cpickl.gadsu.view.logic.consumeBeep
import non_test.Framed
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test
import org.uispec4j.Button
import org.uispec4j.Key
import org.uispec4j.ListBox
import org.uispec4j.MenuItem
import org.uispec4j.TextBox
import org.uispec4j.Trigger
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
import org.uispec4j.interception.WindowHandler
import org.uispec4j.interception.WindowInterceptor
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JWindow

class TestContainer : JPanel(BorderLayout()) {

    companion object {
        val viewName = "containerPanel"
    }

    init {
        name = viewName
    }

    fun setView(subPanel: JPanel) {
        removeAll()
        add(subPanel, BorderLayout.CENTER)
        revalidate()
        repaint()
    }
}

object TestContainerStarter {

    @JvmStatic
    fun main(cliArgs: Array<String>) {
        Framed.showWithContextDefaultSize {
            TestContainer()
        }
    }

}


@Test class TestUiSysPropertySetter {
    @BeforeSuite
    fun setSysProperty() {
        UiTestEnabler.enable()
    }
}

class RichTextAreaAsserter(
        private val textArea: RichTextArea,
        private val test: SimpleUiTest,
        private val window: Window,
        private val viewName: String
) {
    private val textBox: TextBox get() = window.getTextBox(viewName)

    fun enterText(someText: String): RichTextAreaAsserter {
        textBox.setText(someText, false)
        return this
    }

    fun appendAtEnd(text: String): RichTextAreaAsserter {
        textBox.appendText(text)
        return this
    }

    fun selectAll() {
        textArea.selectAll()
    }

    fun select(start: Int, length: Int): RichTextAreaAsserter {
        textArea.select(start, start + length)
//        println("selected: [${textArea.selectedText}]")
        return this
    }

    fun hitShortcut(shortcutKey: Char): RichTextAreaAsserter {
        textBox.typeKey(osSpecificShortcut(shortcutKey))
        return this
    }

    fun hitShortcut(format: RichFormat) = hitShortcut(format.shortcutKey)

    fun assertPlainTextEquals(expectedText: String) {
        assertThat(textArea.text, equalTo(expectedText))
    }

    fun assertHasBeeped() {
        assertThat(consumeBeep(), equalTo(true))
    }

    fun assertEnrichedTextEquals(expectedEnrichedText: String) {
        assertThat(textArea.toEnrichedText(), equalTo(expectedEnrichedText))
    }

    private fun osSpecificShortcut(char: Char) = addMetaOrControl(Key.get(char))
    private fun addMetaOrControl(charKey: Key) = if (IS_OS_MAC) Key.meta(charKey) else Key.control(charKey)

}

class DateTimeSpecPicker(test: UiTest,
                         window: Window,
                         viewNamePrefix: String
) : DateSpecPicker(test, window, viewNamePrefix) {
}

class DatePickerPopupContext(
        private val test: SimpleUiTest,
        private val popup: Window,
        private val jWindow: JWindow,
        private val jDatePanel: JDatePanel) {
    fun assertPopupVisible(expected: Boolean) {
        if (expected) {
            test.assertThat(popup.isVisible)
            Assert.assertTrue(jWindow.isVisible, "Expected the treatment date picker popup to be invisible!")
        } else {
            Assert.assertFalse(jWindow.isVisible, "Expected the treatment date picker popup to be invisible!")
        }
    }

}

open class DateSpecPicker(private val test: SimpleUiTest,
                          private val window: Window,
                          private val viewNamePrefix: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val popupPanelName = MyDatePicker.viewNamePopupPanel(viewNamePrefix)
    private val pickerPanelName = MyDatePicker.viewNamePickerPanel(viewNamePrefix)
    private val buttonName = MyDatePicker.viewNameButton(viewNamePrefix)

    private val picker: MyDatePicker get() = window.findSwingComponent(MyDatePicker::class.java, pickerPanelName)
    private val openButton: Button get() = window.getButton(buttonName)
    private val formattedTextField: TextBox get() = window.getTextBox(MyDatePicker.viewNameText(viewNamePrefix))

    fun openPopupByButton(function: (DatePickerPopupContext) -> Unit) {
        openPopupByButton { window, jWindow, jDatePanelImpl ->
            function(DatePickerPopupContext(test, window, jWindow, jDatePanelImpl))
        }
    }
    fun openPopupByButton(function: (Window, JWindow, JDatePanel) -> Unit) {
        log.debug("openPopupByButton(function), button.name={}", openButton.name)
        WindowInterceptor
                .init(openButton.triggerClick())
                .process(object : WindowHandler() {
                    override fun process(dialog: Window): Trigger {
                        log.trace("process(dialog) date picker popup")
                        val popupContentRaw = (dialog.awtComponent as JWindow).rootPane.contentPane.getComponent(0)
                        if (popupContentRaw !is JDatePanel) {
                            throw AssertionError("Expected popup's content to be a JDatePanelImpl, but was: ${popupContentRaw.javaClass.name} ($popupContentRaw)")
                        }
                        MatcherAssert.assertThat(popupContentRaw.name, Matchers.equalTo(popupPanelName))
                        function(dialog, dialog.awtComponent as JWindow, popupContentRaw)
                        return org.uispec4j.Trigger.DO_NOTHING
                    }
                })
                .run()

    }

    fun assertSelected(expected: DateTime) {
        test.assertThat(formattedTextField.textEquals(expected.formatDate()))

        MatcherAssert.assertThat(picker.selectedDate(), Matchers.allOf(Matchers.notNullValue(), Matchers.equalTo(expected)))
        MatcherAssert.assertThat(picker.model.isSelected, Matchers.equalTo(true))
    }

    fun assertNothingSelected() {
        test.assertThat(formattedTextField.textIsEmpty())

        MatcherAssert.assertThat(picker.selectedDate(), Matchers.nullValue())
        MatcherAssert.assertThat(picker.model.isSelected, Matchers.equalTo(false))
    }

    fun changeDate(date: DateTime?) {
        picker.changeDate(date)
    }

    fun assertMaybeSelected(date: DateTime?){
        if (date == null) {
            assertNothingSelected()
        } else {
            assertSelected(date)
        }
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


fun ListBox.deleteAtRow(index: Int, menuToClick: String = "L\u00F6schen") {
    val popup = PopupMenuInterceptor.run(this.triggerRightClick(index))
    val popupMenuItemDelete = popup.getSubMenu(menuToClick)
    popupMenuItemDelete.clickAndDisposeDialog("L\u00F6schen")
}

