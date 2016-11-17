package at.cpickl.gadsu.view.components.inputs

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.swing.enforceCharactersByRegexp
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.regex.Pattern
import javax.swing.JTextField


open class NumberField(columns: Int = 100, initValue: Int? = null) : JTextField(columns) {
    companion object {
        private val NUMBER_REGEXP = Pattern.compile("""\d+""")
    }

    private val log = LOG(javaClass)

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

    init {
        if (initValue != null) {
            numberValue = initValue
        }
        enforceCharactersByRegexp(NUMBER_REGEXP)
        addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent) {
                log.trace("focusGained; selectAll()")
                selectAll()
            }
            override fun focusLost(e: FocusEvent) {
                if (text.isEmpty()) {
                    text = "0"
                }
            }
        })

        horizontalAlignment = JTextField.RIGHT
    }

}

//class MinutesField() : NumberField(3) {
//    val duration: Duration get() = minutes(numberValue)
//}
