package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.timesLabeledList
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

class MyTimePicker(initValue: DateTime) : MyComboBox<LabeledDateTime>(timesLabeledList(), LabeledDateTime(initValue)) {
    init {
        maximumRowCount = 20
    }
}
