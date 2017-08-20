package at.cpickl.gadsu.service

import at.cpickl.gadsu.Labeled
import at.cpickl.gadsu.Ordered
import at.cpickl.gadsu.orderedValuesOf
import org.joda.time.DateTime


// ====================================================================================================
// STAR SIGN
// ====================================================================================================


enum class StarSign(override val order: Int, override val label: String) : Ordered, Labeled {
    ARIES(      1, "Widder"),       // 21.3. - 19.4.
    TAURUS(     2, "Stier"),        // 20.4. - 20.5.
    GEMINI(     3, "Zwilling"),     // 21.5. - 21.6.
    CANCER(     4, "Krebs"),        // 22.6. - 22.7.
    LEO(        5, "L\u00F6we"),    // 23.7. - 22.8.
    VIRGO(      6, "Jungfrau"),     // 23.8. - 22.9.
    LIBRA(      7, "Waage"),        // 23.9. - 23.10.
    SCORPIO(    8, "Skorpion"),     // 24.10. - 21.11.
    SAGITTARIUS(9, "Sch\u00FCtze"), // 22.11. - 21.12.
    CAPRICORN( 10, "Steinbock"),    // 22.12. - 19.1.
    AQUARIUS(  11, "Wassermann"),   // 20.1. - 18.2.
    PISCES(    12, "Fisch"),        // 19.2. - 20.3.
    UNKNOWN(   99, "Unbekannt");

    companion object {
        val orderedValues:List<StarSign> = orderedValuesOf(values())
    }
}


object StarSignCalculator {
    fun signFor(birth: DateTime): StarSign {
        // could move date from/to into StarSign enum itself...
        if (birth.between(21, 3, 19, 4)) return StarSign.ARIES
        if (birth.between(20, 4, 20, 5)) return StarSign.TAURUS
        if (birth.between(21, 5, 21, 6)) return StarSign.GEMINI
        if (birth.between(22, 6, 22, 7)) return StarSign.CANCER
        if (birth.between(23, 7, 22, 8)) return StarSign.LEO
        if (birth.between(23, 8, 22, 9)) return StarSign.VIRGO
        if (birth.between(23, 9, 23, 10)) return StarSign.LIBRA
        if (birth.between(24, 10, 21, 11)) return StarSign.SCORPIO
        if (birth.between(22, 11, 21, 12)) return StarSign.SAGITTARIUS
        if (birth.between(22, 12, 31, 12) || birth.between(1, 1, 19, 1)) return StarSign.CAPRICORN
        if (birth.between(20, 1, 18, 2)) return StarSign.AQUARIUS
        if (birth.between(19, 2, 20, 3)) return StarSign.PISCES
        throw RuntimeException("Could not determine star sign for birthdate: $birth")
    }

    /** Does not support "edge calculation", eg from 24.12. to 10.1. */
    private fun DateTime.between(fromDay: Int, fromMonth: Int, toDay: Int, toMonth: Int): Boolean {
        val fromDate = withMonthOfYear(fromMonth).withDayOfMonth(fromDay)
        val toDate = withMonthOfYear(toMonth).withDayOfMonth(toDay)
        return (this.isAfter(fromDate) || this.isEqual(fromDate))
                &&
                (this.isBefore(toDate) || this.isEqual(toDate))
    }
}
