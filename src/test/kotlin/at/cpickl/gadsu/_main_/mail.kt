package at.cpickl.gadsu._main_

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.mail.GMailSender
import at.cpickl.gadsu.mail.Mail
import at.cpickl.gadsu.service.GoogleConnectorImpl

fun main(args: Array<String>) {
    GadsuSystemProperty.development.enable()

    GMailSender(GoogleConnectorImpl()).send(Mail(
            listOf("gadsu1@discard.email"),//, "gadsu2@discard.email"),
            "my test subject 2",
            "my test body"),
            "christoph.pickl@gmail.com")
}
