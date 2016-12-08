package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.ui.RichTextAreaAsserter
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import javax.swing.JPanel

@Test(groups = arrayOf("uiTest"))
class RichTextAreaUiTest : SimpleUiTest() {

    companion object {
        private val VIEWNAME = "testRichTextAreaViewName"
    }

    private lateinit var textArea: RichTextArea
    private lateinit var textAsserter: RichTextAreaAsserter
    private val container = JPanel(BorderLayout())

    override fun newMainClassAdapter(): MainClassAdapter {
        TestViewStarter.componentToShow = container
        return MainClassAdapter(TestViewStarter::class.java)
    }

    override fun postInit(window: Window) {
        // no op
    }

    @BeforeMethod
    fun initTest() {
        textArea = testee()
        textAsserter = RichTextAreaAsserter(textArea, this, window!!, VIEWNAME)
    }

    fun testGetEnrichedText() {
        textAsserter.enterText("one two three")
        textAsserter.assertEnrichedTextEquals("one two three")
        textAsserter.select("one ".length, "two".length)
        textAsserter.hitShortcut('b')
        textAsserter.assertEnrichedTextEquals("one <hl>two</hl> three")

        textAsserter.selectAll()
        textAsserter.hitShortcut('b')
        textAsserter.assertEnrichedTextEquals("<hl>one two three</hl>")
    }

    fun testSetEnrichedText() {
        textArea.readEnrichedText("<hl>one</hl> one-B <hl>two</hl> three <hl>four</hl>")

        textAsserter.assertPlainTextEquals("one one-B two three four")
        textAsserter.assertEnrichedTextEquals("<hl>one</hl> one-B <hl>two</hl> three <hl>four</hl>")
    }

    private fun testee(): RichTextArea {
        container.removeAll()
        val testee = RichTextArea(viewName = VIEWNAME)
        container.add(testee, BorderLayout.CENTER)
        return testee
    }

}
