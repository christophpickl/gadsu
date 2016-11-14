package at.cpickl.gadsu.mail

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test class IsValidMailTest {

    @DataProvider
    fun provideValidMailAddresses(): Array<Array<out Any>> = addresses(
            "asdf.asdf@asdf.at",
            "asdf_asdf@asdf.abcde"
    )

    @DataProvider
    fun provideInvalidMailAddresses(): Array<Array<out Any>> = addresses(
            "",
            "a",
            "asdf@asdf.a",
            "asdf@asdf.a1",
            "asdf@asdf.abcdef"
    )

    private fun addresses(vararg addresses: String): Array<Array<out Any>> {
        return addresses.map { arrayOf(it) }.toTypedArray()
    }

    @Test(dataProvider = "provideValidMailAddresses")
    fun `valid mail addresses`(address: String) {
        assertThat(address.isValidMail(), equalTo(true))
        assertThat(address.isNotValidMail(), equalTo(false))
    }

    @Test(dataProvider = "provideInvalidMailAddresses")
    fun `invalid mail addresses`(address: String) {
        assertThat(address.isValidMail(), equalTo(false))
        assertThat(address.isNotValidMail(), equalTo(true))
    }
}
