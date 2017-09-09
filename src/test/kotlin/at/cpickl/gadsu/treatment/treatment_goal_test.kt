package at.cpickl.gadsu.treatment

import at.cpickl.gadsu.global.AppStartupEvent
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.emptyInstance
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class TreatmentGoalControllerTest {

    private val treat1 = Treatment.unsavedValidInstance("clientId")

    private lateinit var prefs: Prefs
    private lateinit var treatmentRepo: TreatmentRepository

    @BeforeMethod fun initMocks() {
        prefs = mock(Prefs::class.java)
        treatmentRepo = mock(TreatmentRepository::class.java)
    }

    fun `given treatmentGoal pref disabled, when after startup delete a not-protocolized treatment, then count should be 0`() {
        `when`(prefs.preferencesData).thenReturn(preferencesData(treatmentGoal = null))
        val controller = TreatmentGoalController(prefs, treatmentRepo)

        controller.onAppStartupEvent(AppStartupEvent())
        controller.onTreatmentDeletedEvent(TreatmentDeletedEvent(treatment = treat1, treatmentHasBeenProtocolizedYet = false))

        assertThat(controller.view.count, equalTo(0))
    }

    private fun preferencesData(treatmentGoal: Int?) = PreferencesData.emptyInstance().copy(treatmentGoal = treatmentGoal)

}
