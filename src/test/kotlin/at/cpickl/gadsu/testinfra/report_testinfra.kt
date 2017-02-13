package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.treatment.dyn.treats.BloodPressure
import at.cpickl.gadsu.treatment.dyn.treats.BloodPressureMeasurement
import at.cpickl.gadsu.treatment.dyn.treats.HaraDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.MeridianAndPosition
import at.cpickl.gadsu.treatment.dyn.treats.PulseDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.PulseProperty
import at.cpickl.gadsu.treatment.dyn.treats.TongueDiagnosis
import at.cpickl.gadsu.treatment.dyn.treats.TongueProperty


val TEST_DYNTREAT_HARA = HaraDiagnosis(
        kyos = listOf(MeridianAndPosition.UrinaryBladderBottom),
        jitsus = listOf(MeridianAndPosition.Liver, MeridianAndPosition.GallBladder),
        bestConnection = MeridianAndPosition.UrinaryBladderBottom to MeridianAndPosition.Liver,
        note = "* rechts mehr kyo\n* insgesamt sehr hohe spannung"
)

val TEST_DYNTREAT_TONGUE = TongueDiagnosis(
        color = listOf(TongueProperty.Color.RedTip, TongueProperty.Color.Pink),
        shape = listOf(TongueProperty.Shape.Long),
        coat = listOf(TongueProperty.Coat.Yellow, TongueProperty.Coat.Thick),
        special = listOf(TongueProperty.Special.MiddleCrack),
        note = "* zunge gruen")

val TEST_DYNTREAT_PULSE = PulseDiagnosis(
        properties = listOf(PulseProperty.Ascending, PulseProperty.Deep),
        note = "* war irgendwie \"zaeh\""
)

val TEST_DYNTREAT_BLOOD = BloodPressure(
        before = BloodPressureMeasurement(90, 110, 80),
        after = BloodPressureMeasurement(80, 100, 70)
)
