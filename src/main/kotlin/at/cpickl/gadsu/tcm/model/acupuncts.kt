package at.cpickl.gadsu.tcm.model

import com.google.common.base.Splitter
import com.google.common.collect.ComparisonChain
import java.util.LinkedHashMap
import java.util.LinkedList


interface AcupunctCoordinate : Comparable<AcupunctCoordinate> {
    val meridian: Meridian
    val number: Int
}

data class AcupunctCoordinateImpl(override val meridian: Meridian, override val number: Int) :
        AcupunctCoordinate {
    override fun compareTo(other: AcupunctCoordinate): Int {
        return ComparisonChain.start()
                .compare(this.meridian, other.meridian)
                .compare(this.number, other.number)
                .result()
    }
}

data class Acupunct(val coordinate: AcupunctCoordinate, val note: String, val indications: List<String>) :
        Comparable<Acupunct> {

    companion object {
        fun build(meridian: Meridian, number: Int, note: String, joinedIndications: String): Acupunct {
            val indications = Splitter.on(",").trimResults().split(joinedIndications).toList()
            return Acupunct(AcupunctCoordinateImpl(meridian, number), note, indications)
        }

    }

    override fun compareTo(other: Acupunct) = this.coordinate.compareTo(other.coordinate)

    // delegation is not working properly due to mismatching Comparable<T> interfaces
    val meridian: Meridian = coordinate.meridian
    val number: Int = coordinate.number

    // move to and delegate by AcupunctCoordinate??
    val titleLong: String get() = "${meridian.labelLong} ${number}"
    val titleShort: String get() = "${meridian.labelShort} ${number}"
}

/**
 * Right now only contains the "important" ones. Would need to mark some of them as "important", but define EACH AND EVERY one of them here!
 */
object Acupuncts {
    private val byMeridian: Map<Meridian, List<Acupunct>>
    init {
        byMeridian = AcupunctsPunctBuilder()
            .meridian(Meridian.Lung, {
                punct(1, "", "erschwerte Atmung, Asthma, Erkältung, Brustschmerzen, Schulterschmerzen")
                punct(5, "", "trockener Husten, Schleimhusten, Bronchitis, Lungenentzündung, erschwerte Atmung, Oberarmschmerzen, Ellbogenschmerzen")
                punct(7, "", "Kurzatmigkeit, Husten, Grippe, Schleimhusten, Asthma, Erkältung, Nackenkopfschmerzen, Menstruationsbeschwerden, Unfruchtbarkeit, emotionale Beschwerden")
                punct(9, "", "Lethargie, schwache Stimme, Husten, Bluthusten, erschwerte Atmung, Kurzatmigkeit, Asthma, Brustbeklemmung, Herzbeschwerden, Kreislaufkollaps, Handgelenksschwäche")
                punct(11, "", "hohes Fieber, Hitzeschlag, Schock, Ohnmacht, Atemnot, Kreislaufkollaps, Halsentzündung, Heiserkeit, emotionale Disharmonie, Unruhe, Manie, Handgelenksbeschwerden, Daumenbeschwerden")
            })
            .meridian(Meridian.LargeIntestine, {
                punct(1, "", "abc")
                punct(4, "", "")
                punct(10, "", "")
                punct(11, "", "")
                punct(15, "", "")
                punct(16, "", "")
                punct(20, "", "")
            })
            .meridian(Meridian.Stomach, {
                punct(2, "", "")
                punct(3, "", "")
                punct(8, "", "")
                punct(9, "", "")
                punct(18, "", "")
                punct(25, "", "")
                punct(30, "", "")
                punct(34, "", "")
                punct(36, "", "")
                punct(40, "", "")
                punct(41, "", "")
                punct(44, "", "")
            })
            .meridian(Meridian.Spleen, {
                punct(1, "", "")
                punct(3, "", "")
                punct(4, "", "")
                punct(12, "", "")
                punct(15, "", "")
                punct(21, "", "")
            })
            .meridian(Meridian.Heart, {
                punct(1, "", "")
                punct(3, "", "")
                punct(7, "", "")
                punct(9, "", "")
            })
            .meridian(Meridian.SmallIntestine, {
                punct(1, "", "")
                punct(3, "", "")
                punct(8, "", "")
                punct(9, "", "")
                punct(10, "", "")
                punct(11, "", "")
                punct(15, "", "")
                punct(19, "", "")
            })
            .meridian(Meridian.UrinaryBladder, {
                punct(2, "", "")
                punct(10, "", "")
                punct(11, "", "")
                punct(36, "", "")
                punct(40, "", "")
                punct(57, "", "")
                punct(60, "", "")
                punct(62, "", "")
                punct(67, "", "")
            })
            .meridian(Meridian.Kidney, {
                punct(1, "", "")
                punct(3, "", "")
                punct(6, "", "")
                punct(7, "", "")
                punct(10, "", "")
                punct(11, "", "")
                punct(16, "", "")
                punct(27, "", "")
            })
            .meridian(Meridian.Pericardium, {
                punct(3, "", "")
                punct(6, "", "")
                punct(7, "", "")
                punct(8, "", "")
            })
            .meridian(Meridian.TripleBurner, {
                punct(4, "", "")
                punct(5, "", "")
                punct(10, "", "")
                punct(14, "", "")
                punct(17, "", "")
                punct(21, "", "")
                punct(23, "", "")
            })
            .meridian(Meridian.GallBladder, {
                punct(1, "", "")
                punct(12, "", "")
                punct(14, "", "")
                punct(20, "", "")
                punct(21, "", "")
                punct(30, "", "")
                punct(34, "", "")
                punct(40, "", "")
                punct(41, "", "")
            })
            .meridian(Meridian.Liver, {
                punct(3, "", "")
                punct(4, "", "")
                punct(5, "", "")
                punct(8, "", "")
            })
            .build()
    }

    val allPuncts = lazy {
        byMeridian.values.flatMap { it }
//        val iAmTooStupidForKotlin = LinkedList<Acupunct>()
//        byMeridian.values.forEach { iAmTooStupidForKotlin.addAll(it) }
//        iAmTooStupidForKotlin
    }

    fun byMeridian(meridian: Meridian) = byMeridian[meridian]!!
}



private class AcupunctsPunctBuilder() {

    // needs to be linked to keep ordering
    private val map = LinkedHashMap<Meridian, LinkedList<Acupunct>>()
    private lateinit var currentMeridian: Meridian

    fun meridian(meridian: Meridian, func: AcupunctsPunctBuilder.() -> Unit): AcupunctsPunctBuilder {
        currentMeridian = meridian
        with(this, func)
        return this
    }

    fun punct(number: Int, note: String, joinedIndications: String): AcupunctsPunctBuilder {
        if (!map.containsKey(currentMeridian)) {
            map.put(currentMeridian, LinkedList())
        }
        map[currentMeridian]!!.add(Acupunct.build(currentMeridian, number, note, joinedIndications))
        return this
    }

    fun build() = map
}
