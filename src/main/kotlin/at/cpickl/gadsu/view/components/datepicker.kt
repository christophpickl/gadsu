package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.RealClock
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.packCenterAndShow
import org.jdatepicker.DateModel
import org.jdatepicker.impl.JDatePanelImpl
import org.jdatepicker.impl.JDatePickerImpl
import org.jdatepicker.impl.UtilDateModel
import org.joda.time.DateTime
import java.awt.BorderLayout
import java.util.*
import javax.swing.JButton
import javax.swing.JFormattedTextField
import javax.swing.JFrame
import javax.swing.WindowConstants


// https://sourceforge.net/projects/jdatepicker/
// http://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component

fun main(args: Array<String>) {
    val datePicker = SwingFactory(RealClock()).newDatePicker()

    val btn = JButton("print selected date")
    btn.addActionListener {
        println("selectedDate: " + datePicker.selectedDate())
    }

    val frame = JFrame()
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.contentPane.layout = BorderLayout()
    frame.contentPane.add(datePicker, BorderLayout.CENTER)
    frame.contentPane.add(btn, BorderLayout.SOUTH)
    frame.packCenterAndShow()
}

/**
 * @param navigateToDate defaults to current date
 * @param preselectDate if true sets the textfield to the date (just as would have been selected manually)
 */
fun SwingFactory.newDatePicker(navigateToDate: DateTime? = null, preselectDate: Boolean = true): MyDatePicker {
    val model = UtilDateModel()
    val date = navigateToDate ?: clock.now()
    // joda uses 1-12, java date uses 0-11
    model.setDate(date.year, date.monthOfYear - 1, date.dayOfMonth)
    model.setSelected(preselectDate)
    return MyDatePicker(model)
}

class MyDatePicker(model: DateModel<Date>) : JDatePickerImpl(JDatePanelImpl(model, LABELS), DatePickerFormatter()) {
    companion object {
        private val LABELS = Properties()
        init {
            LABELS.setProperty("text.today", "Heute")
            LABELS.setProperty("text.month", "Monat")
            LABELS.setProperty("text.year", "Jahr")
        }
    }
    fun selectedDate(): DateTime? {
        if (model.value == null) {
            return null
        }
        val selectedDate = model.value as Date
        return DateTime(selectedDate)
    }
}

class DatePickerFormatter : JFormattedTextField.AbstractFormatter() {
    private val formatter = DateFormats.DATE
    override fun stringToValue(text: String?): Any? {
        return formatter.parseDateTime(text).toDate()
    }
    override fun valueToString(value: Any?): String? {
        if (value is Calendar) {
            return formatter.print(value.time.time)
        }
        if (value == null) {
            return ""
        }
        throw UnsupportedOperationException("Expected date picker value to be either a GregorianCalender instance or null, but was: $value")
    }
}