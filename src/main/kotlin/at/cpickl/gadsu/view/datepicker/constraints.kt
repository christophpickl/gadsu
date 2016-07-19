package at.cpickl.gadsu.view.datepicker

import org.jdatepicker.DateModel
import java.util.*


/**
 * This interface provides a callback function to limit the selection of a date from the picker and panel.
 */
interface DateSelectionConstraint {

    /**
     * Check the models value to be a valid, selectable date.
     *
     * @param model The model to check
     * @return `true` if the models value is valid, else returns `false`
     */
    fun isValidSelection(model: DateModel<*>): Boolean

}


/**
 * This class provides a simple constraint to limit the selectable date to be inside a given range.
 */
class RangeConstraint : DateSelectionConstraint {

    /**
     * The lower bound of selectable dates.
     */
    private val after: Calendar?

    /**
     * The upper bound of selectable dates.
     */
    private val before: Calendar?

    /**
     * Create a new constraint for values between (and excluding) the given dates.
     * @param after  Lower bound for values, excluding.
     * @param before Upper bound for values, excluding.
     */
    constructor(after: Calendar, before: Calendar) {
        this.after = after
        this.before = before

        // remove hours / minutes / seconds from dates
        cleanTime()
    }

    /**
     * Create a new constraint for values between the given dates.
     * @param after  Lower bound for values, including.
     * @param before Upper bound for values, including.
     */
    constructor(after: Date, before: Date) {
        val _after = Calendar.getInstance()
        val _before = Calendar.getInstance()

        _after.time = after
        _before.time = before

        this.after = _after
        this.before = _before

        cleanTime()
    }

    /**
     * Simple helper method to remove the time (hours, minutes, seconds) the date bounds.
     */
    private fun cleanTime() {
        if (after != null) {
            after.set(Calendar.HOUR_OF_DAY, 0)
            after.set(Calendar.MINUTE, 0)
            after.set(Calendar.SECOND, 0)
            after.set(Calendar.MILLISECOND, 0)
        }

        if (before != null) {
            before.set(Calendar.HOUR_OF_DAY, 23)
            before.set(Calendar.MINUTE, 59)
            before.set(Calendar.SECOND, 59)
            before.set(Calendar.MILLISECOND, 999)
        }
    }

    override fun isValidSelection(model: DateModel<*>): Boolean {
        var result = true

        if (model.isSelected && after != null) {
            result = result and newCalendarWithYearMonthDayOnly(model).after(after)
        }
        if (model.isSelected && before != null) {
            result = result and newCalendarWithYearMonthDayOnly(model).before(before)
        }

        return result
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is RangeConstraint) return false

        if (after != other.after) return false
        if (before != other.before) return false

        return true
    }

    override fun hashCode(): Int{
        var result = after?.hashCode() ?: 0
        result = 31 * result + (before?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String{
        return "RangeConstraint(after=$after, before=$before)"
    }

}


/**
 * This class provides a simple constraint to limit the selectable date to be a
 * workday (Monday - Friday).
 */
class WeekdayConstraint : DateSelectionConstraint {

    override fun isValidSelection(model: DateModel<*>): Boolean {
        if (!model.isSelected) {
            return true
        }
        when (newCalendarWithYearMonthDayOnly(model).get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY -> return true
            else -> return false
        }
    }

}


/**
 * This class provides a simple constraint to limit the selectable date to be a weekend day (Saturday or Sunday).
 */
class WeekendConstraint : DateSelectionConstraint {

    override fun isValidSelection(model: DateModel<*>): Boolean {
        if (!model.isSelected) {
            return true
        }
        when (newCalendarWithYearMonthDayOnly(model).get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY, Calendar.SUNDAY -> return true
            else -> return false
        }
    }

}

private fun newCalendarWithYearMonthDayOnly(model: DateModel<*>) = Calendar.getInstance().apply {
    set(model.year, model.month, model.day)
    set(Calendar.HOUR, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
