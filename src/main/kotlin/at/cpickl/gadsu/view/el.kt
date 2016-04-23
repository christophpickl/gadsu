package at.cpickl.gadsu.view

import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.Labeled
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.MyComboBox
import at.cpickl.gadsu.view.components.MyDatePicker
import at.cpickl.gadsu.view.components.scrolled
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.awt.Component
import java.util.LinkedList
import java.util.Objects
import javax.swing.JTextArea
import javax.swing.JTextField


private val LOG_ElFIeld = LoggerFactory.getLogger(ElField::class.java)

interface ElField<V> {
    val formLabel: String
    fun isModified(value: V): Boolean
    fun updateValue(value: V)
    fun asComponent(): Component
}

private fun <V, T> ElField<V>._isModified(oldValue: T, extractValue: (V) -> T, value: V): Boolean {
    val extracted = extractValue(value)
    val modified = !Objects.equals(extracted, oldValue)
    if (modified) {
        LOG_ElFIeld.trace("Changes detected for form item '{}': UI-value: '{}', given value: '{}'", formLabel, oldValue, extracted)
    }
    return modified
}

class ElTextField<V>(
        override val formLabel: String,
        private val extractValue: (V) -> String,
        private val viewName: String
) : JTextField(), ElField<V> {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        name = viewName
    }

    override fun isModified(value: V) = _isModified(text, extractValue, value)
    override fun updateValue(value: V) { text = extractValue(value) }
    override fun asComponent() = this
}

class ElTextArea<V>(
        override val formLabel: String,
        private val extractValue: (V) -> String,
        private val viewName: String
) : JTextArea(), ElField<V> {

    init {
        name = viewName
        lineWrap = true
    }

    override fun isModified(value: V) = _isModified(text, extractValue, value)
    override fun updateValue(value: V) { text = extractValue(value) }
    override fun asComponent() = this.scrolled()
}

class ElComboBox<V, T : Labeled>(
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
    override fun asComponent() = delegate
}

class ElDatePicker<V>(
        private val delegate: MyDatePicker,
        override val formLabel: String,
        private val extractValue: (V) -> DateTime?
) : ElField<V> {
    var selectedDate: DateTime?
        get() = delegate.selectedDate()
        set(value) = delegate.changeDate(value)

    override fun isModified(value: V) = _isModified(selectedDate, extractValue, value)
    override fun updateValue(value: V) { selectedDate = extractValue(value) }
    override fun asComponent() = delegate
    fun hidePopup() {
        delegate.hidePopup()
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

    fun newTextArea(label: String, extractValue: (V) -> String, viewName: String): ElTextArea<V> {
        val field = ElTextArea(label, extractValue, viewName)
        modifications.enableChangeListener(field)
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
    addFormInput(field.formLabel, field.asComponent())
}
