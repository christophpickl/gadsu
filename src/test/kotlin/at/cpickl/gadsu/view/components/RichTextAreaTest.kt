package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.acupuncture.Acupunct
import com.google.common.eventbus.EventBus
import gadsu.generated.Acupuncts
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class RichTextAreaTest {

    fun readComplexEnrichedText() {
        val testee = RichTextArea("viewName", EventBus())
        val text = "${RichFormat.Bold.wrap("one")} two t${RichFormat.Italic.wrap("hre")}e ${RichFormat.Bold.wrap("four")}"
        testee.readEnrichedText(text)
        assertThat(testee.toEnrichedText(), Matchers.equalTo(text))
    }

    @DataProvider
    fun dpAcupuncts(): Array<Array<Any>> = arrayOf(
            arrayOf("", emptyList<Acupunct>()),
            arrayOf("something", emptyList<Acupunct>()),
            arrayOf("Lu1", listOf(Acupuncts.Lu1)),
            arrayOf("Das Lu1 ist.", listOf(Acupuncts.Lu1)),
            arrayOf("Suffix Dot Lu1. Yes.", listOf(Acupuncts.Lu1)),
            arrayOf("Suffix Comma Lu1, Yes.", listOf(Acupuncts.Lu1)),
            arrayOf("Suffix Linebreak Lu1\nYes.", listOf(Acupuncts.Lu1)),
            arrayOf("Within Dot Lu.1", emptyList<Acupunct>()),
            arrayOf("Distinct Lu1 Lu1", listOf(Acupuncts.Lu1)),
            arrayOf("Multiple Bl1, Lu1", listOf(Acupuncts.Bl1, Acupuncts.Lu1))
    )

    @Test(dataProvider = "dpAcupuncts")
    fun `extractAcupuncts`(text: String, expected: List<Acupunct>) {
        assertThat(RichTextArea.extractAcupuncts(text), equalTo(expected))
    }

}

@Test class RichFormatExtensionTest {

    @DataProvider
    fun removeAllTagsProvider(): Array<Array<Any>> = arrayOf(
            arrayOf<Any>("", ""),
            arrayOf<Any>("a", "a"),
            arrayOf<Any>(" a ", " a "),
            arrayOf<Any>("a" + RichFormat.Bold.wrap("x"), "ax")
    )

    @Test(dataProvider = "removeAllTagsProvider")
    fun `removeAllTags works`(given: String, expected: String) {
        assertThat(given.removeAllTags(), Matchers.equalTo(expected))
    }


}

fun RichFormat.wrap(innerHtml: String) = tag1 + innerHtml + tag2
