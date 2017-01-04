package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.view.TreatCount.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class TreatCountTest {

    @DataProvider
    fun listifyDp(): Array<Array<Any>> = arrayOf(
            arrayOf<Any>(0, emptyList<TreatCount>()),
            arrayOf<Any>(1, listOf(Count1)),
            arrayOf<Any>(4, listOf(Count1, Count1, Count1, Count1)),
            arrayOf<Any>(5, listOf(Count5)),
            arrayOf<Any>(6, listOf(Count5, Count1)),
            arrayOf<Any>(9, listOf(Count5, Count1, Count1, Count1, Count1)),
            arrayOf<Any>(10, listOf(Count10)),
            arrayOf<Any>(11, listOf(Count10, Count1)),
            arrayOf<Any>(16, listOf(Count10, Count5, Count1))
    )

    @Test(dataProvider = "listifyDp")
    fun `listify`(count: Int, expected: List<TreatCount>) {
        assertThat(TreatCount.listify(count), equalTo(expected))
    }

}
