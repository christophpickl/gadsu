package at.cpickl.gadsu

import at.cpickl.gadsu.testinfra.Expects.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class SystemPropertiesSPTest {

    private val key = "SystemPropertiesSPTest_key"

    @BeforeMethod
    fun resetKey() {
        System.clearProperty(key)
    }

    fun `spReadString, not initialized returns null or default`() {
        assertThat(key.spReadStringOrNull(), nullValue())
        assertThat(key.spReadString("def"), equalTo("def"))
    }

    fun `spReadString, sunshine`() {
        setProperty("foo")
        assertThat(key.spReadString("def"), equalTo("foo"))
        assertThat(key.spReadStringOrNull(), equalTo("foo"))
    }


    fun `spReadBooleanOrNull, not initialized, set to null`() {
        assertThat(key.spReadBooleanOrNull(), nullValue())
    }

    fun `spReadBoolean, sunshine`() {
        setProperty("true"); assertThat(key.spReadBooleanOrNull(), equalTo(true))
        setProperty("TrUe"); assertThat(key.spReadBooleanOrNull(), equalTo(true))
        setProperty("1"); assertThat(key.spReadBooleanOrNull(), equalTo(true))

        setProperty("false"); assertThat(key.spReadBooleanOrNull(), equalTo(false))
        setProperty("FaLsE"); assertThat(key.spReadBooleanOrNull(), equalTo(false))
        setProperty("0"); assertThat(key.spReadBooleanOrNull(), equalTo(false))
    }

    fun `spReadBoolean, invalid value, fails`() {
        setProperty("fuchur")
        expect(GadsuException::class, {key.spReadBooleanOrNull()}, "fuchur")
    }

    fun `spWriteString`() {
        key.spWriteString("foo")
        assertProperty("foo")
    }

    fun `spWriteTrue`() {
        key.spWriteTrue()
        assertProperty("true")
    }

    fun `spWriteFalse`() {
        key.spWriteFalse()
        assertProperty("false")
    }

    fun `spClear`() {
        setProperty("abc")
        key.spClear()
        assertProperty(null)
    }

    private fun setProperty(value: String) {
        System.setProperty(key, value)
    }

    private fun assertProperty(expected: String?) {
        assertThat(System.getProperty(key), equalTo(expected))
    }

}
