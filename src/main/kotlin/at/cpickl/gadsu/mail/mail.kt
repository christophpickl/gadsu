package at.cpickl.gadsu.mail

import java.util.regex.Pattern


data class Mail(
        val recipients: List<String>,
        val subject: String,
        val body: String
) {
    init {
        if (recipients.isEmpty()) {
            throw IllegalArgumentException("Recipients must not be empty! ($this)")
        }
    }
}

private val mailPattern = Pattern.compile("""^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$""")
fun String.isNotValidMail() = !this.isValidMail()
fun String.isValidMail(): Boolean {
    return mailPattern.matcher(this).matches()
}
