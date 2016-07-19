package at.cpickl.gadsu.view.datepicker.view

import at.cpickl.gadsu.view.datepicker.ComponentFormatDefaults
import at.cpickl.gadsu.view.datepicker.DateSelectionConstraint
import org.jdatepicker.DateModel
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

class DateComponentFormatter : JFormattedTextField.AbstractFormatter() {

    override fun valueToString(value: Any?): String {
        if (value == null) return ""
        val format = ComponentFormatDefaults.getFormat(ComponentFormatDefaults.Key.SELECTED_DATE_FIELD)
        val cal = value as Calendar
        return format.format(cal.time)
    }

    override fun stringToValue(text: String?): Any? {
        if (text == null || text == "") {
            return null
        }
        val format = ComponentFormatDefaults.getFormat(ComponentFormatDefaults.Key.SELECTED_DATE_FIELD)
        val date = format.parse(text)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }

}
