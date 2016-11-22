package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.MainFrame
import com.google.inject.Inject
import javax.swing.JFrame
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

    private val log = LOG(javaClass)

    /**
     * @param buttonLabels for each button its label
     * @param defaultButton if null, the first option of buttonLabels will be used
     * @return the label which was selected or null if user just hit the close button
     */
    fun show(title: String,
             message: String,
             buttonLabels: Array<String> = arrayOf("Ok"),
             defaultButton: String? = null,
             type: DialogType = DialogType.PLAIN,
             overrideOwner: JFrame? = null
    ): String? {
        if (type == DialogType.ERROR) {
            log.debug("ERROR dialog showing: {} - {}", title, message)
        }
        // could enable html (<br>) in Dialogs, but would need to rewrite dialog from scratch...
        val selected = JOptionPane.showOptionDialog(overrideOwner ?: frame?.asJFrame(), message, title,
                JOptionPane.DEFAULT_OPTION, type.swingConstant, null, buttonLabels, defaultButton?:buttonLabels[0])
        if (selected === JOptionPane.CLOSED_OPTION) {
            return null
        }
        return buttonLabels[selected]
    }


    fun confirmedDelete(promptPart: String, onSuccess: () -> Unit, promptSuffix: String = "") {
        val selected = show(
                title = "Bist du dir sicher?",
                message = "Willst du $promptPart wirklich l\u00f6schen?$promptSuffix",
                type = DialogType.QUESTION,
                buttonLabels = arrayOf("L\u00f6schen", "Abbrechen")
        )
        if (selected === null || selected.equals("Abbrechen")) {
            return
        }
        onSuccess()
    }

}
