package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.testinfra.TEST_DATETIME2
import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.ui.DateSpecPicker
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import org.joda.time.DateTime
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import javax.swing.JPanel

@Test(groups = arrayOf("uiTest"))
class DatePickerUiTest : SimpleUiTest() {
    companion object {
        private val VIEWNAME_PREFIX = "myTest"
    }
    private val container = JPanel(BorderLayout())
    private var _testee: DateSpecPicker? = null
    private val testee: DateSpecPicker get() = _testee!!

    override fun newMainClassAdapter(): MainClassAdapter {
        TestViewStarter.componentToShow = container
        return MainClassAdapter(TestViewStarter::class.java)
    }

    override fun postInit(window: Window) {
        _testee = DateSpecPicker(this, window, VIEWNAME_PREFIX)
    }

    fun `at startup without initial date, date picker should be nulled and deselected`() {
        testee(null)
        testee.assertNothingSelected()
    }

    fun `at startup with initial date, date picker should have selected that value`() {
        testee(TEST_DATETIME2)
        testee.assertSelected(TEST_DATETIME2.clearTime())
    }

    private fun testee(initial: DateTime?): MyDatePicker {
        container.removeAll()
        val testee =  MyDatePicker.build(initial, VIEWNAME_PREFIX)
        container.add(testee, BorderLayout.CENTER)
        return testee
    }

}
