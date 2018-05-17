package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.global.Labeled
import at.cpickl.gadsu.service.TimeSequence
import at.cpickl.gadsu.service.ensureNoSeconds
import at.cpickl.gadsu.service.ensureQuarterMinute
import at.cpickl.gadsu.service.equalsHoursAndMinute
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import org.joda.time.DateTime


class LabeledDateTime(val delegate: DateTime) : Labeled {
    override val label: String = delegate.formatTimeWithoutSeconds()

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is LabeledDateTime) return false
        if (label != other.label) return false
        return true
    }
    override fun hashCode(): Int{
        return label.hashCode()
    }

    override fun toString(): String {
        return "LabeledDateTime(label='$label')"
    }

}

class MyTimePicker(
        initValue: DateTime,
        viewName: String,
        private val timeSequence: TimeSequence = TimeSequence.QUARTER
) : MyComboBox<LabeledDateTime>(timeSequence.timesLabeled, LabeledDateTime(initValue)) {
    init {
        name = viewName
        maximumRowCount = 20

        timeSequence.ensureCompatible(initValue)
        initValue.ensureNoSeconds()
    }

    fun changeSelectedByDateTime(value: DateTime?) {
        if (value == null) {
            selectedItem = null
            return
        }
        timeSequence.ensureCompatible(value)

        (0 until model.size).forEach {
            val currentDate = model.getElementAt(it)
            if (currentDate.delegate.equalsHoursAndMinute(value)) {
                selectedItem = currentDate
                return
            }
        }
        throw GadsuException("Unable to preselect date: $value")
    }
}

