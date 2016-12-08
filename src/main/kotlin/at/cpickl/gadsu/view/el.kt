package at.cpickl.gadsu.view

import at.cpickl.gadsu.client.xprops.view.ElFieldForProps
import at.cpickl.gadsu.service.withAllButHourAndMinute
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.RichTextArea
import at.cpickl.gadsu.view.components.inputs.DateAndTimePicker
import at.cpickl.gadsu.view.components.inputs.Labeled
import at.cpickl.gadsu.view.components.inputs.MyCheckBox
import at.cpickl.gadsu.view.components.inputs.MyComboBox
import at.cpickl.gadsu.view.components.inputs.MyTimePicker
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import at.cpickl.gadsu.view.logic.MAX_FIELDLENGTH_SHORT
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.enforceMaxCharacters
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.base.MoreObjects
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.Component
import java.util.LinkedList
import java.util.Objects
import javax.swing.JTextField


private val LOG_ElFIeld = LoggerFactory.getLogger(ElField::class.java)

interface ElField<in V> {
    val formLabel: String
    fun isModified(value: V): Boolean
    fun updateValue(value: V)
    fun toComponent(): Component
}

private fun <V, T> ElField<V>._isModified(uiValue: T, extractValue: (V) -> T, value: V): Boolean {
    val extractedValue = extractValue(value)
    val modified = !Objects.equals(extractedValue, uiValue)
    if (modified) {
        LOG_ElFIeld.trace("Changes detected for form item '{}'! UI value='{}', extracted object value='{}' ({})", formLabel, uiValue, extractedValue, this)
    }
    return modified
}

class ElTextField<V>(
        override val formLabel: String,
        private val extractValue: (V) -> String,
        viewName: String
// as we use those in the grid panel only, setting columns to 100 is necessary to evenly distribute the horizontal length in multi column form panels!
) : JTextField(20), ElField<V> {

    init {
        name = viewName
        enforceMaxCharacters(MAX_FIELDLENGTH_SHORT)
    }

    override fun isModified(value: V) = _isModified(text, extractValue, value)
//    override fun isModified(value: V): Boolean {
//        val mod = _isModified(text, extractValue, value)
//        if (mod) {
//            println("Change detected")
//        }
//        return mod
//    }
    override fun updateValue(value: V) { text = extractValue(value) }
    override fun toComponent() = this

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("formLabel", formLabel)
            .add("text", text)
            .toString()
}

class ElTextArea<in V>(
        override val formLabel: String,
        private val extractValue: (V) -> String,
        viewName: String,
        visibleRows: Int? = null
) : MyTextArea(viewName, visibleRows), ElField<V> {

    override fun isModified(value: V) = _isModified(text, extractValue, value)
    override fun updateValue(value: V) { text = extractValue(value) }
    override fun toComponent() = this.scrolled()
}

class ElRichTextArea<in V> (
        override val formLabel: String,
        private val extractValue: (V) -> String,
        viewName: String
) : RichTextArea(viewName), ElField<V> {

    override fun isModified(value: V) = _isModified(toEnrichedText(), extractValue, value)
    override fun updateValue(value: V) {
        readEnrichedText(extractValue(value))
    }
    override fun toComponent() = this.scrolled()
}

class ElNumberField<V>(
        override val formLabel: String,
        private val extractValue: (V) -> Int,
        viewName: String,
        columns: Int = 100
) : NumberField(columns), ElField<V> {
    init {
        name = viewName
    }
    override fun isModified(value: V) = _isModified(numberValue, extractValue, value)
    override fun updateValue(value: V) {
        numberValue = extractValue(value)
    }
    override fun toComponent() = this
}

class ElCheckBox<in V>(
        val delegate: MyCheckBox,
        override val formLabel: String,
        private val extractValue: (V) -> Boolean
) : ElField<V> {

    override fun isModified(value: V) = _isModified(delegate.isSelected, extractValue, value)
    override fun updateValue(value: V) { delegate.isSelected = extractValue(value) }
    override fun toComponent() = delegate
}

class ElComboBox<in V, T : Labeled>(
        private val delegate: MyComboBox<T>,
        override val formLabel: String,
        private val extractValue: (V) -> T
) :
        ElField<V> {
    var selectedItemTyped: T
        get() = delegate.selectedItemTyped
        set(value) {
            delegate.selectedItemTyped = value
        }

    override fun isModified(value: V) = _isModified(selectedItemTyped, extractValue, value)
    override fun updateValue(value: V) { selectedItemTyped = extractValue(value) }
    override fun toComponent() = delegate
}

class ElDatePicker<in V>(
        private val delegate: MyDatePicker,
        override val formLabel: String,
        private val extractValue: (V) -> DateTime?
) : ElField<V> {
    var selectedDate: DateTime?
        get() = delegate.selectedDate()
        set(value) = delegate.changeDate(value)

    override fun isModified(value: V) = _isModified(selectedDate, extractValue, value)
    override fun updateValue(value: V) { selectedDate = extractValue(value) }
    override fun toComponent() = delegate
    fun hidePopup() {
        delegate.hidePopup()
    }
}

