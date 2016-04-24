package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.acupuncture.Acupunct.Companion.punkt
import at.cpickl.gadsu.tcm.Meridian
import at.cpickl.gadsu.tcm.Meridian.*
import com.google.common.collect.ComparisonChain


data class Acupunct(val meridian: Meridian, val number: Int, val note: String, val indications: List<String>) : Comparable<Acupunct> {
    companion object {

        fun punkt(meridian: Meridian, number: Int, note: String, vararg indications: String) = Acupunct(meridian, number, note, indications.toList())
    }

    override fun compareTo(other: Acupunct): Int {
        return ComparisonChain.start()
                .compare(this.meridian, other.meridian)
                .compare(this.number, other.number)
                .result()
    }

    val titleLong: String get() = "${meridian.labelLong} $number"
    val titleShort: String get() = "${meridian.labelShort} $number"

}

interface AcupunctureRepository {
    fun load(): List<Acupunct>
}

class InMemoryAcupunctureRepository : AcupunctureRepository {
    private val data = listOf(

            punkt(LargeIntestine, 1, """""", "M\u00fcdigkeit"),
            punkt(Stomach, 36, """Der gute <b>alte Magen36</b> :)""","Hals", "Nacken"),
            punkt(Heart, 1, """<h1>Fire Finger! Fire Finger, oh my fire finger!</h1>
                    Haha...<br/>Oder auch kleiner Finger.<br/>Mal <i>schauen</i>""",
                    "Husten"),
            punkt(Kidney, 15, """""", "Halsschmerzen"),
            punkt(Kidney, 16, """""", "Halsschmerzen"),
            punkt(GallBladder, 7, """Der hats in <b>vollgas</b> sich.""",
                    "Kopfweh", "Hautr\u00f6tung", "Fussschmerzen", "Augentutweh", "KannNix sehen gut")

    )

    override fun load() = data

}
