package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.clearTime
import org.jdatepicker.impl.JDatePanelImpl
import org.jdatepicker.impl.JDatePickerImpl
import org.jdatepicker.impl.UtilDateModel
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Calendar
import java.util.Date
import java.util.Properties
import javax.swing.JButton
import javax.swing.JFormattedTextField


// https://github.com/JDatePicker/JDatePicker
// http://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component
fun main(args: Array<String>) {
    val datePicker = MyDatePicker.build(null, "btn", "pnl", "txt")
    val btn = JButton("print selected date")
    btn.addActionListener {
        println("selectedDate: " + datePicker.selectedDate())
    }

    Framed.show(arrayOf(datePicker as Component, btn))
}

///**
// * @param navigateToDate defaults to current date
// * @param preselectDate if true sets the textfield to the date (just as would have been selected manually)
// */
//fun SwingFactory.newDatePicker(buttonViewName: String,
//                               panelViewName: String,
//                               textViewName: String,
//                               navigateToDate: DateTime? = null
//) = MyDatePicker.build(navigateToDate ?: clock.now(), buttonViewName, panelViewName, textViewName)

class MyDatePicker(buttonViewName: String,
                   panelViewName: String,
                   textViewName: String,
                   panel: JDatePanelImpl,
                   val model: UtilDateModel,
                   formatter: JFormattedTextField.AbstractFormatter) :
        JDatePickerImpl(panel, formatter) {
    companion object {

        private val log = LoggerFactory.getLogger(MyDatePicker::class.java)
        private val LABELS = Properties()
        init {
            LABELS.setProperty("text.today", "Heute")
            LABELS.setProperty("text.month", "Monat")
            LABELS.setProperty("text.year", "Jahr")
        }

        fun build(initDate: DateTime?, buttonViewName: String, panelViewName: String, textViewName: String): MyDatePicker {
            log.trace("build(initDate={}, ..)", initDate)

            val model = UtilDateModel()
            // joda uses 1-12, java date uses 0-11
            if (initDate != null) {
                model.setDate(initDate.year, initDate.monthOfYear - 1, initDate.dayOfMonth)
                model.isSelected = true // enter date in textfield by default
            }
            val panel = JDatePanelImpl(model, LABELS)
            return MyDatePicker(buttonViewName, panelViewName, textViewName, panel, model, DatePickerFormatter())
        }
    }

    private val hidePopupMethod: () -> Unit
    init {
        name = panelViewName
        val thiz = this

        val implClazz = JDatePickerImpl::class.java
        val fieldRef = implClazz.getDeclaredField("internalEventHandler")
        fieldRef.isAccessible = true
        val field = fieldRef.get(thiz) as ActionListener

        val buttonRef = implClazz.getDeclaredField("button")
        buttonRef.isAccessible = true
        val button = buttonRef.get(thiz) as JButton

        button.name = buttonViewName

        val hidePopupRef = implClazz.getDeclaredMethod("hidePopup")
        hidePopupRef.isAccessible = true
        hidePopupMethod = { hidePopupRef.invoke(thiz) }

        jFormattedTextField.name = textViewName
        jFormattedTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                field.actionPerformed(ActionEvent(button, 0, ""))
            }
        })

    }

    fun changeDate(newValue: DateTime?) {
        log.trace("changeDate(newValue={})", newValue)
        model.value = newValue?.toDate()
    }


    fun selectedDate(): DateTime? {
        if (model.value !is Date) {
            log.trace("Current datepicker value is not a date but: {}", model.value?.javaClass?.name)
            return null
        }

        return DateTime(model.value).clearTime()
    }

    fun hidePopup() {
        log.trace("hidePopup()")
        hidePopupMethod.invoke()
    }

    fun addChangeListener(function: () -> Unit) {
        // so, after the popup opened, and something is selected, it is for sure the formatted textfield value has changed, so rely on that
        jFormattedTextField.addPropertyChangeListener("value", {
            function()
        })
    }
}



class DatePickerFormatter : JFormattedTextField.AbstractFormatter() {

    private val formatter = DateFormats.DATE

    override fun stringToValue(text: String): Any {
        // MINOR JDatePicker seems to support JodaTime (see their website)
        return formatter.parseDateTime(text).toDate()
    }

    override fun valueToString(value: Any?): String {
        if (value is Calendar) {
            return formatter.print(value.time.time)
        }
        if (value === null) {
            return ""
        }
        throw GadsuException("Expected date picker value to be either a GregorianCalender instance or null, but was: $value")
    }
}
