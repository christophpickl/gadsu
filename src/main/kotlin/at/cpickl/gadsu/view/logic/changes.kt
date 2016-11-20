package at.cpickl.gadsu.view.logic

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs

// builds its logic upon the modification logic

enum class ChangeBehaviour {
    CONTINUE,
    ABORT
}


interface ChangesCheckerCallback {
    fun isModified(): Boolean
    fun save()
}

class ChangesChecker(
        private val dialogs: Dialogs,
        private val callback: ChangesCheckerCallback
) {

    private val log = LOG(javaClass)

    fun checkChanges(): ChangeBehaviour {
        log.trace("checkChanges()")
        if (!callback.isModified()) {
            return ChangeBehaviour.CONTINUE
        }
        log.debug("Changes detected.")

        val result = dialogs.show("Ungespeicherte \u00c4nderungen", "Es existieren ungespeicherte \u00c4nderungen. Wie w\u00fcnscht du mit diesen umzugehen?",
                arrayOf("Speichern", "\u00c4nderungen verwerfen", "Abbrechen"), type = DialogType.WARN)

        when (result) {
            "Speichern" -> {
                callback.save()
                // would be nice to CONTINUE, but ... difficult
                return ChangeBehaviour.ABORT
            }
            "\u00c4nderungen verwerfen" -> {
                return ChangeBehaviour.CONTINUE
            }
            "Abbrechen", null -> {
                return ChangeBehaviour.ABORT
            }
            else -> throw GadsuException("Unhandled dialog option: '$result'")
        }
    }
}
