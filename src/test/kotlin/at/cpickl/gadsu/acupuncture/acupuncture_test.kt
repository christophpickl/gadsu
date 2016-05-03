package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.tcm.model.Acupunct
import at.cpickl.gadsu.tcm.model.AcupunctureRepository
import at.cpickl.gadsu.tcm.model.Meridian
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.slf4j.LoggerFactory
import org.testng.annotations.DataProvider
import org.testng.annotations.Test



object TestAcupunctures {
    val Stomach36 = Acupunct.build(Meridian.Stomach, 36, "note1", "hals, nacken")
    val Heart1 = Acupunct.build(Meridian.Heart, 1, "note2", "husten")

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
