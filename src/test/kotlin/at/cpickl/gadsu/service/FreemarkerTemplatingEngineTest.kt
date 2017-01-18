package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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

    private fun process(templateText: String, data: Map<String, Any>) =
            FreemarkerTemplatingEngine().process(templateText, data)

    data class TemplateDummyData(val name: String, val age: Int)

}
