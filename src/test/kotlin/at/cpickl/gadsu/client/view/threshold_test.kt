package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ThresholdPrefData
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.github.christophpickl.kpotpourri.test4k.toDataProviding
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.joda.time.DateTime
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test


@Test
class ThresholdCalculatorImplTest {

    private lateinit var prefs: Prefs

    private val anyDonation = ClientDonation.UNKNOWN
    private val anyCategory = ClientCategory.B

    private val allCombinations: List<Pair<ClientCategory, ClientDonation>> = ClientCategory.values().map { cat ->
        ClientDonation.values().map { don ->
            Pair(cat, don)
        }

    }.flatten()

    private val defaultThreshold = ThresholdPrefData(
            daysAttention = 100,
            daysWarn = 200,
            daysFatal = 300
    )
    private val defaultPrefs = PreferencesData.DEFAULT.copy(
            threshold = defaultThreshold
    )

    @BeforeMethod
    fun initMocks() {
        prefs = mock()
        whenever(prefs.preferencesData).thenReturn(defaultPrefs)

    }

    @DataProvider
    fun `provideAllCombinations`() = allCombinations.toDataProviding()

    @DataProvider
    fun `provideAllCategories`() = ClientCategory.values().toList().toDataProviding()

    @Test(dataProvider = "provideAllCombinations")
    fun `days 0 is Ok for all combinations`(pairs: Pair<ClientCategory, ClientDonation>) {
        val (cat, don) = pairs
        assertThat(calcThreshold(0, cat, don),
                equalTo(ThresholdResult.Ok))
    }

    @Test(dataProvider = "provideAllCombinations")
    fun `days HIGH is Fatal for all combinations`(pairs: Pair<ClientCategory, ClientDonation>) {
        val (cat, don) = pairs
        assertThat(calcThreshold(defaultThreshold.daysFatal * 3, cat, don),
                equalTo(ThresholdResult.Fatal))
    }

    fun `category changes level at days equal configured attention`() {
        val days = defaultThreshold.daysAttention

        assertThat(calcThreshold(days, ClientCategory.A, anyDonation), equalTo(ThresholdResult.Attention))
        assertThat(calcThreshold(days, ClientCategory.B, anyDonation), equalTo(ThresholdResult.Attention))
        assertThat(calcThreshold(days, ClientCategory.C, anyDonation), equalTo(ThresholdResult.Ok))
    }

    fun `donation changes level at days equal configured attention`() {
        val days = defaultThreshold.daysAttention

        assertThat(calcThreshold(days, anyCategory, ClientDonation.NONE), equalTo(ThresholdResult.Ok))
        assertThat(calcThreshold(days, anyCategory, ClientDonation.UNKNOWN), equalTo(ThresholdResult.Attention))
        assertThat(calcThreshold(days, anyCategory, ClientDonation.PRESENT), equalTo(ThresholdResult.Attention))
        assertThat(calcThreshold(days, anyCategory, ClientDonation.MONEY), equalTo(ThresholdResult.Attention))
    }

    fun `if got appointment then grey out`() {
        assertThat(calcThreshold(buildClient(appointment = DateTime.now())),
                equalTo(ThresholdResult.GotNextAppointment))
    }

    private fun buildClient(appointment: DateTime) =
            buildClient(1, ClientCategory.A, anyDonation, appointment)

    private fun buildClient(days: Int?, category: ClientCategory, donation: ClientDonation, appointment: DateTime? = null) = ExtendedClient(
            client = Client.savedValidInstance().copy(
                    category = category,
                    donation = donation
            ),
            countTreatments = 42,
            upcomingAppointment = appointment,
            differenceDaysToRecentTreatment = days)

    private fun calcThreshold(days: Int?, category: ClientCategory, donation: ClientDonation, appointment: DateTime? = null) =
            calcThreshold(buildClient(days, category, donation, appointment))

    private fun calcThreshold(client: ExtendedClient) =
            ThresholdCalculatorImpl(prefs).calc(client)


}
