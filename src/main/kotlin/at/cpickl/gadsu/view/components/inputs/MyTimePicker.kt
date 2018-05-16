package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.global.Labeled
import at.cpickl.gadsu.service.ensureNoSeconds
import at.cpickl.gadsu.service.ensureQuarterMinute
import at.cpickl.gadsu.service.equalsHoursAndMinute
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.timesLabeledListQuarter
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

class MyTimePicker(initValue: DateTime, viewName: String) : MyComboBox<LabeledDateTime>(timesLabeledListQuarter, LabeledDateTime(initValue)) {
    init {
        name = viewName
        maximumRowCount = 20

        initValue.ensureQuarterMinute()
        initValue.ensureNoSeconds()
    }

    fun changeSelectedByDateTime(value: DateTime?) {
        if (value == null) {
            selectedItem = null
            return
        }
        value.ensureQuarterMinute()
        value.ensureNoSeconds()

        0.rangeTo(model.size - 1).forEach {
            val currentDate = model.getElementAt(it)
            if (currentDate.delegate.equalsHoursAndMinute(value)) {
                selectedItem = currentDate
                return
            }
        }
        throw GadsuException("Unable to preselect date: $value")
    }
}

