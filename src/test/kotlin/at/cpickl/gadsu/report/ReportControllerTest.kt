package at.cpickl.gadsu.report

import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.parseDate
import at.cpickl.gadsu.service.toMinutes
import at.cpickl.gadsu.testinfra.TEST_DYNTREAT_BLOOD
import at.cpickl.gadsu.testinfra.TEST_DYNTREAT_HARA
import at.cpickl.gadsu.testinfra.TEST_DYNTREAT_PULSE
import at.cpickl.gadsu.testinfra.TEST_DYNTREAT_TONGUE
import at.cpickl.gadsu.treatment.dyn.DynTreatment
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.jfree.data.time.DateRange
import org.joda.time.DateTime
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class DynTreatmentXXXtoReportString {

    @DataProvider
    fun dpToReportString() = arrayOf<Array<Any>>(
            arrayOf<Any>(TEST_DYNTREAT_HARA, "Haradiagnose: Kyo { Bl }, Jitsu { Le, Gb }, Verbindung { Bl + Le }"),
            arrayOf<Any>(TEST_DYNTREAT_HARA.copy(bestConnection = null), "Haradiagnose: Kyo { Bl }, Jitsu { Le, Gb }"),

            arrayOf<Any>(TEST_DYNTREAT_TONGUE, "Zungendiagnose: rote Spitze, normal rosa, lang, gelb, dick, Mittelriss\n* zunge gruen"),
            arrayOf<Any>(PulseDiagnosis(emptyList(), ""), "Pulsdiagnose: "),
            arrayOf<Any>(TEST_DYNTREAT_PULSE, "Pulsdiagnose: ansteigend, tief\n* war irgendwie \"zaeh\""),
            arrayOf<Any>(TEST_DYNTREAT_BLOOD, "Blutdruckmessung: " +
                    "${TEST_DYNTREAT_BLOOD.before.systolic}/${TEST_DYNTREAT_BLOOD.before.diastolic}/${TEST_DYNTREAT_BLOOD.before.frequency}, " +
                    "${TEST_DYNTREAT_BLOOD.after.systolic}/${TEST_DYNTREAT_BLOOD.after.diastolic}/${TEST_DYNTREAT_BLOOD.after.frequency}")
    )

    @Test(dataProvider = "dpToReportString")
    fun `toReportString sunshine`(dynTreatment: DynTreatment, expectedReport: String) {
        assertThat(dynTreatment.toReportString(), equalTo(expectedReport))
    }

}


@Test class ReportControllerTest {

    companion object {
        val EARLY_DATE_STRING = "01.01.2000"
        val EARLY_DATE_DATETIME = EARLY_DATE_STRING.parseDate()
        val LATE_DATE_STRING = "01.01.2020"
        val LATE_DATE_DATETIME = LATE_DATE_STRING.parseDate()
        val SOME_DATE_STRING = "03.04.2012"
        val SOME_DATE_DATETIME = SOME_DATE_STRING.parseDate()
        val SOME_DATE_DATE = SOME_DATE_DATETIME.toDate()
        val ANY_DURATION = 0
    }

    fun `generateStatistics treatmentDateRange with single treatment`() {
        assertThat(statisticsOf(protocol(treatment(SOME_DATE_STRING))).treatmentDateRange,
                equalTo(DateRange(SOME_DATE_DATE, SOME_DATE_DATE)))
    }

    fun `generateStatistics treatmentDateRange with multiple treatments`() {
        assertThat(statisticsOf(
                protocol(treatment(LATE_DATE_DATETIME.minusDays(3).formatDate()), treatment(LATE_DATE_STRING)),
                protocol(treatment(EARLY_DATE_STRING), treatment(EARLY_DATE_DATETIME.plusDays(20).formatDate()))
        ).treatmentDateRange,
                equalTo(DateRange(EARLY_DATE_DATETIME.toDate(), LATE_DATE_DATETIME.toDate())))
    }

    fun `generateStatistics numberOfClients`() {
        assertThat(statisticsOf(protocol(), protocol()).numberOfClients, equalTo(2))
    }

    fun `generateStatistics numberOfTreatments`() {
        assertThat(statisticsOf(protocol(treatment()), protocol(treatment(), treatment())).numberOfTreatments, equalTo(3))
    }

    fun `generateStatistics totalTreatmentTime`() {
        assertThat(statisticsOf(protocol(treatment(duration = 20)), protocol(treatment(duration = 22))).totalTreatmentTime.toMinutes(), equalTo(42))
    }

    private fun statisticsOf(vararg protocols: ProtocolReportData) = ReportController.generateStatistics(protocols.toList())

    private fun protocol(vararg treatments: TreatmentReportData): ProtocolReportData {
        return ProtocolReportData("anyAuthor", DateTime.now(), ClientReportData.testInstance(""),
                if (treatments.size == 0) listOf(treatment()) else treatments.toList())
    }

    private fun treatment(date: String = SOME_DATE_STRING, duration: Int = ANY_DURATION): TreatmentReportData {
        return TreatmentReportData.testInstance(date = date, duration = duration)
    }

}
