package at.cpickl.gadsu.view.datepicker

import org.jdatepicker.AbstractDateModel
import org.jdatepicker.DateModel
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

/**
 * @param  <T > The type of this model (e.g. java.util.Date, java.util.Calendar)
 */
interface DateModel<T> {

    /**
     * Adds a ChangeListener. ChangeListeners will be notified when the internal
     * state of the control changes. This means that as a user scrolls through
     * dates the internal model changes, which fires a ChangeEvent each time it
     * changes.
     * @param changeListener The changelistener to add.
     */
    fun addChangeListener(changeListener: ChangeListener)

    /**
     * Removes the specified ChangeListener. ChangeListeners will be notified
     * when the selected date is changed.
     * @param changeListener The changelistener to remove.
     */
    fun removeChangeListener(changeListener: ChangeListener)

    /**
     * Getters and setters which represent a gregorian date.
     */
    var year: Int

    /**
     * Getters and setters which represent a gregorian date.
     */
    var month: Int

    /**
     * Getters and setters which represent a gregorian date.
     */
    var day: Int

    /**
     * Getters and setters which represent a gregorian date.
     */
    fun setDate(year: Int, month: Int, day: Int)

    /**
     * Add or substract number of years.
     */
    fun addYear(add: Int)

    /**
     * Add or substract number of months.
     */
    fun addMonth(add: Int)

    /**
     * Add or substract number of day.
     */
    fun addDay(add: Int)

    /**
     * Get the value this model represents.
     */
    var value: T

    /**
     * Set the value as selected.
     * @return Is the value selected or is it not.
     */
    var isSelected: Boolean

    /**
     * Adds a PropertyChangeListener to the list of bean listeners.
     * The listener is registered for all bound properties of the target bean.
     * @param listener The PropertyChangeListener to be added
     * @see .removePropertyChangeListener
     */
    fun addPropertyChangeListener(listener: PropertyChangeListener)


    /**
     * Removes a PropertyChangeListener from the list of bean listeners.
     * This method should be used to remove PropertyChangeListeners that
     * were registered for all bound properties of the target bean.
     * @param listener The PropertyChangeListener to be removed
     * @see .addPropertyChangeListener
     */
    fun removePropertyChangeListener(listener: PropertyChangeListener)

}

abstract class AbstractDateModel<T> protected constructor() : DateModel<T> {

    companion object {
        val PROPERTY_YEAR = "year"
        val PROPERTY_MONTH = "month"
        val PROPERTY_DAY = "day"
        val PROPERTY_VALUE = "value"
        val PROPERTY_SELECTED = "selected"
    }

    private var selected = false
    private var calendarValue = Calendar.getInstance()
    private val changeListeners: MutableSet<ChangeListener> = HashSet()
    private val propertyChangeListeners: MutableSet<PropertyChangeListener> = HashSet()

    @Synchronized override fun addChangeListener(changeListener: ChangeListener) {
        changeListeners.add(changeListener)
    }

    @Synchronized override fun removeChangeListener(changeListener: ChangeListener) {
        changeListeners.remove(changeListener)
    }

    @Synchronized protected fun fireChangeEvent() {
        for (changeListener in changeListeners) {
            changeListener.stateChanged(ChangeEvent(this))
        }
    }

