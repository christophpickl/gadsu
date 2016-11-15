package at.cpickl.gadsu._main_

import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.view.components.Framed

fun main(args: Array<String>) {
    Framed.show(HaraDiagnosisRenderer(HaraDiagnosis(
            kyos = listOf(MeridianAndPosition.LungLeft, MeridianAndPosition.KidneyBottom),
            jitsus = listOf(MeridianAndPosition.LargeIntestineLeft, MeridianAndPosition.Pericardium),
            bestConnection = Pair(MeridianAndPosition.LungLeft, MeridianAndPosition.LargeIntestineLeft),
            note = "my note"
    )).view)
}
