package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.ui.RichTextAreaAsserter
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.logic.addChangeListener
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import java.util.LinkedList
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

@Test class RichTextAreaTest {

    fun readComplexEnrichedText() {
        val testee = RichTextArea("viewName")
        testee.readEnrichedText("<hl>one</hl> two t<i>hre</i>e <hl>four</hl>")
    }

}

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
        val enrichedText = "<hl>one</hl> mid1 <hl>two</hl> mid2 <hl>three</hl>"
        textArea.readEnrichedText(enrichedText)

        textAsserter.assertEnrichedTextEquals(enrichedText)
        textAsserter.assertPlainTextEquals("one mid1 two mid2 three")
    }

    fun testSetEnrichedTextShouldMergeTwoAdjacent() {
        textArea.readEnrichedText("ab")
        textAsserter.select(0, 1).hitShortcut('b')

        textAsserter.assertEnrichedTextEquals("<hl>a</hl>b")

        textAsserter.select(1, 1).hitShortcut('b')
        textAsserter.assertEnrichedTextEquals("<hl>ab</hl>")
    }

    fun testOnChange() {
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

    fun twoDifferentProperIndexCalculation() {
        textAsserter.enterText("one two three four")
        textAsserter.select(0, 3).hitShortcut('b')
        textAsserter.select(14, 4).hitShortcut('b')
        textAsserter.select(9, 3).hitShortcut('i')

        textAsserter.assertEnrichedTextEquals("<hl>one</hl> two t<i>hre</i>e <hl>four</hl>")
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



    fun modificationCheck() {
        val originalNote = "abcd"
        val context = ModificationTestContext(Client.unsavedValidInstance().copy(note = originalNote))
        textAsserter = RichTextAreaAsserter(context.inpNote, this, window!!, VIEWNAME)

        container.removeAll()
        container.add(context.inpNote, BorderLayout.CENTER)


        context.inpNote.readEnrichedText(originalNote)
        MatcherAssert.assertThat(context.isModified(), Matchers.equalTo(false))
        textAsserter.assertEnrichedTextEquals(originalNote)

        textAsserter.select(0, 1).hitShortcut('b')

        MatcherAssert.assertThat(context.isModified(), Matchers.equalTo(true))
        textAsserter.assertEnrichedTextEquals("<hl>a</hl>bcd")
    }

    private class ModificationTestContext(val client: Client) : ModificationAware {

        val modificationChecker = ModificationChecker(this)
        val fields = Fields<Client>(modificationChecker)
        val inpNote = fields.newTextArea("Notiz", { it.note }, VIEWNAME)

        override fun isModified() = inpNote.isModified(client)

    }
}
