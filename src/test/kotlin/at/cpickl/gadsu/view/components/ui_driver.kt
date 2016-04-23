package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.formatDate
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.joda.time.DateTime
import org.uispec4j.Button
import org.uispec4j.TextBox
import org.uispec4j.UISpecTestCase
import org.uispec4j.Window


class DatePickerDriver(private val test: UISpecTestCase, private val window: Window,
                       private val viewNameButton: String, private val viewNamePanel: String, private val viewNameText: String) {

    private val picker: MyDatePicker get() = window.findSwingComponent(MyDatePicker::class.java, viewNamePanel)


    private val formattedTextField: TextBox get() = window.getTextBox(viewNameText)
    private val button: Button get() = window.getButton(viewNameButton)

    fun clickOpenPopupButton() {
        button.click()
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

    fun changeDate(date: DateTime) {
        picker.changeDate(date)
    }
}
