package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.tcm.model.ZangOrgan

val ZangOrgan.leitSymptome get() = listOf(
        Symptom.Husten,
        Symptom.Heiserkeit,
        Symptom.Stimmbaender
)

enum class OrganSyndrome(
        val label: String,
        val organ: ZangOrgan,
        val part: SyndromePart? = null,
        val tendency: SyndromeTendency,

        val externalFactors: List<ExternalPathos> = emptyList(),
        val symptoms: Set<Symptom>
) {

    LuQiMangel(
            label = "Lu-Qi-Mangel",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = SyndromeTendency.Mangel,
            symptoms = setOf(
                    Symptom.Kurzatmigkeit,
                    Symptom.Husten,
                    Symptom.Asthma,
                    Symptom.FlacheAtmung,

                    Symptom.WenigLeiseSprechen,
                    Symptom.EnergieMangel,
                    Symptom.Muedigkeit,
                    Symptom.Blaesse,
                    Symptom.TrauererloseDepression,

                    Symptom.LeichtesSchwitzen,
                    Symptom.Erkaeltungen,
                    Symptom.AversionKaelte,

                    Symptom.PulsSchwach,
                    Symptom.PulsWeich,
                    Symptom.PulsLeer
                    )),

    LuYinMangel(
            label = "Lu-Yin-Mangel",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Yin,
            tendency = SyndromeTendency.Mangel,
            symptoms = LuQiMangel.symptoms.plus(setOf(
                    Symptom.Heiserkeit,
                    Symptom.TrockenerHals,
                    Symptom.TrockenerHusten,
                    Symptom.HitzeGefuehlAbends,

                    Symptom.ZungeRot,
                    Symptom.ZungeTrocken,
                    Symptom.WenigBelag,

                    Symptom.PulsBeschleunigt,
                    Symptom.PulsDuenn,
                    Symptom.PulsSchwach
            )))

}
