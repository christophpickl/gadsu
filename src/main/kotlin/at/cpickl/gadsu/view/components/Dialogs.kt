package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.view.MainFrame
import com.google.inject.Inject
import javax.swing.JOptionPane


enum class DialogType(val swingConstant: Int) {
    PLAIN(-1),
    INFO(1),
    WARN(2),
    QUESTION(3),
    ERROR(0)
}

class Dialogs @Inject constructor(
        private val frame: MainFrame?
) {
    /**
     * @param buttonLabels for each button its label
     * @param defaultButton if null, the first option of buttonLabels will be used
     * @return the label which was selected or null if user just hit the close button
     */
    fun show(title: String, message: String, buttonLabels: Array<String>, defaultButton: String? = null, type: DialogType = DialogType.PLAIN): String? {
        val selected = JOptionPane.showOptionDialog(frame?.asJFrame(), message, title,
                JOptionPane.DEFAULT_OPTION, type.swingConstant, null, buttonLabels, defaultButton?:buttonLabels[0])
        if (selected === JOptionPane.CLOSED_OPTION) {
            return null
        }
        return buttonLabels[selected]
    }


    fun confirmedDelete(promptPart: String, onSuccess: () -> Unit, promptSuffix: String = "") {
        val selected = show(
                title = "Bist du dir sicher?",
                message = "Willst du $promptPart wirklich l\u00f6schen?!$promptSuffix",
                type = DialogType.QUESTION,
                buttonLabels = arrayOf("L\u00f6schen", "Abbrechen")
        )
        if (selected === null || selected.equals("Abbrechen")) {
            return
        }
        onSuccess()
    }

}