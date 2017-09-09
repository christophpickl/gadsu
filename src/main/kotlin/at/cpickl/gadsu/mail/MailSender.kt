package at.cpickl.gadsu.mail

import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.LOG
import javax.inject.Inject

interface MailSender {
    fun send(mail: Mail)
}

class MockMailSender : MailSender {
    private val log = LOG(javaClass)
    override fun send(mail: Mail) {
        log.warn("!MOCK MAIL!")
        log.warn("send(mail) ... mock is NOT sending this mail: $mail")
    }
}

class MailSenderImpl @Inject constructor(
        private val prefs: Prefs,
        private val gmailApi: GMailApi
) : MailSender {

    override fun send(mail: Mail) {
        verifyMailPrefsSet()

        // brute force access as already checked in verifyMailPrefsSet()
        val myAddress = prefs.preferencesData.gmailAddress!!
        val credentials = prefs.preferencesData.gapiCredentials!!

        gmailApi.send(mail, myAddress, credentials)
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
