package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.testinfra.SimpleUiTest
import at.cpickl.gadsu.testinfra.TEST_DATE2
import at.cpickl.gadsu.testinfra.TestViewStarter
import org.joda.time.DateTime
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import javax.swing.JPanel

@Test(groups = arrayOf("uiTest"))
class DatePickerUiTest : SimpleUiTest() {
    companion object {
        private val VIEWNAME_BUTTON = "testButton"
        private val VIEWNAME_PANEL = "testPanel"
        private val VIEWNAME_TEXT = "testText"
    }
    private val container = JPanel()
    private var _driver: DatePickerDriver? = null
    private val driver: DatePickerDriver get() = _driver!!

    init {
        container.layout = BorderLayout()
    }

    override fun newMainClassAdapter(): MainClassAdapter {
        TestViewStarter.componentToShow = container
        return MainClassAdapter(TestViewStarter::class.java)
    }

    override fun postInit(window: Window) {
        _driver = DatePickerDriver(this, window, VIEWNAME_BUTTON, VIEWNAME_PANEL, VIEWNAME_TEXT)
    }

    fun `at startup without initial date, date picker should be nulled and deselected`() {
        testee(null)
        driver.assertNothingSelected()
    }

    fun `at startup with initial date, date picker should have selected that value`() {
        testee(TEST_DATE2)
        driver.assertSelected(TEST_DATE2.clearTime())
    }

    private fun testee(initial: DateTime?): MyDatePicker {
        container.removeAll()
        val testee =  MyDatePicker.build(initial, VIEWNAME_BUTTON, VIEWNAME_PANEL, VIEWNAME_TEXT)
        container.add(testee, BorderLayout.CENTER)
        return testee
    }

}
