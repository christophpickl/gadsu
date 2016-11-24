package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.GadsuSystemProperty
import at.cpickl.gadsu.service.GapiCredentials
import at.cpickl.gadsu.service.GoogleConnectorImpl
import at.cpickl.gadsu.service.clearMinutes
import org.joda.time.DateTime
import org.junit.BeforeClass
import org.testng.annotations.Test


fun readGapiCredentialsFromSysProps(): GapiCredentials {
    val gapiSecret = System.getProperty("GAPI_SECRET", "")
    val gapiId = System.getProperty("GAPI_ID", "")
    if (gapiId.isEmpty()) throw IllegalStateException("define VM argument: -DGAPI_ID")
    if (gapiSecret.isEmpty()) throw IllegalStateException("define VM argument: -DGAPI_SECRET")
    return GapiCredentials.buildNullSafe(gapiId, gapiSecret)!!
}

@Test(groups = arrayOf("mTest")) abstract class GoogleManualTest {

    companion object {
        val USER_ID = "christoph.pickl@gmail.com"
        val CALENDER_NAME = "gadsu_mtest"

    }
    protected val connector = GoogleConnectorImpl()
    protected val credentials: GapiCredentials by lazy { readGapiCredentialsFromSysProps() }
    protected val now = DateTime.now().clearMinutes()
    private var originalDevState = false

    @BeforeClass fun init() {
        originalDevState = GadsuSystemProperty.development.isEnabledOrFalse()

        GadsuSystemProperty.development.enable()
    }

    @BeforeClass fun after() {
        if (originalDevState) {
            GadsuSystemProperty.development.enable()
        } else {
            GadsuSystemProperty.development.disable()
        }
    }

}
