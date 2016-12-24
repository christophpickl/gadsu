package at.cpickl.gadsu.view.datepicker.view

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.IS_OS_WIN
import at.cpickl.gadsu.service.LOGUI
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.parseDate
import at.cpickl.gadsu.service.toDateTime
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.currentActiveJFrame
import at.cpickl.gadsu.view.datepicker.UtilDateModel
import at.cpickl.gadsu.view.logic.beep
import at.cpickl.gadsu.view.swing.ClosableWindow
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.changeBackgroundForASec
import at.cpickl.gadsu.view.swing.emptyBorderForDialogs
import at.cpickl.gadsu.view.swing.registerCloseOnEscape
import at.cpickl.gadsu.view.swing.transparent
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.Date
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFormattedTextField
import javax.swing.JTextField
import javax.swing.SwingUtilities


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
                   panel: JDatePanel,
                   val dateModel: UtilDateModel,
                   formatter: JFormattedTextField.AbstractFormatter,
                   textFieldAlignment: Int = JTextField.LEFT) :
        JDatePicker(panel, formatter) {
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

        fun build(initDate: DateTime?, viewNamePrefix: String, textFieldAlignment: Int = JTextField.LEFT): MyDatePicker {
            LOG.trace("build(initDate={}, ..)", initDate)

            val dateModel = UtilDateModel()
            // joda uses 1-12, java date uses 0-11
            if (initDate != null) {
                dateModel.setDate(initDate.year, initDate.monthOfYear - 1, initDate.dayOfMonth)
                dateModel.isSelected = true // enter date in textfield by default
            }
            val panel = JDatePanel(dateModel)
            panel.name = viewNamePopupPanel(viewNamePrefix)
            return MyDatePicker(viewNamePrefix, panel, dateModel, DatePickerFormatter(), textFieldAlignment)
        }
    }

    private val log = LOGUI(javaClass, viewNamePrefix)

    init {
        name = viewNamePickerPanel(viewNamePrefix)
        transparent()

        button.name = viewNameButton(viewNamePrefix)

        formattedTextField.isFocusable = false
        formattedTextField.columns = if (IS_OS_WIN) 9 else 7
        formattedTextField.horizontalAlignment = textFieldAlignment
        formattedTextField.name = viewNameText(viewNamePrefix)
        formattedTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                log.trace("textfield clicked; displaying dialog to enter date manually via keyboard.")
                SwingUtilities.invokeLater { EnterDateByKeyboardDialog({ dateModel.value = it.toDate() }, dateModel.value?.toDateTime()).isVisible = true }
            }
        })

        addChangeListener {
            log.trace("Value changed to (via textfield text listening): {}", selectedDate())
        }
    }

    fun changeDate(newValue: DateTime?) {
        log.trace("changeDate(newValue={})", newValue)
        dateModel.value = newValue?.toDate()
    }


    fun selectedDate(): DateTime? {
        if (dateModel.value == null) {
            log.trace("Current datepicker value is not a date but: {}", dateModel.value?.javaClass?.name)
            return null
        }
        if (dateModel.value !is Date) {
            throw GadsuException("Expected the datepicker model value to be of type Date, but was: ${dateModel.value?.javaClass?.name}")
        }

        return DateTime(dateModel.value).clearTime()
    }


    fun addChangeListener(function: () -> Unit) {
        // so, after the popup opened, and something is selected, it is for sure the formatted textfield value has changed, so rely on that
        formattedTextField.addPropertyChangeListener("value", {
            function()
        })
    }

    /**
     * Make it non nullable, if initial date was not null from beginning
     */
    fun disableClear() {
        log.trace("disableClear()")
        datePanel.disableClear()
    }
}


private class EnterDateByKeyboardDialog(
        private val onSuccess: (DateTime) -> Unit,
        initialDate: DateTime? = null
) : JDialog(currentActiveJFrame(), "Datum eingeben", true), ClosableWindow {

    private val inpText = JTextField(10)

    init {
        registerCloseOnEscape()
        val panel = GridPanel()
        panel.emptyBorderForDialogs()

        inpText.addActionListener { confirmInput() }
        inpText.toolTipText = "Format: TT.MM.JJJJ"//DateFormats.DATE
        if (initialDate != null) {
            inpText.text = initialDate.formatDate()
        } else {
            inpText.text = "31.12.1985"
            inpText.requestFocus()
            inpText.selectAll()
        }
        val btnOkay = JButton("Okay")
        btnOkay.addActionListener {
            confirmInput()
        }

        with(panel) {
            c.weightx = 1.0
            c.fill = GridBagConstraints.HORIZONTAL
            panel.add(inpText)

            c.weightx = 0.0
            c.fill = GridBagConstraints.NONE
            c.gridx++
            c.insets = Pad.LEFT
            panel.add(btnOkay)
        }

        add(panel)
        pack()
        setLocationRelativeTo(parent)
        isResizable = false
    }

    private fun confirmInput() {
        try {
            val dateEntered = inpText.text.parseDate()
            dispose()
            onSuccess(dateEntered)
        } catch (e: IllegalArgumentException) {
            inpText.changeBackgroundForASec(Color.RED)
            beep()
        }
    }

    override fun closeWindow() {
        dispose()
    }
}
