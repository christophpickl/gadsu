package at.cpickl.gadsu.mail

import at.cpickl.gadsu.isNotValidMail


data class Mail(
        val recipients: List<String>,
        val subject: String,
        val body: String,
        val recipientsAsBcc: Boolean = true
) {

    constructor(recipient: String, subject: String, body: String, recipientsAsBcc: Boolean = true): this(listOf(recipient), subject, body, recipientsAsBcc)

    init {
        if (recipients.isEmpty()) {
            throw IllegalArgumentException("Recipients must not be empty! ($this)")
        }
        if (recipients.any(String::isNotValidMail)) {
            throw IllegalArgumentException("There was an invalid address in the list of recipients! ($this)")
        }
    }
}


data class MailPreferencesData(
        val subject: String,
        val body: String
)
