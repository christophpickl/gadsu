package at.cpickl.gadsu.tcm.patho

import at.cpickl.gadsu.tcm.model.ZangOrgan


enum class OrganSyndrome(
        val label: String,
        val organ: ZangOrgan,
        val part: SyndromePart? = null,
        val tendency: SyndromeTendency,

        val externalFactors: List<ExternalPathos> = emptyList(),
        val symptoms: List<Symptom>
) {

    LuQiMangel(
            label = "Lu-Qi-Mangel",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Qi,
            tendency = SyndromeTendency.Mangel,
            symptoms = listOf(Symptom.BigHunger, Symptom.Traeumen, Symptom.EinschlafStoerungen)),

    LuYinMangel(
            label = "Lu-Yin-Mangel",
            organ = ZangOrgan.Lung,
            part = SyndromePart.Yin,
            tendency = SyndromeTendency.Mangel,
//            symptoms = LuQiMangel.symptoms.plus(listOf(Symptom.LittleHunger)))
            symptoms = listOf(Symptom.LittleHunger))

}
