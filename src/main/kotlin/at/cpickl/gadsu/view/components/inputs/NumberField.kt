package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.view.swing.enforceCharactersByRegexp
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.regex.Pattern
import javax.swing.JTextField


open class NumberField(columns: Int = 100) : JTextField(columns) {
    companion object {
        private val NUMBER_REGEXP = Pattern.compile("""\d+""")
    }
    init {
        enforceCharactersByRegexp(NUMBER_REGEXP)
        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent?) {
                if (text.isEmpty()) {
                    text = "0"
                }
            }
            override fun focusGained(e: FocusEvent?) { }
        })

        horizontalAlignment = JTextField.RIGHT
    }

    var numberValue: Int
        get() {
            if (text.trim().isEmpty()) {
                return 0
            }
            return text.toInt()
        }
        set(value) {
            text = value.toString()
        }

}

//class MinutesField() : NumberField(3) {
//    val duration: Duration get() = minutes(numberValue)
//}