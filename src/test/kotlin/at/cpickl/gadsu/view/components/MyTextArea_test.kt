package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.ui.RichTextAreaAsserter
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.RichFormat.Bold
import at.cpickl.gadsu.view.components.RichFormat.Italic
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.uispec4j.Window
import org.uispec4j.interception.MainClassAdapter
import java.awt.BorderLayout
import java.util.LinkedList
import javax.swing.JPanel

@Test class RichTextAreaTest {

    fun readComplexEnrichedText() {
        val testee = RichTextArea("viewName")
        val text = "${Bold.wrap("one")} two t${Italic.wrap("hre")}e ${Bold.wrap("four")}"
        testee.readEnrichedText(text)
        MatcherAssert.assertThat(testee.toEnrichedText(), Matchers.equalTo(text))
    }

}

@Test(groups = arrayOf("uiTest"))
class RichTextAreaUiTest : SimpleUiTest() {

    companion object {
        private val VIEWNAME = "testRichTextAreaViewName"
        private val ANY_FORMAT = Bold
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

    @DataProvider
    fun provideFormats() = RichFormat.values().map { arrayOf(it) }.toTypedArray()

    @Test(dataProvider = "provideFormats")
    fun formatWordWithinBoldAndThenWholeTextBold(format: RichFormat) {
        textAsserter.enterText("one two three")
        textAsserter.assertEnrichedTextEquals("one two three")
        textAsserter.select("one ".length, "two".length)
        textAsserter.hitShortcut(format)
        textAsserter.assertEnrichedTextEquals("one ${format.wrap("two")} three")

        textAsserter.selectAll()
        textAsserter.hitShortcut(format)
        textAsserter.assertEnrichedTextEquals(format.wrap("one two three"))
    }

    fun testSetEnrichedText() {
        val enrichedText = "${Bold.wrap("one")} mid1 ${Bold.wrap("two")} mid2 ${Bold.wrap("three")}"
        textArea.readEnrichedText(enrichedText)

        textAsserter.assertEnrichedTextEquals(enrichedText)
        textAsserter.assertPlainTextEquals("one mid1 two mid2 three")
    }

    fun testSetEnrichedTextShouldMergeTwoAdjacent() {
        textArea.readEnrichedText("ab")
        textAsserter.select(0, 1).hitShortcut(Bold)

        textAsserter.assertEnrichedTextEquals("${Bold.wrap("a")}b")

        textAsserter.select(1, 1).hitShortcut(Bold)
        textAsserter.assertEnrichedTextEquals(Bold.wrap("ab"))
    }

    fun twoDifferentProperIndexCalculation() {
        textAsserter.enterText("one two three four")
        textAsserter.select(0, 3).hitShortcut(Bold)
        textAsserter.select(14, 4).hitShortcut(Bold)
        textAsserter.select(9, 3).hitShortcut(Italic)

        textAsserter.assertEnrichedTextEquals(Bold.wrap("one") + " two t" + Italic.wrap("hre") + "e " + Bold.wrap("four"))
    }

    fun selectAllAndHighlightShouldDispatchShortcutEvent() {
        val events = LinkedList<ShortcutEvent>()
        textArea.registerListener(object : ShortcutListener {
            override fun onShortcut(event: ShortcutEvent) {
                events.add(event)
            }
        })

        textAsserter.enterText("123")
        textAsserter.selectAll()
        textAsserter.hitShortcut(ANY_FORMAT)

        MatcherAssert.assertThat(events, Matchers.hasSize(1))
        MatcherAssert.assertThat(events[0], Matchers.equalTo(ShortcutEvent(Bold, "123")))
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

        textAsserter.select(0, 1).hitShortcut(ANY_FORMAT.shortcutKey)

        MatcherAssert.assertThat(context.isModified(), Matchers.equalTo(true))
        textAsserter.assertEnrichedTextEquals(ANY_FORMAT.wrap("a") + "bcd")
    }

    private fun testee(): RichTextArea {
        container.removeAll()
        val testee = RichTextArea(viewName = VIEWNAME)
        container.add(testee, BorderLayout.CENTER)
        return testee
    }


    private class ModificationTestContext(val client: Client) : ModificationAware {

        val modificationChecker = ModificationChecker(this)
        val fields = Fields<Client>(modificationChecker)
        val inpNote = fields.newTextArea("Notiz", { it.note }, VIEWNAME)

        override fun isModified() = inpNote.isModified(client)

    }

}

private fun RichFormat.wrap(innerHtml: String) = tag1 + innerHtml + tag2
