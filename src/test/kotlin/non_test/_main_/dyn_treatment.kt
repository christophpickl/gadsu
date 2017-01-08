@file:Suppress("unused")

package non_test._main_

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureMeasurement
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureRenderer
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
            pulseDiagnosis().view
//            haraDiagnosis().view
//            tongueDiagnosis().view
//        bloodPressure().view
    )
}

private fun haraDiagnosis() =
        HaraDiagnosisRenderer(HaraDiagnosis(
                kyos = listOf(MeridianAndPosition.LungLeft, MeridianAndPosition.KidneyBottom),
                jitsus = listOf(MeridianAndPosition.LargeIntestineLeft, MeridianAndPosition.Pericardium),
                bestConnection = Pair(MeridianAndPosition.LungLeft, MeridianAndPosition.LargeIntestineLeft),
                note = "my note"
        ))

private fun tongueDiagnosis() =
        TongueDiagnosisRenderer(TongueDiagnosis(
                color = listOf(TongueProperty.Color.Pale),
                shape = listOf(TongueProperty.Shape.Swollen),
                coat = listOf(TongueProperty.Coat.Yellow),
                special = listOf(TongueProperty.Special.RedDots, TongueProperty.Special.MiddleCrack),
                note = "test note"
        ), EventBus())


private fun pulseDiagnosis() =
        PulseDiagnosisRenderer(PulseDiagnosis(
                properties = listOf(PulseProperty.Superficial, PulseProperty.Full, PulseProperty.Raugh),
                note = "test note"
        ), EventBus())



private fun bloodPressure() =
        BloodPressureRenderer(BloodPressure(
                before = BloodPressureMeasurement(80, 120, 60),
                after = BloodPressureMeasurement(70, 110, 50)
                ))
