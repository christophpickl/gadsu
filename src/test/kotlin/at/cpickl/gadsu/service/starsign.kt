package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.joda.time.DateTime
import org.testng.annotations.Test


@Test class StarSignCalculatorTest {
    fun allStarSigns() {
        assertStarSign("1985-03-21", StarSign.ARIES)
        assertStarSign("1985-05-20", StarSign.TAURUS)
        assertStarSign("1985-05-22", StarSign.GEMINI)
        assertStarSign("1985-06-25", StarSign.CANCER)
        assertStarSign("1985-07-25", StarSign.LEO)
        assertStarSign("1985-08-25", StarSign.VIRGO)
        assertStarSign("1985-09-25", StarSign.LIBRA)
        assertStarSign("1985-11-20", StarSign.SCORPIO)
        assertStarSign("1985-11-24", StarSign.SAGITTARIUS)
        assertStarSign("1985-12-24", StarSign.CAPRICORN)
        assertStarSign("1985-01-25", StarSign.AQUARIUS)
        assertStarSign("1985-03-01", StarSign.PISCES)
    }

    private fun assertStarSign(date: String, expected: StarSign) {
        assertThat("Wrong for expected sign '${expected.name}'.",
                StarSignCalculator.signFor(DateTime.parse("${date}T00:00:00")), equalTo(expected))
    }
}
