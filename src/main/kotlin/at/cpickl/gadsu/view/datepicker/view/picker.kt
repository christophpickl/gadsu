package at.cpickl.gadsu.view.datepicker.view

import at.cpickl.gadsu.view.datepicker.ComponentColorDefaults
import at.cpickl.gadsu.view.datepicker.ComponentIconDefaults
import at.cpickl.gadsu.view.datepicker.DateSelectionConstraint
import at.cpickl.gadsu.view.datepicker.UtilCalendarModel
import org.jdatepicker.DateModel
import java.awt.*
import java.awt.event.*
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener


class JDatePicker
/**
 * You are able to set the format of the date being displayed on the label.
 * Formatting is described at:

 * @param datePanel The DatePanel to use
 */
private constructor(private val datePanel: JDatePanel) : JComponent(), DatePicker {

    private var popup: Popup? = null
    private val formattedTextField: JFormattedTextField
    private val button: JButton

    /**
     * Create a JDatePicker with a default calendar model.
     */
    constructor() : this(JDatePanel()) {
    }

    /**
     * Create a JDatePicker with an initial value, with a UtilCalendarModel.

     * @param value the initial value
     */
    constructor(value: Calendar) : this(JDatePanel(value)) {
    }

    /**
     * Create a JDatePicker with an initial value, with a UtilDateModel.

     * @param value the initial value
     */
    constructor(value: java.util.Date) : this(JDatePanel(value)) {
    }

    /**
     * Create a JDatePicker with an initial value, with a SqlDateModel.

     * @param value the initial value
     */
    constructor(value: java.sql.Date) : this(JDatePanel(value)) {
    }

    /**
     * Create a JDatePicker with a custom date model.

     * @param model a custom date model
     */
    constructor(model: DateModel<*>) : this(JDatePanel(model)) {
    }


    init {

        //Initialise Variables
        popup = null
        datePanel.setBorder(BorderFactory.createLineBorder(colors.getColor(ComponentColorDefaults.Key.POPUP_BORDER)))
        val internalEventHandler = InternalEventHandler()

        //Create Layout
        val layout = SpringLayout()
        setLayout(layout)

        //Create and Add Components
        //Add and Configure TextField
        formattedTextField = JFormattedTextField(DateComponentFormatter())
        val model = datePanel.model
        setTextFieldValue(formattedTextField, model.year, model.month, model.day, model.isSelected)
        formattedTextField.isEditable = false
        add(formattedTextField)
        layout.putConstraint(SpringLayout.WEST, formattedTextField, 0, SpringLayout.WEST, this)
        layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, formattedTextField)

        //Add and Configure Button
        button = JButton()
        button.isFocusable = true
        val icon = ComponentIconDefaults.popupButtonIcon
        button.icon = icon
        if (icon == null) {
            // reset to caption
            button.text = "..."
        } else {
            // remove text
            button.text = ""
        }
        add(button)
        layout.putConstraint(SpringLayout.WEST, button, 1, SpringLayout.EAST, formattedTextField)
        layout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, button)
        layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, button)

        //Do layout formatting
        val h = button.preferredSize.getHeight().toInt()
        button.preferredSize = Dimension(h, h)
        formattedTextField.preferredSize = Dimension(datePanel.preferredSize.width - h - 1, h)

        //Add event listeners
        addHierarchyBoundsListener(internalEventHandler)
        //TODO        addAncestorListener(listener)
        button.addActionListener(internalEventHandler)
        formattedTextField.addPropertyChangeListener("value", internalEventHandler)
        datePanel.addActionListener(internalEventHandler)
        datePanel.model.addChangeListener(internalEventHandler)
        val eventMask = MouseEvent.MOUSE_PRESSED.toLong()
        Toolkit.getDefaultToolkit().addAWTEventListener(internalEventHandler, eventMask)
    }

    override fun addActionListener(actionListener: ActionListener) {
        datePanel.addActionListener(actionListener)
    }

    override fun removeActionListener(actionListener: ActionListener) {
        datePanel.removeActionListener(actionListener)
    }

    override val model: DateModel<*>
        get() = datePanel.model

    override var isTextEditable: Boolean
        get() = formattedTextField.isEditable
        set(editable) {
            formattedTextField.isEditable = editable
        }

    override var buttonFocusable: Boolean
        get() = button.isFocusable
        set(focusable) {
            button.isFocusable = focusable
        }

    val jDateInstantPanel: DatePanel
        get() = datePanel

    /**
     * Called internally to popup the dates.
     */
    private fun showPopup() {
        if (popup == null) {
            val fac = PopupFactory()
            val xy = locationOnScreen
            datePanel.setVisible(true)
            popup = fac.getPopup(this, datePanel, xy.getX().toInt(), (xy.getY() + this.height).toInt())
            popup!!.show()
        }
    }

    /**
     * Called internally to hide the popup dates.
     */
    private fun hidePopup() {
        if (popup != null) {
            popup!!.hide()
            popup = null
        }
    }

    private fun getAllComponents(component: Component): Set<Component> {
        val children = HashSet<Component>()
        children.add(component)
        if (component is Container) {
            val components = component.components
            for (i in components.indices) {
                children.addAll(getAllComponents(components[i]))
            }
        }
        return children
    }

    override var isDoubleClickAction: Boolean
        get() = datePanel.isDoubleClickAction
        set(doubleClickAction) {
            datePanel.isDoubleClickAction = doubleClickAction
        }

    override var isShowYearButtons: Boolean
        get() = datePanel.isShowYearButtons
        set(showYearButtons) {
            datePanel.isShowYearButtons = showYearButtons
        }

    private fun setTextFieldValue(textField: JFormattedTextField, year: Int, month: Int, day: Int, isSelected: Boolean) {
        if (!isSelected) {
            textField.value = null
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            textField.value = calendar
        }
    }

    override fun addDateSelectionConstraint(constraint: DateSelectionConstraint) {
        datePanel.addDateSelectionConstraint(constraint)
    }

    override fun removeDateSelectionConstraint(constraint: DateSelectionConstraint) {
        datePanel.removeDateSelectionConstraint(constraint)
    }

    override fun removeAllDateSelectionConstraints() {
        datePanel.removeAllDateSelectionConstraints()
    }

    override val dateSelectionConstraints: Set<DateSelectionConstraint>
        get() = datePanel.dateSelectionConstraints

    override var textfieldColumns: Int
        get() = formattedTextField.columns
        set(columns) {
            formattedTextField.columns = columns
        }

    override fun setVisible(aFlag: Boolean) {
        if (!aFlag) {
            hidePopup()
        }
        super.setVisible(aFlag)
    }

    override fun setEnabled(enabled: Boolean) {
        button.isEnabled = enabled
        datePanel.setEnabled(enabled)
        formattedTextField.isEnabled = enabled

        super.setEnabled(enabled)
    }

    /**
     * This internal class hides the public event methods from the outside
     */
    private inner class InternalEventHandler : ActionListener, HierarchyBoundsListener, ChangeListener, PropertyChangeListener, AWTEventListener {

        override fun ancestorMoved(arg0: HierarchyEvent) {
            hidePopup()
        }

        override fun ancestorResized(arg0: HierarchyEvent) {
            hidePopup()
        }

        override fun actionPerformed(arg0: ActionEvent) {
            if (arg0.source === button) {
                if (popup == null) {
                    showPopup()
                } else {
                    hidePopup()
                }
            } else if (arg0.source === datePanel) {
                hidePopup()
            }
        }

        override fun stateChanged(arg0: ChangeEvent) {
            if (arg0.source === datePanel.model) {
                val model = datePanel.model
                setTextFieldValue(formattedTextField, model.year, model.month, model.day, model.isSelected)
            }
        }

        override fun propertyChange(evt: PropertyChangeEvent) {
            // Short circuit if the following cases are found
            if (evt.oldValue == null && evt.newValue == null) {
                return
            }
            if (evt.oldValue != null && evt.oldValue == evt.newValue) {
                return
            }
            if (!formattedTextField.isEditable) {
                return
            }

            // If the field is editable and we need to parse the date entered
            if (evt.newValue != null) {
                val value = evt.newValue as Calendar
                val model = UtilCalendarModel(value)
                // check constraints
                if (!datePanel.checkConstraints(model)) {
                    // rollback
                    formattedTextField.value = evt.oldValue
                    return
                }
                datePanel.model.setDate(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE))
                datePanel.model.isSelected = true
            }

            // Clearing textfield will also fire change event
            if (evt.newValue == null) {
                // Set model value unselected, this will fire an event
                model.isSelected = false
            }
        }

        override fun eventDispatched(event: AWTEvent) {
            if (MouseEvent.MOUSE_CLICKED == event.id && event.source !== button) {
                val components = getAllComponents(datePanel)
                var clickInPopup = false
                for (component in components) {
                    if (event.source === component) {
                        clickInPopup = true
                    }
                }
                if (!clickInPopup) {
                    hidePopup()
                }
            }
        }

    }

    companion object {
        private val colors = ComponentColorDefaults
    }
}
