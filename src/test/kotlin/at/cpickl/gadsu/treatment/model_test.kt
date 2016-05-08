package at.cpickl.gadsu.treatment

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test
class TreatmentTest {

    fun `comparable, given treatments 1, 3, 2, should return in reversed order 3, 2, 1`() {
        assertNumbered(treatments(1, 3, 2).sorted(), 1, 2, 3)
    }

    fun `comparable, given treatments 3-7-2-16, should return even with gaps 2, 3, 7, 16`() {
        assertNumbered(treatments(3, 7, 2, 16).sorted(), 2, 3, 7, 16)
    }

    private fun assertNumbered(actual: List<Treatment>, vararg expectedNumbers: Int) {
        assertThat(actual, Matchers.hasSize(expectedNumbers.size))
        actual.forEachIndexed { i, it -> assertThat("Expected treatment on index [$i] to have number [${expectedNumbers[i]}], but was: ${it.number}!",
                it.number, equalTo(expectedNumbers[i])) }
//        MatcherAssert.assertThat(actual.map { it.number }, Matchers.contains(xxx)) ... i dont get kotlin/varargs/mockito
    }

    private fun treatments(vararg numbers: Int) = numbers.map { treatment(it) }

    private fun treatment(number: Int) = Treatment.savedValidInstance(clientId = "clientId", number = number)

}
