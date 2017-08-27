package at.cpickl.gadsu.view.components

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class WordDetectorTest {

    @DataProvider
    fun extractPreviousWordProvider(): Array<Array<out Any?>> = arrayOf(
            arrayOf("", 0, null),
            arrayOf("a", 1, "a"),
            arrayOf("ab", 1, "a"),
            arrayOf("ab", 2, "ab"),
            arrayOf("a b", 3, "b"),
            arrayOf("a bc", 3, "b"),
            arrayOf("ab ", 3, null)
    )

    @Test(dataProvider = "extractPreviousWordProvider")
    fun `extractPreviousWord`(text: String, position: Int, expectedWord: String?) {
        assertThat(extractPreviousWord(text, position), equalTo(expectedWord))
    }

    @DataProvider
    fun extractWordAtProvider(): Array<Array<out Any?>> = arrayOf(
            arrayOf("", 0, null),
            arrayOf("", 1, null),
            arrayOf("a", 0, "a"),
            arrayOf("a", 1, "a"),
            arrayOf("ab", 0, "ab"),
            arrayOf("ab", 1, "ab"),
            arrayOf("ab ", 2, "ab"),
            arrayOf("ab ", 3, null),
            arrayOf("a b", 0, "a"),
            arrayOf("a b", 1, "a"),
            arrayOf("a b", 2, "b"),
            arrayOf("a b", 3, "b"),
            arrayOf("a b", 4, null),
            arrayOf("abc def", 0, "abc"),
            arrayOf("abc def", 1, "abc"),
            arrayOf("abc def", 2, "abc"),
            arrayOf("abc def", 3, "abc"),
            arrayOf("abc def", 4, "def"),
            arrayOf("abc def", 5, "def"),
            arrayOf("abc def", 6, "def"),
            arrayOf("abc def", 7, "def"),
            arrayOf("abc def", 8, null)
    )

    @Test(dataProvider = "extractWordAtProvider")
    fun `extractWordAt`(text: String, position: Int, expectedWord: String?) {
        assertThat(extractWordAt(text, position), equalTo(expectedWord))
    }

}
