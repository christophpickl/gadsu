package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test
class ThresholdCalculatorTest {

    private lateinit var prefs: Prefs

    @BeforeMethod
    fun initMocks() {
        prefs = mock()

    }

    fun `foo`() {
        val client = ExtendedClient(
                client = Client.savedValidInstance(),
                countTreatments = 3,
                upcomingAppointment = null,
                differenceDaysToRecentTreatment = 1)

        val level = calcThreshold(client)

        assertThat(level, equalTo(ThresholdLevel.Ok))
    }

    private fun calcThreshold(client: ExtendedClient) =
            ThresholdCalculator(prefs).calc(client)

}
