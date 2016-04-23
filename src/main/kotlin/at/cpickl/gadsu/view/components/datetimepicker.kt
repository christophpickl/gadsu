package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.clearSeconds
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.parseDateTimeWithoutSeconds
import org.joda.time.DateTime
import javax.swing.JTextField


/**
 * Value is not nullable!
 */
class DateAndTimePicker(
        modificationChecker: ModificationChecker,
        initialDate: DateTime,
        viewNamePrefix: String
) : GridPanel() {

    private val inpTime = JTextField() // FIXME make own time component; must also react on changes
    val inpDate: MyDatePicker
    init {
        inpDate = modificationChecker.enableChangeListener(MyDatePicker.build(initialDate, viewNamePrefix))
        inpTime.text = initialDate.formatTimeWithoutSeconds()
        inpDate.disableClear()

        add(inpDate)

        c.gridx++
        add(inpTime)
    }

    fun readDateTime(): DateTime? {
        val date = inpDate.selectedDate() ?: return null
        val time = inpTime.text.parseDateTimeWithoutSeconds()
        return date
                .withHourOfDay(time.hourOfDay)
                .withMinuteOfHour(time.minuteOfHour)
                .clearSeconds()
    }
}
