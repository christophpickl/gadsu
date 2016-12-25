package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.acupuncture.AcupunctFlag.BoPoint
import at.cpickl.gadsu.acupuncture.AcupunctFlag.Marinaportant
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.Meridian.Lung
import java.util.LinkedHashMap
import java.util.LinkedList

/**
 * Right now only contains the "important" ones. Would need to mark some of them as "important", but define EACH AND EVERY one of them here!
 */
object Acupuncts {

    private val byMeridian: Map<Meridian, List<Acupunct>>

    init {
        byMeridian = AcupunctsPunctBuilder()


                .meridian(Lung, {
                    punct(1, "Sammelzentrum", "Zhongfu",
                            localisation = "1C unterhalb Clavikula, in S-Biegung",
                            indications = "erschwerte Atmung, Asthma, Erkältung, Brustschmerzen, Schulterschmerzen",
                            flags = listOf(Marinaportant, BoPoint.Lung)) // erfahrungsstelle Lu / verbindungspunkt lunge et lienalis lt DTV atlas
                    punct(2, "Wolkentor", "Yunmen",
                            localisation = "Über Lu1, unmittelbar unterhalb der Clavikula",
                            indications = "")
                    punct(3, "Himmlischer Amtssitz", "Tianfu",
                            localisation = "Am lateralen Rand des Biceps, 3C unter Achselfalte",
                            indications = "")
                    punct(4, "Das edle Weiß", "Xiabai",
                            localisation = "1C unter Lu3",
                            indications = "")
                    punct(5, "Moor der Elle", "Chize",
                            localisation = "In Ellbogenfalte, radial Bizepssehne",
                            indications = "trockener Husten, Schleimhusten, Bronchitis, Lungenentzündung, erschwerte Atmung, Oberarmschmerzen, Ellbogenschmerzen",
                            flags = listOf(Marinaportant, AcupunctFlag.ElementPoint.Water, AcupunctFlag.SedatePoint)) // He/Eintrittspunkt
                    punct(6, "Tiefes Loch", "Kongzui",
                            localisation = "An der radialen Innenseite des Unterarmes, 7C proximal von Lu9",
                            indications = "")// RIM
                    punct(7, "Reihenlücke", "Lieque",
                            localisation = "1.5C proximal Handgelenk, Vertiefung proximal Proc. styloideus radii, zwischen Sehne Abductor Pollicis Longus und Radiuskante",
                            indications = "Kurzatmigkeit, Husten, Grippe, Schleimhusten, Asthma, Erkältung, Nackenkopfschmerzen, Menstruationsbeschwerden, Unfruchtbarkeit, emotionale Beschwerden",
                            flags = listOf(Marinaportant, AcupunctFlag.NexusPoint))// schluesselpunkt KG
                    punct(8, "Hindurchgehender Meridian", "Jingqu",
                            localisation = "1C proximal Handgelenk, radial der Arteria radialis, Höhe von Proc. styloideus radii",
                            indications = "")
                    punct(9, "Tiefer Abgrund", "Taiyuan",
                            localisation = "Im Handgelenk in einer Vertiefung radial der Arteria radialis und ulnar der Sehne des Abductor pollicis longus; radialer Puls spürbar",
                            indications = "Lethargie, schwache Stimme, Husten, Bluthusten, erschwerte Atmung, Kurzatmigkeit, Asthma, Brustbeklemmung, Herzbeschwerden, Kreislaufkollaps, Handgelenksschwäche",
                            flags = listOf(Marinaportant, AcupunctFlag.ElementPoint.Earth, AcupunctFlag.OriginalPoint, AcupunctFlag.MasterPoint/*for Gefaesssysteme*/, AcupunctFlag.TonePoint))
                    punct(10, "Fischgrenze", "Yuji",
                            localisation = "Auf dem Daumenballen in der Mitte des ersten Mittelhandknochens an der Grenze weißes-rotes Fleisch",
                            indications = "") // feuer
                    punct(11, "Kleine Lunge", "Shaoshang",
                            localisation = "Am radialen Nagelfalzwinkel des Daumens",
                            indications = "hohes Fieber, Hitzeschlag, Schock, Ohnmacht, Atemnot, Kreislaufkollaps, Halsentzündung, Heiserkeit, emotionale Disharmonie, Unruhe, Manie, Handgelenksbeschwerden, Daumenbeschwerden",
                            flags = listOf(Marinaportant, AcupunctFlag.ElementPoint.Wood))
                })

                .meridian(Meridian.LargeIntestine, {
                })

                .meridian(Meridian.Stomach, {
                })

                .meridian(Meridian.Spleen, {
                    // punct 4, AcupunctFlag.KeyPoint.ChongMai
                })

                .meridian(Meridian.Heart, {
                })

                .meridian(Meridian.SmallIntestine, {
                })

                .meridian(Meridian.UrinaryBladder, {
                })

                .meridian(Meridian.Kidney, {
                })

                .meridian(Meridian.Pericardium, {
                })

                .meridian(Meridian.TripleBurner, {
                })

                .meridian(Meridian.GallBladder, {
                })

                .meridian(Meridian.Liver, {
                })
                .build()
    }

    val allPuncts = lazy {
        byMeridian.values.flatMap { it }
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

    fun punct(
            number: Int,
            germanName: String,
            chineseName: String,
            localisation: String,
            indications: String = "",
            note: String = "",
            flags: List<AcupunctFlag> = emptyList()
    ): AcupunctsPunctBuilder {
        if (!map.containsKey(currentMeridian)) {
            map.put(currentMeridian, LinkedList())
        }
        map[currentMeridian]!!.add(Acupunct.build(currentMeridian, number, germanName, chineseName, note, localisation, indications, flags))
        return this
    }

    fun build() = map
}
