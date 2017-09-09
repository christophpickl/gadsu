package at.cpickl.gadsu.view.datepicker.view

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.view.datepicker.DateModel
import at.cpickl.gadsu.view.datepicker.DateSelectionConstraint
import java.awt.event.ActionListener
import java.util.*
import javax.swing.JFormattedTextField


/**
 * This interface is implemented by all components which represent a date by day
 * granularity. T will be one of the following org.joda.time.DateMidnight,
 * java.util.Date or java.util.Calendar.
 *
 * Since the first version of JDatePicker generics was added to Java and
 * JodaTime emerged as a important date handling library in the Java community.
 */
interface DateComponent {

    /**
     * Returns the value of the currently represented date in the component.
     * Depending on the version of the library used this type will one of the
     * following:
     * - java.util.Calendar
     * - org.joda.time.DateMidnight
     * - java.util.Date
     *
     * @return A new Model
     */
    val model: DateModel<*>

    /**
     * Adds an ActionListener. The actionListener is notified when a user clicks
     * on a date. Deliberately selecting a date will trigger this event, not
     * scrolling which fires a ChangeEvent for ChangeListeners.

     * @param actionListener The listener to add
     */
    fun addActionListener(actionListener: ActionListener)

    /**
     * Removes the ActionListener. The actionListener is notified when a user clicks on a date.
     *
     * @param actionListener The listener to remove
     */
    fun removeActionListener(actionListener: ActionListener)


    fun addDateSelectionConstraint(constraint: DateSelectionConstraint)

    fun removeDateSelectionConstraint(constraint: DateSelectionConstraint)

    fun removeAllDateSelectionConstraints()

    /**
     * Get all registered date selection constraints.
     *
     * @return An immutable Set of all constraints.
     */
    val dateSelectionConstraints: Set<DateSelectionConstraint>

}


interface DatePanel : DateComponent {

    /**
     * Sets the visibilty of the Year navigation buttons. Defaults to false.
     */
    var isShowYearButtons: Boolean

    /**
     * Is a double click required to fire a ActionEvent.
     * This changes the behaviour of the control to require a double click on
     * actionable clicks. If this is set the ActionEvent will only be fired
     * when double clicked on a date. Defaults to false.
     */
    var isDoubleClickAction: Boolean

}


interface DatePicker : DatePanel {

    /**
     * Is the text component editable or not. Defaults to false.
     */
    var isTextEditable: Boolean

    /**
     * Sets the button to be focusable. Defaults to true.
     */
    var buttonFocusable: Boolean

    /**
     * Sets the size of the underlying textfield in columns
     */
    var textfieldColumns: Int

}

class DatePickerFormatter : JFormattedTextField.AbstractFormatter() {

    private val formatter = DateFormats.DATE

    override fun valueToString(value: Any?): String {
        if (value is Calendar) {
            return formatter.print(value.time.time)
        }
        if (value === null) {
            return ""
        }
        throw GadsuException("Expected date picker value to be either a GregorianCalender instance or null, but was: $value")
    }

    override fun stringToValue(text: String): Any {
        return formatter.parseDateTime(text).toDate()
    }
}
