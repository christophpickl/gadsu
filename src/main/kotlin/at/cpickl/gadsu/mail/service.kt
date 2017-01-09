package at.cpickl.gadsu.mail

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.preferences.Prefs
import javax.inject.Inject

interface MailService {
    fun send(mail: Mail)
}

class MailServiceImpl @Inject constructor(
        private val prefs: Prefs,
        private val sender: MailSender
) : MailService {

    override fun send(mail: Mail) {
        verifyMailPrefsSet()

        // brute force access as already checked in verifyMailPrefsSet()
        val myAddress = prefs.preferencesData.gmailAddress!!
        val credentials = prefs.preferencesData.gapiCredentials!!

        sender.send(mail, myAddress, credentials)
    }

    private fun verifyMailPrefsSet() {
        if (prefs.preferencesData.gmailAddress == null) {
            throw MailSendException("GMail address needs to be set in preferences in order to send mails!")
        }
        if (prefs.preferencesData.gapiCredentials == null) {
            throw MailSendException("Google API credentials needs to be set in preferences in order to send mails!")
        }
    }

}

class MailSendException(message: String, cause: Throwable? = null) : GadsuException(message, cause)
