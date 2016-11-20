@file:Suppress("unused")

package at.cpickl.gadsu._main_

import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosisRenderer
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty
import at.cpickl.gadsu.view.components.Framed
import com.google.common.eventbus.EventBus

fun main(args: Array<String>) {
    Framed.show(
            pulseDiagnosis()
//            haraDiagnosis()
//            tongueDiagnosis()

    )
}

private fun haraDiagnosis() =
        HaraDiagnosisRenderer(HaraDiagnosis(
                kyos = listOf(MeridianAndPosition.LungLeft, MeridianAndPosition.KidneyBottom),
                jitsus = listOf(MeridianAndPosition.LargeIntestineLeft, MeridianAndPosition.Pericardium),
                bestConnection = Pair(MeridianAndPosition.LungLeft, MeridianAndPosition.LargeIntestineLeft),
                note = "my note"
        )).view

private fun tongueDiagnosis() =
        TongueDiagnosisRenderer(TongueDiagnosis(
                color = listOf(TongueProperty.Color.Pale),
                shape = listOf(TongueProperty.Shape.Swollen),
                coat = listOf(TongueProperty.Coat.Yellow),
                special = listOf(TongueProperty.Special.RedDots, TongueProperty.Special.MiddleCrack),
                note = "test note"
        ), EventBus()).view


private fun pulseDiagnosis() =
        PulseDiagnosisRenderer(PulseDiagnosis(
                properties = listOf(PulseProperty.Superficial),
                note = "test note"
        ), EventBus()).view
