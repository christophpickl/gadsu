package at.cpickl.gadsu.mail

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.service.GoogleConnector
import at.cpickl.gadsu.service.GoogleConnectorImpl
import at.cpickl.gadsu.service.formatDateTime
import at.cpickl.gadsu.testinfra.readGapiCredentialsFromSysProps
import org.joda.time.DateTime
import org.testng.annotations.Test


@Test(groups = arrayOf("mTest")
        , enabled = false
)
class MailManualTest {

    companion object {
        private val USER_ID = "christoph.pickl@gmail.com"
    }

    fun `send mail and receive again, must not throw 404 GoogleJsonResponseException`() {
        GadsuSystemProperty.development.enable()
        val credentials = readGapiCredentialsFromSysProps()
        val connector = GoogleConnectorImpl()

        val sent = sendMail(connector, credentials)

        println("Waiting 2 secs ...")
        Thread.sleep(2000)

        assertMail(connector, credentials, sent.id)

    }

    private fun sendMail(connector: GoogleConnector, credentials: GapiCredentials) =
            GMailSender(connector).send(Mail(
                    listOf("gadsu1@discard.email"), //, "gadsu2@discard.email"),
                    "my test subject (${DateTime.now().formatDateTime()})",
                    "my test body"),

                    USER_ID,
                    credentials)

    private fun assertMail(connector: GoogleConnector, credentials: GapiCredentials, mesageId: String) {
        val gmail = connector.connectGmail(credentials)
        val storedMessage = gmail.users().messages().get(USER_ID, mesageId).execute()
        println("storedMessage: $storedMessage")
    }


}


