package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.parseTimeWithoutSeconds
import org.joda.time.DateTime


fun main(args: Array<String>) {
    println(timesList().map { it.formatTimeWithoutSeconds() }.joinToString("\n"))
}

private fun timesList(): List<DateTime> {
    var current = "00:00".parseTimeWithoutSeconds()
    return 0.rangeTo(24 * 4 - 1).map {
        val result = current
        current = current.plusMinutes(15)
        result
    }
}

private fun mapThisShit(): List<LabeledDateTime> {
    return timesList().map { LabeledDateTime(it) }.toList()
}

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

class MyTimePicker(initValue: DateTime) : MyComboBox<LabeledDateTime>(mapThisShit(), LabeledDateTime(initValue)) {
    init {
        maximumRowCount = 20
    }
}
