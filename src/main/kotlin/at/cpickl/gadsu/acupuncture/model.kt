package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.tcm.Meridian
import com.google.common.collect.ComparisonChain


data class Acupunct(val meridian: Meridian, val number: Int, val indications: List<String>) : Comparable<Acupunct> {
    companion object {

        fun build(meridian: Meridian, number: Int, vararg indications: String) = Acupunct(meridian, number, indications.toList())
    }

    override fun compareTo(other: Acupunct): Int {
        return ComparisonChain.start()
                .compare(this.meridian, other.meridian)
                .compare(this.number, other.number)
                .result()
    }
}

interface AcupunctureRepository {
    fun load(): List<Acupunct>
}

class InMemoryAcupunctureRepository : AcupunctureRepository {
    private val data = listOf(
            Acupunct.build(Meridian.Stomach, 36, "Hals", "Nacken"),
            Acupunct.build(Meridian.Heart, 1, "Husten")
    )

    override fun load() = data

}
