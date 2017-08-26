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
    Lung(
            meridian = Meridian.Lung,
            leadSymptoms = listOf(
                    Symptom.Husten, Symptom.Heiserkeit, Symptom.Stimmbaender
            ),
            functions = listOf(
                    ZangFunction("Sendet Qi nach außen und unten"),
                    ZangFunction("Reguliert Wasserbewegung"),
                    ZangFunction("Pumpfunktion")
            )
    ),
    Spleen(meridian = Meridian.Spleen,
            leadSymptoms = listOf(
                // TODO implement us
            ),
            functions = listOf(
                    ZangFunction("")
            )
    ),
    Heart(meridian = Meridian.Heart,
            leadSymptoms = listOf(

            ),
            functions = listOf(
                    ZangFunction("")
            )
    ),
    Liver(meridian = Meridian.Liver,
            leadSymptoms = listOf(

            ),
            functions = listOf(
                    ZangFunction("")
            )
    ),
    Kidney(meridian = Meridian.Kidney,
            leadSymptoms = listOf(

            ),
            functions = listOf(
                    ZangFunction("")
            )
    )
    ;
}
