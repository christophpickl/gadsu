package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.view.MainWindow
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
        private val window: MainWindow?
) {
    /**
     * @param buttonLabels for each button its label
     * @param defaultButton if null, the first option of buttonLabels will be used
     * @return the label which was selected or null if user just hit the close button
     */
    fun show(title: String, message: String, buttonLabels: Array<String>, defaultButton: String? = null, type: DialogType = DialogType.PLAIN): String? {
        val selected = JOptionPane.showOptionDialog(window?.asJFrame(), message, title,
                JOptionPane.DEFAULT_OPTION, type.swingConstant, null, buttonLabels, defaultButton?:buttonLabels[0])
        if (selected == JOptionPane.CLOSED_OPTION) {
            return null
        }
        return buttonLabels[selected]
    }

}
