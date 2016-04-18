package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.parseDateTimeWithoutSeconds
import org.joda.time.DateTime
import javax.swing.JTextField


class DateAndTimePicker(
        modificationChecker: ModificationChecker,
        initialDate: DateTime,
        swing: SwingFactory
) : GridPanel() {

    private val inpTime = JTextField() // FIXME make own time component; must also react on changes
    private val inpDate: MyDatePicker
    init {
        inpDate = modificationChecker.enableChangeListener(swing.newDatePicker(initialDate))
        inpTime.text = initialDate.formatTimeWithoutSeconds()

        add(inpDate)

        c.gridx++
        add(inpTime)
    }

    fun readDateTime(): DateTime {
        val time = inpTime.text.parseDateTimeWithoutSeconds()
        return inpDate.selectedDate()
                .withHourOfDay(time.hourOfDay)
                .withMinuteOfHour(time.minuteOfHour)
                .clearSeconds()
    }
}
fun SwingFactory.newDateAndTimePicker(modificationChecker: ModificationChecker,
                                         initialDate: DateTime): DateAndTimePicker {
    return DateAndTimePicker(modificationChecker, initialDate, this)
}

