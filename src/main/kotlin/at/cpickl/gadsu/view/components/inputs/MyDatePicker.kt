package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.service.DateFormats
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.swing.transparent
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
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField


//<editor-fold desc="main">
// https://github.com/JDatePicker/JDatePicker
// http://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component
fun main(args: Array<String>) {
    val datePicker = MyDatePicker.build(null, "my")
//    datePicker.disableClear()
    val btn = JButton("print selected date")
    btn.addActionListener {
        println("selectedDate: " + datePicker.selectedDate())
    }

    Framed.show(arrayOf(datePicker as Component, btn))
}
//</editor-fold>

///**
// * @param navigateToDate defaults to current date
// * @param preselectDate if true sets the textfield to the date (just as would have been selected manually)
// */
//fun SwingFactory.newDatePicker(buttonViewName: String,
//                               panelViewName: String,
//                               textViewName: String,
//                               navigateToDate: DateTime? = null
//) = MyDatePicker.build(navigateToDate ?: clock.now(), buttonViewName, panelViewName, textViewName)

class MyDatePicker(viewNamePrefix: String,
                   panel: JDatePanelImpl,
                   val model: UtilDateModel,
                   formatter: JFormattedTextField.AbstractFormatter,
                   textFieldAlignment: Int = JTextField.LEFT) :
        JDatePickerImpl(panel, formatter) {
    companion object {

        private val LOG = LoggerFactory.getLogger(MyDatePicker::class.java)

        private val VIEWNAME_BUTTON_SUFFIX = ".OpenButton"
        private val VIEWNAME_TEXTFIELD_SUFFIX = ".TextField"
        private val VIEWNAME_PICKER_PANEL_SUFFIX = ".PickerPanel"
        private val VIEWNAME_POPUP_PANEL_SUFFIX = ".PopupPanel"

        fun viewNameButton(prefix: String) = prefix + VIEWNAME_BUTTON_SUFFIX
        fun viewNameText(prefix: String) = prefix + VIEWNAME_TEXTFIELD_SUFFIX
        fun viewNamePickerPanel(prefix: String) = prefix + VIEWNAME_PICKER_PANEL_SUFFIX
        fun viewNamePopupPanel(prefix: String) = prefix + VIEWNAME_POPUP_PANEL_SUFFIX

        private val LABELS = Properties()
        init {
            LABELS.setProperty("text.today", "Heute")
            LABELS.setProperty("text.month", "Monat")
            LABELS.setProperty("text.year", "Jahr")
        }

        fun build(initDate: DateTime?, viewNamePrefix: String, textFieldAlignment: Int = JTextField.LEFT): MyDatePicker {
            LOG.trace("build(initDate={}, ..)", initDate)

            val model = UtilDateModel()
            // joda uses 1-12, java date uses 0-11
            if (initDate != null) {
                model.setDate(initDate.year, initDate.monthOfYear - 1, initDate.dayOfMonth)
                model.isSelected = true // enter date in textfield by default
            }
            val panel = JDatePanelImpl(model, LABELS)
            panel.name = viewNamePopupPanel(viewNamePrefix)
            return MyDatePicker(viewNamePrefix, panel, model, DatePickerFormatter(), textFieldAlignment)
        }
    }

    private val log = LoggerFactory.getLogger(javaClass.name + "#" + viewNamePrefix)
    private val hidePopupMethod: () -> Unit
    private val disableClearFuntion: () -> Unit

    init {
        name = viewNamePickerPanel(viewNamePrefix)
        transparent()
        val thiz = this

        val pickerClass = JDatePickerImpl::class.java
        val pickerClassName = pickerClass.name
        val panelClass = JDatePanelImpl::class.java
        val panelClassName = panelClass.name

        val dateTextField = reflectivelyGetFieldAs<JFormattedTextField>(pickerClassName, thiz, "formattedTextField")
        dateTextField.columns = if (IS_OS_WIN) 8 else 6
        dateTextField.horizontalAlignment = textFieldAlignment

        val eventHandler = reflectivelyGetFieldAs<ActionListener>(pickerClassName, thiz, "internalEventHandler")

        val button = reflectivelyGetFieldAs<JButton>(pickerClassName, thiz, "button")
        button.name = viewNameButton(viewNamePrefix)

        val hidePopupRef = pickerClass.getDeclaredMethod("hidePopup")
        hidePopupRef.isAccessible = true
        hidePopupMethod = { hidePopupRef.invoke(thiz) }

        jFormattedTextField.name = viewNameText(viewNamePrefix)
        jFormattedTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                eventHandler.actionPerformed(ActionEvent(button, 0, ""))
            }
        })

        val internalViewClassname = "org.jdatepicker.impl.JDatePanelImpl\$InternalView"
        val internalView = reflectivelyGetFieldAs<JPanel>(panelClassName, panel, "internalView")
        val southPanel = reflectivelyGetFieldAs<JPanel>(internalViewClassname, internalView, "southPanel")
        val noneLabel = reflectivelyGetFieldAs<JLabel>(internalViewClassname, internalView, "noneLabel")
        disableClearFuntion = { southPanel.remove(noneLabel) }

        addChangeListener {
            log.trace("Value changed to (via textfield text listening): {}", selectedDate())
        }

    }

    private fun <T> reflectivelyGetFieldAs(providerType: String, providerObject: Any, fieldName: String): T {
        val providerClass = Class.forName(providerType)
//        println("Reflectively get field '${fieldName}' for: ${providerClass.name}\n" +
//                providerClass.declaredFields.map { "${it.name}: ${it.type.simpleName}" }
//                .joinToString(separator = "\n", prefix = "  - "))
        val fieldRef = providerClass.getDeclaredField(fieldName)
        fieldRef.isAccessible = true
        val obj = fieldRef.get(providerObject)
        @Suppress("UNCHECKED_CAST")
        return obj as T
    }

    fun changeDate(newValue: DateTime?) {
        log.trace("changeDate(newValue={})", newValue)
        model.value = newValue?.toDate()
    }


    fun selectedDate(): DateTime? {
        if (model.value == null) {
            log.trace("Current datepicker value is not a date but: {}", model.value?.javaClass?.name)
            return null
        }
        if (model.value !is Date) {
            throw GadsuException("Expected the datepicker model value to be of type Date, but was: ${model.value.javaClass.name}")
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

    /**
     * Make it non nullable, if initial date was not null from beginning
     */
    fun disableClear() {
        log.trace("disableClear()")
        disableClearFuntion.invoke()
    }
}



class DatePickerFormatter : JFormattedTextField.AbstractFormatter() {

    private val formatter = DateFormats.DATE

    override fun stringToValue(text: String): Any {
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
