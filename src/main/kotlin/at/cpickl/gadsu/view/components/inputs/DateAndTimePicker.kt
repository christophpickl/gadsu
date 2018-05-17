package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.service.TimeSequence
import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import org.joda.time.DateTime
import javax.swing.JLabel
import javax.swing.JTextField


/**
 * Value is not nullable!
 */
class DateAndTimePicker(
        initialDate: DateTime,
        viewNamePrefix: String,
        textFieldAlignment: Int = JTextField.LEFT,
        timeSequence: TimeSequence = TimeSequence.QUARTER
) : GridPanel() {

    val inpDate = MyDatePicker.build(initialDate, viewNamePrefix, textFieldAlignment)
    val inpTime = MyTimePicker(initialDate, "$viewNamePrefix.Time", timeSequence)

    init {
        inpTime.selectedItemTyped = LabeledDateTime(initialDate)
        inpDate.disableClear() // avoid null values!

        add(inpDate)
        c.gridx++
        add(JLabel(" um "))
        c.gridx++
        add(inpTime)
        c.gridx++
        add(JLabel(" Uhr"))
    }

    fun readDateTime(): DateTime {
        val date = inpDate.selectedDate()!!
        val time = inpTime.selectedItemTyped.delegate
        return date
                .withHourOfDay(time.hourOfDay)
                .withMinuteOfHour(time.minuteOfHour)
                .clearSeconds()
    }

    fun writeDateTime(value: DateTime) {
        inpDate.changeDate(value)
        inpTime.selectedItemTyped = LabeledDateTime(value)
    }
}
