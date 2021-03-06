package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.global.UserEvent
import at.cpickl.gadsu.service.GapiCredentials


class ShowPreferencesEvent : UserEvent()

class PreferencesWindowClosedEvent(val persistData: Boolean) : UserEvent()

data class ThresholdPrefData(
        val daysAttention: Int, // orange
        val daysWarn: Int, // red
        val daysFatal: Int // lila
) {
    companion object {
        val DEFAULT = ThresholdPrefData(
                // 0 ... green
                daysAttention = 14,
                daysWarn = 30,
                daysFatal = 60
        )
    }
}

data class PreferencesData(
        val username: String,
        val checkUpdates: Boolean,
        val proxy: String?,
        val gcalName: String?,
        var gmailAddress: String?,
        var gapiCredentials: GapiCredentials?,
        val treatmentGoal: Int?,
        val threshold: ThresholdPrefData,
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
                threshold = ThresholdPrefData.DEFAULT,
                templateConfirmSubject = "[shiatsu] terminbestaetigung \${dateStart?string[\"d.M.\"]}",
                templateConfirmBody = """hallo <#if gender == \"M\">lieber <#elseif gender == \"F\">liebe </#if>$\{name?lower_case},
meine software ist so nett und moechte dich in meinem namen daran erinnern,
dass wir einen shiatsu termin haben, naemlich am:

  $\{dateStart?string[\"EEEE 'der' d. MMMMM\"]?lower_case}, von $\{dateStart?string[\"HH:mm\"]} bis $\{dateEnd?string[\"HH:mm\"]} uhr

ich freu mich schon sehr drauf,
auf bald,

christoph
"""
        )
    }

    val isGmailAndGapiConfigured: Boolean get() = (gmailAddress?.isNotEmpty() ?: false) && (gapiCredentials?.isNotEmpty ?: false)

}

