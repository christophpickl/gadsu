package at.cpickl.gadsu.view

import at.cpickl.gadsu.testinfra.Expects
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.Locale

@Test
class LanguageTest {

    @BeforeMethod
    fun resetState() {
        System.clearProperty("gadsu.overrideLanguage")
        defaultLocale(Locale.KOREAN)
    }

    @DataProvider(name = "defaultLocaleProvider")
    fun defaultLocaleProvider(): Array<Array<out Any>> = arrayOf(
            arrayOf(Locale.GERMAN, "DE"),
//            arrayOf(Locale.ENGLISH, "EN"),
            arrayOf(Locale.ITALIAN, "DE") // default to DE
    )

    @Test(dataProvider = "defaultLocaleProvider")
    fun `change default locale`(locale: Locale, expected: String) {
        defaultLocale(locale)
        assertInitLang(expected)
    }

    fun `override language vis system property`() {
        overrideLang("DE")
        assertInitLang("DE")

//        overrideLang("EN")
//        assertInitLang("EN")

        Expects.expect(
                type = LanguageException::class,
                messageContains = "ABC",
                action = {
                    overrideLang("ABC")
                    initLang()
                })
    }

    private fun assertInitLang(expected: String) {
        assertThat(initLang().id, equalTo(expected))
    }

    private fun initLang() = Languages._initLanguage()

    private fun overrideLang(value: String) {
        System.setProperty("gadsu.overrideLanguage", value)
    }

    private fun defaultLocale(locale: Locale) {
        Locale.setDefault(locale)
    }
}

@Test
class LabelsTest {

    @DataProvider(name = "labelAndLang")
    fun labelAndLang(): Array<Array<Any>> = arrayOf(
            arrayOf(Buttons::class.java as Any, "DE" as Any, Labels.Buttons_DE as Any)
//            arrayOf(Buttons::class.java as Any, "EN" as Any, Labels.Buttons_EN as Any)
//            arrayOf(Tabs::class.java as Any, Language.DE as Any, Labels.Tabs_DE as Any),
//            arrayOf(Tabs::class.java as Any, Language.EN as Any, Labels.Tabs_EN as Any)

    )

    @Test(dataProvider = "labelAndLang")
    fun `find LABEL for LANGUAGE`(requestType: Class<Any>, requestLanguage: String, expected: Any) {
        assertLabel(requestType, requestLanguage, expected)
    }

    fun `find for not existing language, defaults to DE`() {
        assertLabel(Tabs::class.java, "XXX", Labels.Tabs_DE)
        assertLabel(Buttons::class.java, "XXX", Labels.Buttons_DE)
    }

    fun `find for not existing label, throws exception`() {
        Expects.expect(
                type = LanguageException::class,
                action = { find(TestLabels::class.java, "DE") }
        )
    }

    private fun assertLabel(requestType: Class<out Any>, requestLanguage: String, expected: Any) {
        val actual = find(requestType, requestLanguage)
        Assert.assertTrue(actual === expected, "Expected: $expected, Actual: $actual")
    }

    private fun find(requestType: Class<out Any>, requestLanguage: String): Any {
        val lang = Language.byId(requestLanguage) ?: throw AssertionError("Invalid language ID '${requestLanguage}'!")
        return LabelsLanguageFinder.findForLanguage(requestType, lang)
    }


}
