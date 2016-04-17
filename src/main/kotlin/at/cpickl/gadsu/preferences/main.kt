package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.UserEvent


class ShowPreferencesEvent : UserEvent()

class PreferencesWindowClosedEvent : UserEvent()

data class PreferencesData(val username: String) {
    companion object {
        val DEFAULT = PreferencesData(
                username = System.getProperty("user.name")
        )
    }
}

