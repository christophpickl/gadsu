package at.cpickl.gadsu.mail

import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.testinfra.GoogleManualTest
import org.joda.time.DateTime
import org.testng.annotations.Test


@Test(groups = arrayOf("mTest")
        , enabled = false
)
class MailManualTest : GoogleManualTest() {

    fun `send mail and receive again, must not throw 404 GoogleJsonResponseException`() {
        val sent = sendMail()

        println("Waiting 2 secs ...")
        Thread.sleep(2000)

        assertMail(sent.id)
    }

    private fun sendMail() =
            GMailSender(connector).send(Mail(
                    listOf("gadsu1@discard.email"), //, "gadsu2@discard.email"),
                    "my test subject (${DateTime.now().formatDateTime()})",
                    "my test body"),

                    USER_ID,
                    credentials)

    private fun assertMail(mesageId: String) {
        val gmail = connector.connectGmail(credentials)
        val storedMessage = gmail.users().messages().get(USER_ID, mesageId).execute()
        println("storedMessage: $storedMessage")
    }

}