    @Synchronized override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeListeners.add(listener)
    }

    @Synchronized override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        propertyChangeListeners.remove(listener)
    }

    @Synchronized protected fun firePropertyChange(propertyName: String, oldValue: Any?, newValue: Any?) {
        if (oldValue != null && newValue != null && oldValue == newValue) {
            return
        }

        for (listener in propertyChangeListeners) {
            listener.propertyChange(PropertyChangeEvent(this, propertyName, oldValue, newValue))
        }
    }

    override fun getDay(): Int {
        return calendarValue.get(Calendar.DATE)
    }

    override fun getMonth(): Int {
        return calendarValue.get(Calendar.MONTH)
    }

    override fun getYear(): Int {
        return calendarValue.get(Calendar.YEAR)
    }

    override fun getValue(): T? {
        if (!selected) {
            return null
        }
        return fromCalendar(calendarValue)
    }

    override fun setDay(day: Int) {
        val oldDayValue = calendarValue.get(Calendar.DATE)
        val oldValue = value
        calendarValue.set(Calendar.DATE, day)
        fireChangeEvent()
        firePropertyChange(PROPERTY_DAY, oldDayValue, calendarValue.get(Calendar.DATE))
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun addDay(add: Int) {
        val oldDayValue = calendarValue.get(Calendar.DATE)
        val oldValue = value
        calendarValue.add(Calendar.DATE, add)
        fireChangeEvent()
        firePropertyChange(PROPERTY_DAY, oldDayValue, calendarValue.get(Calendar.DATE))
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun setMonth(month: Int) {
        val oldYearValue = calendarValue.get(Calendar.YEAR)
        val oldMonthValue = calendarValue.get(Calendar.MONTH)
        val oldDayValue = calendarValue.get(Calendar.DAY_OF_MONTH)
        val oldValue = value

        val newVal = Calendar.getInstance()
        newVal.set(Calendar.DAY_OF_MONTH, 1)
        newVal.set(Calendar.MONTH, month)
        newVal.set(Calendar.YEAR, oldYearValue)

        if (newVal.getActualMaximum(Calendar.DAY_OF_MONTH) <= oldDayValue) {
            newVal.set(Calendar.DAY_OF_MONTH,
                    newVal.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
            newVal.set(Calendar.DAY_OF_MONTH, oldDayValue)
        }

        calendarValue.set(Calendar.MONTH, newVal.get(Calendar.MONTH))
        calendarValue.set(Calendar.DAY_OF_MONTH, newVal.get(Calendar.DAY_OF_MONTH))

        fireChangeEvent()
        firePropertyChange(PROPERTY_MONTH, oldMonthValue, calendarValue.get(Calendar.MONTH))
        // only fire change event when day actually changed
        if (calendarValue.get(Calendar.DAY_OF_MONTH) != oldDayValue) {
            firePropertyChange(PROPERTY_DAY, oldDayValue,
                    calendarValue.get(Calendar.DAY_OF_MONTH))
        }
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun addMonth(add: Int) {
        val oldMonthValue = calendarValue.get(Calendar.MONTH)
        val oldValue = value
        calendarValue.add(Calendar.MONTH, add)
        fireChangeEvent()
        firePropertyChange(PROPERTY_MONTH, oldMonthValue, calendarValue.get(Calendar.MONTH))
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun setYear(year: Int) {
        val oldYearValue = calendarValue.get(Calendar.YEAR)
        val oldMonthValue = calendarValue.get(Calendar.MONTH)
        val oldDayValue = calendarValue.get(Calendar.DAY_OF_MONTH)
        val oldValue = value

        val newVal = Calendar.getInstance()
        newVal.set(Calendar.DAY_OF_MONTH, 1)
        newVal.set(Calendar.MONTH, oldMonthValue)
        newVal.set(Calendar.YEAR, year)

        if (newVal.getActualMaximum(Calendar.DAY_OF_MONTH) <= oldDayValue) {
            newVal.set(Calendar.DAY_OF_MONTH,
                    newVal.getActualMaximum(Calendar.DAY_OF_MONTH))
        } else {
            newVal.set(Calendar.DAY_OF_MONTH, oldDayValue)
        }

        calendarValue.set(Calendar.YEAR, newVal.get(Calendar.YEAR))
        calendarValue.set(Calendar.DAY_OF_MONTH, newVal.get(Calendar.DAY_OF_MONTH))

        fireChangeEvent()
        firePropertyChange(PROPERTY_YEAR, oldYearValue, calendarValue.get(Calendar.YEAR))
        // only fire change event when day actually changed
        if (calendarValue.get(Calendar.DAY_OF_MONTH) != oldDayValue) {
            firePropertyChange(PROPERTY_DAY, oldDayValue, calendarValue.get(Calendar.DAY_OF_MONTH))
        }
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun addYear(add: Int) {
        val oldYearValue = calendarValue.get(Calendar.YEAR)
        val oldValue = value
        calendarValue.add(Calendar.YEAR, add)
        fireChangeEvent()
        firePropertyChange(PROPERTY_YEAR, oldYearValue, calendarValue.get(Calendar.YEAR))
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun setValue(value: T?) {
        val oldYearValue = calendarValue.get(Calendar.YEAR)
        val oldMonthValue = calendarValue.get(Calendar.MONTH)
        val oldDayValue = calendarValue.get(Calendar.DATE)
        val oldValue = getValue()
        val oldSelectedValue = isSelected

        if (value != null) {
            this.calendarValue = toCalendar(value)
            setToMidnight()
            selected = true
        } else {
            selected = false
        }

        fireChangeEvent()
        firePropertyChange(PROPERTY_YEAR, oldYearValue, calendarValue.get(Calendar.YEAR))
        firePropertyChange(PROPERTY_MONTH, oldMonthValue, calendarValue.get(Calendar.MONTH))
        firePropertyChange(PROPERTY_DAY, oldDayValue, calendarValue.get(Calendar.DATE))
        firePropertyChange(PROPERTY_VALUE, oldValue, getValue())
        firePropertyChange("selected", oldSelectedValue, this.selected)
    }

    override fun setDate(year: Int, month: Int, day: Int) {
        val oldYearValue = calendarValue.get(Calendar.YEAR)
        val oldMonthValue = calendarValue.get(Calendar.MONTH)
        val oldDayValue = calendarValue.get(Calendar.DATE)
        val oldValue = value
        calendarValue.set(year, month, day)
        fireChangeEvent()
        firePropertyChange(PROPERTY_YEAR, oldYearValue, calendarValue.get(Calendar.YEAR))
        firePropertyChange(PROPERTY_MONTH, oldMonthValue, calendarValue.get(Calendar.MONTH))
        firePropertyChange(PROPERTY_DAY, oldDayValue, calendarValue.get(Calendar.DATE))
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
    }

    override fun isSelected(): Boolean {
        return selected
    }

    override fun setSelected(selected: Boolean) {
        val oldValue = value
        val oldSelectedValue = isSelected
        this.selected = selected
        fireChangeEvent()
        firePropertyChange(PROPERTY_VALUE, oldValue, value)
        firePropertyChange(PROPERTY_SELECTED, oldSelectedValue, this.selected)
    }

    private fun setToMidnight() {
        calendarValue.set(Calendar.HOUR, 0)
        calendarValue.set(Calendar.MINUTE, 0)
        calendarValue.set(Calendar.SECOND, 0)
        calendarValue.set(Calendar.MILLISECOND, 0)
    }

    protected abstract fun toCalendar(from: T): Calendar

    protected abstract fun fromCalendar(from: Calendar): T


}


class UtilDateModel constructor(value: Date? = null) : AbstractDateModel<Date>() {
    init {
        setValue(value)
    }

    override fun fromCalendar(from: Calendar) = Date(from.timeInMillis)

    override fun toCalendar(from: Date): Calendar {
        val to = Calendar.getInstance()
        to.time = from
        return to
    }

}

class UtilCalendarModel constructor(value: Calendar? = null) : AbstractDateModel<Calendar>() {
    init {
        setValue(value)
    }

    override fun fromCalendar(from: Calendar) = from.clone() as Calendar
    override fun toCalendar(from: Calendar) = from.clone() as Calendar
}


class SqlDateModel constructor(value: java.sql.Date? = null) : AbstractDateModel<java.sql.Date>() {
    init {
        setValue(value)
    }

    override fun fromCalendar(from: Calendar) = java.sql.Date(from.timeInMillis)

    override fun toCalendar(from: java.sql.Date): Calendar {
        val to = Calendar.getInstance()
        to.time = from
        return to
    }

}