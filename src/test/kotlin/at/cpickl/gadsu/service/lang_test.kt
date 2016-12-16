package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test


@Test class LangTest {

    @DataProvider fun stringTimesProvider(): Array<Array<Any>> = arrayOf(
            arrayOf("x", 0, ""),
            arrayOf("x", 1, "x"),
            arrayOf("x", 2, "xx")
    )

    @Test(dataProvider = "stringTimesProvider")
    fun `string X times`(symbol: String, count: Int, expected: String) {
        assertThat(symbol.times(count),
                equalTo(expected))
    }

}