class ElTimePicker<in V>(
        private val delegate: MyTimePicker,
        override val formLabel: String,
        private val extractValue: (V) -> DateTime
) : ElField<V> {
    var selectedTime: DateTime
        get() = delegate.selectedItemTyped.delegate
        set(value) = delegate.changeSelectedByDateTime(value)

    override fun isModified(value: V) = _isModified(selectedTime.withAllButHourAndMinute(extractValue(value)), extractValue, value)
    override fun updateValue(value: V) { selectedTime = extractValue(value) }
    override fun toComponent() = delegate
}

class ElDateAndTimePicker<in V>(
        val delegate: DateAndTimePicker,
        override val formLabel: String,
        private val extractValue: (V) -> DateTime
) : ElField<V> {
    var selectedDate: DateTime
        get() = delegate.readDateTime()
        set(value) = delegate.writeDateTime(value)

    override fun isModified(value: V) = _isModified(selectedDate, extractValue, value)
    override fun updateValue(value: V) { selectedDate = extractValue(value) }
    override fun toComponent() = delegate
    fun hidePopup() {
        delegate.inpDate.hidePopup()
    }
}


class Fields<V>(private val modifications: ModificationChecker) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = LinkedList<ElField<V>>()

    fun newTextField(label: String, extractValue: (V) -> String, viewName: String): ElTextField<V> {
        val field = ElTextField(label, extractValue, viewName)
        modifications.enableChangeListener(field)
        fields.add(field)
        return field
    }

    fun newMinutesField(label: String, extractValue: (V) -> Int, viewName: String, columns: Int = 100): ElNumberField<V> {
        val field = ElNumberField(label, extractValue, viewName, columns)
        modifications.enableChangeListener(field)
        fields.add(field)
        return field
    }

    fun newTextArea(label: String, extractValue: (V) -> String, viewName: String): ElRichTextArea<V> {
        val field = ElRichTextArea(label, extractValue, viewName)
        modifications.enableChangeListener(field)
        fields.add(field)
        return field
    }

    fun newCheckBox(label: String, sideLabel: String, extractValue: (V) -> Boolean, viewName: String, preSelected: Boolean = false): ElCheckBox<V> {
        val realField = MyCheckBox().apply {
            name = viewName
            text = sideLabel
            isSelected = preSelected
        }
        val field = ElCheckBox(realField, label, extractValue)
        modifications.enableChangeListener(realField)
        fields.add(field)
        return field
    }

    fun <T: Labeled> newComboBox(values: List<T>, initValue: T, label: String, extractValue: (V) -> T, viewName: String): ElComboBox<V, T> {
        val realField = MyComboBox(values, initValue)
        val field = ElComboBox(realField, label, extractValue)
        realField.name = viewName
        modifications.enableChangeListener(realField)
        fields.add(field)
        return field
    }

    fun newDatePicker(initDate: DateTime?, label: String, extractValue: (V) -> DateTime?, viewNamePrefix: String): ElDatePicker<V> {
        val realField = MyDatePicker.build(initDate, viewNamePrefix)
        val field = ElDatePicker(realField, label, extractValue)
        modifications.enableChangeListener(realField)
        fields.add(field)
        return field
    }

    fun newTimePicker(label: String, initDate: DateTime, extractValue: (V) -> DateTime, viewName: String): ElTimePicker<V> {
        val realField = MyTimePicker(initDate, viewName)
        val field = ElTimePicker(realField, label, extractValue)
        modifications.enableChangeListener(realField)
        fields.add(field)
        return field
    }

    fun newDateAndTimePicker(label: String, initDate: DateTime, extractValue: (V) -> DateTime, viewNamePrefix: String,
                             dateFieldAlignment: Int = JTextField.LEFT): ElDateAndTimePicker<V> {
        val realField = DateAndTimePicker(initDate, viewNamePrefix, dateFieldAlignment)
        val field = ElDateAndTimePicker(realField, label, extractValue)
        modifications.enableChangeListener(realField.inpDate)
        modifications.enableChangeListener(realField.inpTime)
        fields.add(field)
        return field
    }


    fun register(field: ElFieldForProps<V>) {
        field.enableFor(modifications)
        fields.add(field)

    }

    fun isAnyModified(value : V): Boolean {
        return fields.any {
            val modified = it.isModified(value)
            if (modified && log.isTraceEnabled) {
                log.trace("Modification detected by '{}' for value: {}", it, value)
            }
            modified
        }
    }

    fun updateAll(value: V) {
        modifications.enableModificationsCheck = false
        fields.forEach { it.updateValue(value) }
        modifications.enableModificationsCheck = true

    }
}

fun <V> FormPanel.addFormInput(field: ElField<V>) {
    addFormInput(field.formLabel, field.toComponent())
}
