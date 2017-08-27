package at.cpickl.gadsu.view.components

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class WordDetectorTest {

    @DataProvider
    fun dp(): Array<Array<out Any?>> = arrayOf(
            arrayOf("", 0, null),
            arrayOf("a", 1, "a"),
            arrayOf("ab", 1, "a"),
            arrayOf("ab", 2, "ab"),
            arrayOf("a b", 3, "b"),
            arrayOf("a bc", 3, "b"),
            arrayOf("ab ", 3, null)
    )

    @Test(dataProvider = "dp")
    fun `extractPreviousWord`(text: String, position: Int, expectedWord: String?) {
        assertThat(extractPreviousWord(text, position), equalTo(expectedWord))
    }

}
