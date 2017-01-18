package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test


@Test class FreemarkerTemplatingEngineTest {

    fun `process simple`() {
        assertThat(
                process("hello \${name}!",
                        mapOf("name" to "gadsu")),
                equalTo("hello gadsu!"))
    }

    fun `process data object`() {
        assertThat(
                process("hello \${data.name} you are \${data.age}",
                        mapOf("data" to TemplateDummyData(name = "gadsu", age = 42))),
                equalTo("hello gadsu you are 42"))
    }


    @Test(expectedExceptions = arrayOf(FreemarkerInvalidReferenceException::class))
    fun `process fails on invalid reference`() {
        process("hello \${name}!", emptyMap())
    }


    private val someDate = "1.2.2001 14:30:59".parseDateTime()
    @DataProvider fun provideFormats(): Array<Array<Any>> = arrayOf<Array<Any>>(
            arrayOf<Any>("\${date?string[\"d.M.\"]}", "1.2."),
            arrayOf<Any>("\${date?string[\"EEEE 'der' d. MMMMM\"]}", "Donnerstag der 1. Februar"),
            arrayOf<Any>("\${date?string[\"HH:mm\"]}", "14:30")
    )

    @Test(dataProvider = "provideFormats")
    fun `process complex date formats`(template: String, expected: String) {
        assertThat(process(template, mapOf("date" to someDate.toDate())),
                equalTo(expected))
    }

    private fun process(templateText: String, data: Map<String, Any>) =
            FreemarkerTemplatingEngine().process(templateText, data)

    data class TemplateDummyData(val name: String, val age: Int)

}
