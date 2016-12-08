package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.skip
import at.cpickl.gadsu.testinfra.ui.RichTextAreaAsserter
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.view.logic.addChangeListener
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import java.util.LinkedList
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

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
        skip("a")
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
        skip("a")
        textArea.readEnrichedText("<hl>one</hl> one-B <hl>two</hl> three <hl>four</hl>")

        textAsserter.assertPlainTextEquals("one one-B two three four")
        textAsserter.assertEnrichedTextEquals("<hl>one</hl> one-B <hl>two</hl> three <hl>four</hl>")
    }

    fun testOnChange() {
        skip("f")
        textAsserter.enterText("123")
        textAsserter.select(1, 1)
        val events = LinkedList<DocumentEvent>()
        textArea.addChangeListener {
            events.add(it)
            println(it)
            // maybe we allow a change event
        }
        textAsserter.hitShortcut('i')

//        MatcherAssert.assertThat(events, empty())
    }

    fun testOnChange2() {
        textAsserter.enterText("123")
        textAsserter.selectAll()
        val events = LinkedList<DocumentEvent>()
        textArea.addChangeListener {
            events.add(it)
            println(it)
            // maybe we allow a change event
        }
        textAsserter.hitShortcut('i')

        println("enriched: [${textArea.toEnrichedText()}]")
//        MatcherAssert.assertThat(events, empty())
    }

    private fun testee(): RichTextArea {
        container.removeAll()
        val testee = RichTextArea(viewName = VIEWNAME)
        container.add(testee, BorderLayout.CENTER)
        return testee
    }

}
