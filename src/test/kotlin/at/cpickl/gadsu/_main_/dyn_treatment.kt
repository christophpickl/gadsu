package at.cpickl.gadsu._main_

import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.view.components.Framed

fun main(args: Array<String>) {
    Framed.show(HaraDiagnosisRenderer(HaraDiagnosis.insertPrototype()).view)
}
