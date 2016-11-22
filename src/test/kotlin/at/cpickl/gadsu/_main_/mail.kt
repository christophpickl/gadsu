package at.cpickl.gadsu._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.mail.GMailSender
import at.cpickl.gadsu.mail.GMailServerConectorImpl
import at.cpickl.gadsu.mail.GapiCredentials
import at.cpickl.gadsu.mail.Mail

fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    val gapiSecret = System.getProperty("GAPI_SECRET", "")
    val gapiId = System.getProperty("GAPI_ID", "")
    if (gapiId.isEmpty()) throw IllegalStateException("define VM argument: GAPI_ID")
    if (gapiSecret.isEmpty()) throw IllegalStateException("define VM argument: GAPI_SECRET")

    GMailSender(GMailServerConectorImpl()).send(Mail(
            listOf("gadsu1@discard.email"),//, "gadsu2@discard.email"),
            "my test subject 2",
            "my test body"),
            "christoph.pickl@gmail.com",
            GapiCredentials.buildNullSafe(gapiId, gapiSecret)!!)
}
