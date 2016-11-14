package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.UserEvent


class ShowPreferencesEvent : UserEvent()

class PreferencesWindowClosedEvent(val persistData: Boolean) : UserEvent()

data class PreferencesData(
        val username: String,
        val checkUpdates: Boolean,
        val proxy: String?,
        val gcalName: String?,
        var gmailAddress: String?,
        val treatmentGoal: Int?
) {
    companion object {
        val DEFAULT = PreferencesData(
                username = System.getProperty("user.name"),
                checkUpdates = true,
                proxy = null,
                gcalName = null,
                gmailAddress = null,
                treatmentGoal = null
        )
    }
}

