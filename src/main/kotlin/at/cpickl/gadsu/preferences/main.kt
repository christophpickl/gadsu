package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.service.GapiCredentials


class ShowPreferencesEvent : UserEvent()

class PreferencesWindowClosedEvent(val persistData: Boolean) : UserEvent()

data class PreferencesData(
        val username: String,
        val checkUpdates: Boolean,
        val proxy: String?,
        val gcalName: String?,
        var gmailAddress: String?,
        var gapiCredentials: GapiCredentials?,
        val treatmentGoal: Int?,
        val templateConfirmSubject: String?,
        val templateConfirmBody: String?
) {
    companion object {
        val DEFAULT = PreferencesData(
                username = System.getProperty("user.name"),
                checkUpdates = true,
                proxy = null,
                gcalName = null,
                gmailAddress = null,
                gapiCredentials = null,
                treatmentGoal = null,
                templateConfirmSubject = null,
                templateConfirmBody = null
        )
    }
}

