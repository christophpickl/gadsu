package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.YinYang


enum class ZangFu(val yy: YinYang) {
    Zang(YinYang.Yin),
    Fu(YinYang.Yang)
}

data class ZangFunction(
        val label: String
)

enum class ZangOrgan(
        val meridian: Meridian,
        val leadSymptoms: List<Symptom>,
        val functions: List<ZangFunction>
) {
    Liver(meridian = Meridian.Liver,
            functions = listOf(
                    ZangFunction("Blut speichern"),
                    ZangFunction("Freier Qi-Fluss"),
                    ZangFunction("Emotionen harmonisieren"),
                    ZangFunction("Menstruation harmonisieren"),
                    ZangFunction("Verdauung harmonisieren"),
                    ZangFunction("4 vitale Richtungen")
            ),
            leadSymptoms = listOf(
                    Symptom.WechselhaftAllgemein,
                    Symptom.EmotionalAllgemein,
                    Symptom.MenstruationAllgemein
            )
    ),
    Heart(meridian = Meridian.Heart,
            functions = listOf(
                    ZangFunction("Blut/-gefäße regieren (Zirkulation, Geschmeidigkeit)")
            ),
            leadSymptoms = listOf(
                    Symptom.Palpitationen,
                    Symptom.Schlafstoerungen,
                    Symptom.SpracheAllgemein
            )
    ),
    Spleen(meridian = Meridian.Spleen,
            functions = listOf(
                    ZangFunction("Transformation, Transportation"),
                    ZangFunction("Reine rauf, Unreine runter"),
                    ZangFunction("Blut produzieren"),
                    ZangFunction("Haltefunktion (Blutgefäße, Organe)")
            ),
            leadSymptoms = listOf(
                    Symptom.VerdauungsProbleme
            )
    ),
    Lung(
            meridian = Meridian.Lung,
            functions = listOf(
                    ZangFunction("Qi und Atmung regieren"),
                    ZangFunction("Qi und JinYe verteilen"),
                    ZangFunction("Wasser bewegen"),
                    ZangFunction("Körperoberfläche herrschen"),
                    ZangFunction("Blut/-gefäße reinigen"),
                    ZangFunction("Pumpfunktion")
            ),
            leadSymptoms = listOf(
                    Symptom.Husten,
                    Symptom.Heiserkeit,
                    Symptom.Stimmbaender
            )
    ),
    Kidney(meridian = Meridian.Kidney,
            functions = listOf(
                    ZangFunction("Jing speichern"),
                    ZangFunction("Wurzel von Yin und Yang"),
                    ZangFunction("Wasser regulieren"),
                    ZangFunction("Qi aus Atmung einfangen")
            ),
            leadSymptoms = listOf(
                    Symptom.LendenBereichAllgemein,
                    Symptom.KnieAllgemein,
                    Symptom.FussknoechelAllgemein
            )
    )
    ;

    val syndromes: List<OrganSyndrome> by lazy { OrganSyndrome.values().filter { it.organ == this } }

}
