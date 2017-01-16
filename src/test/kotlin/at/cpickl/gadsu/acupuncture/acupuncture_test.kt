package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.tcm.model.Meridian
import gadsu.generated.Acupuncts
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.equalTo
import org.slf4j.LoggerFactory
import org.testng.Assert.fail
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.LinkedList


object TestAcupunctures {
    val Stomach36 = Acupunct.build(Meridian.Stomach, 36, "DeName Ma36", "ChinName Ma36", "note1", "1C darunter", "hals, nacken", listOf(AcupunctFlag.Marinaportant, AcupunctFlag.ElementPoint.Earth))
    val Heart1 = Acupunct.build(Meridian.Heart, 1, "DeName He1", "ChinName He1", "note2", "", "husten", emptyList())

    val all = listOf(Stomach36, Heart1)
}

class TestableAcupunctureRepository : AcupunctureRepository {
    override fun loadAll() = TestAcupunctures.all
}


@Test class AcupunctureServiceImplTest {

    private val log = LoggerFactory.getLogger(javaClass)

    @DataProvider(name = "luckyDataProvider")
    fun luckyDataProvider(): Array<Array<out Any>> = arrayOf(
            arrayOf("nacken", listOf(TestAcupunctures.Stomach36)),

            // case insensitive
            arrayOf("NaCkEn", listOf(TestAcupunctures.Stomach36)),

            // sub part
            arrayOf("ack", listOf(TestAcupunctures.Stomach36)),
            arrayOf("s", listOf(TestAcupunctures.Stomach36, TestAcupunctures.Heart1)),
            arrayOf("", emptyList<Acupunct>()),
            arrayOf("  ", emptyList<Acupunct>())
    )

    @Test(dataProvider = "luckyDataProvider")
    fun `find _, should return _`(searchTerm: String, expectedAcupuncts: List<Acupunct>) {
        val actual = testee().find(searchTerm)
        log.debug("find [{}], should return [{}] => was: {}", searchTerm, expectedAcupuncts, actual)
        assertThat(actual, containsInAnyOrder(*expectedAcupuncts.toTypedArray()))
//        assertThat(testee().find("Nacken"), Matchers.contains(TestableAcupunctureRepository.Stomach36))
    }

    private fun testee(): AcupunctureService = AcupunctureServiceImpl(TestableAcupunctureRepository())

}

@Test class AcupunctCoordinateTest {

    @BeforeClass fun initAcupuncts() {
        disableAcupunctVerification = true
        Acupuncts.enforceEagerLoading()
        disableAcupunctVerification = false

        val es = LinkedList<GadsuException>()
        Acupunct.all().forEach {
            try {
                it.verifyFlags()
            } catch(e: GadsuException) {
                es += e
            }
        }

        if (es.isNotEmpty()) {
            fail("Invalid flags found!\n-> " + es.map { it.message }.joinToString("\n"))
        }
    }

    @DataProvider
    fun potentialProvider(): Array<Array<out Any>> = arrayOf(
            arrayOf("Lu1", true),
            arrayOf("Lu49", true),
            arrayOf("3E33", true),
            arrayOf("He99", true),
            arrayOf("He100", false),
            arrayOf("", false),
            arrayOf("a", false),
            arrayOf("Ab1", false),
            arrayOf("He0", false),
            arrayOf("3e", false),
            arrayOf("He", false),
            arrayOf("he", false),
            arrayOf("He 1", false)
    )

    @Test(dataProvider = "potentialProvider")
    fun `potential`(search: String, expected: Boolean) {
        assertThat(AcupunctCoordinate.isPotentialLabel(search), equalTo(expected))
    }

    @DataProvider
    fun allCoordinateLabelsProvider(): Array<Array<out Any>> = Acupunct.all().map { arrayOf(it.coordinate.label) }.toTypedArray()

    @Test(dataProvider = "allCoordinateLabelsProvider")
    fun `isPotentialLabel for all acupuncts`(label: String) {
        assertThat(AcupunctCoordinate.isPotentialLabel(label), equalTo(true))
    }

    @DataProvider
    fun byLabelProvider(): Array<Array<out Any?>> = arrayOf(
            arrayOf("Lu1", AcupunctCoordinate(Meridian.Lung, 1)),
            arrayOf("Lu1", Meridian.Lung.acupuncts[0].coordinate),
            arrayOf("Lu12", null)
    )

    @Test(dataProvider = "byLabelProvider")
    fun `AcupunctCoordinate byLabel`(label: String, expected: AcupunctCoordinate?) {
        assertThat(AcupunctCoordinate.byLabel(label), equalTo(expected))
    }
}

@Test class AcupunctTest {

    @DataProvider
    fun byCoordinateProvider(): Array<Array<out Any?>> = arrayOf(
            arrayOf(AcupunctCoordinate(Meridian.Lung, 1), Meridian.Lung.acupuncts[0] as Any?),
            arrayOf(AcupunctCoordinate(Meridian.Lung, 50), null)
    )

    @Test(dataProvider = "byCoordinateProvider")
    fun `Acupunct byCoordinate`(coordinate: AcupunctCoordinate, expected: Acupunct?) {
        assertThat(Acupunct.byCoordinate(coordinate), equalTo(expected))
    }

    @DataProvider
    fun allCoordinatesProvider(): Array<Array<out Any>> = Acupunct.all().map { arrayOf(it.coordinate, it) }.toTypedArray()

    @Test(dataProvider = "allCoordinatesProvider")
    fun `byCoordinate for all acupuncts`(coordinate: AcupunctCoordinate, expected: Acupunct) {
        assertThat(Acupunct.byCoordinate(coordinate), equalTo(expected))
    }

}
