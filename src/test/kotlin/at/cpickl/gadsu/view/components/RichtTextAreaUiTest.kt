package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.service.times
import at.cpickl.gadsu.testinfra.TestViewStarter
import at.cpickl.gadsu.testinfra.ui.RichTextAreaAsserter
import at.cpickl.gadsu.testinfra.ui.SimpleUiTest
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import com.google.common.eventbus.EventBus
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


@Test(groups = arrayOf("uiTest"))
class RichTextAreaUiTest : SimpleUiTest() {

    companion object {
        private val VIEWNAME = "testRichTextAreaViewName"
        private val ANY_FORMAT = RichFormat.Bold
        private val MAX_CHARS = 100
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
        val enrichedText = "${RichFormat.Bold.wrap("one")} mid1 ${RichFormat.Bold.wrap("two")} mid2 ${RichFormat.Bold.wrap("three")}"
        textArea.readEnrichedText(enrichedText)

        textAsserter.assertEnrichedTextEquals(enrichedText)
        textAsserter.assertPlainTextEquals("one mid1 two mid2 three")
    }

    fun testSetEnrichedTextShouldMergeTwoAdjacent() {
        textArea.readEnrichedText("ab")
        textAsserter.select(0, 1).hitShortcut(RichFormat.Bold)

        textAsserter.assertEnrichedTextEquals("${RichFormat.Bold.wrap("a")}b")

        textAsserter.select(1, 1).hitShortcut(RichFormat.Bold)
        textAsserter.assertEnrichedTextEquals(RichFormat.Bold.wrap("ab"))
    }

    fun twoDifferentProperIndexCalculation() {
        textAsserter.enterText("one two three four")
        textAsserter.select(0, 3).hitShortcut(RichFormat.Bold)
        textAsserter.select(14, 4).hitShortcut(RichFormat.Bold)
        textAsserter.select(9, 3).hitShortcut(RichFormat.Italic)

        textAsserter.assertEnrichedTextEquals(RichFormat.Bold.wrap("one") + " two t" + RichFormat.Italic.wrap("hre") + "e " + RichFormat.Bold.wrap("four"))
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
        MatcherAssert.assertThat(events[0], Matchers.equalTo(ShortcutEvent(RichFormat.Bold, "123")))
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

    fun `enforce length, given entered text of length MAX_CHARS, when enter yet another char, then it should beep`() {
        val atMaxText = "x".times(MAX_CHARS)
        textAsserter.appendAtEnd(atMaxText)
        textAsserter.assertPlainTextEquals(atMaxText)

        textAsserter.appendAtEnd("o")

        textAsserter.assertPlainTextEquals(atMaxText)
        textAsserter.assertHasBeeped()
    }

    fun `enforce length, restrict respecting format tags`() {
        val oTag = RichFormat.Bold.wrap("o")
        val xes = "x".times(MAX_CHARS - oTag.length)
        val atMaxTextWithTag = xes + oTag
        textArea.readEnrichedText(atMaxTextWithTag)

        textAsserter.appendAtEnd("o")

        textAsserter.assertPlainTextEquals(xes + "o")
        textAsserter.assertEnrichedTextEquals(atMaxTextWithTag)
        textAsserter.assertHasBeeped()
    }

    fun `emphasize acupuncture as single word`() {
        textArea.readEnrichedText("Lu1")
        assertAcupunctFormat(0, 2)
    }

    fun `emphasize acupuncture within`() {
        textArea.readEnrichedText("a Lu1 b")
        assertNotAcupunctFormat(0, 1)
        assertAcupunctFormat(2, 4)
        assertNotAcupunctFormat(5, 5)
    }

    fun `emphasize acupuncture many points`() {
        textArea.readEnrichedText("a Lu1 b Lu1 c Bl1")
        assertAcupunctFormat(2, 4)
        assertAcupunctFormat(8, 10)
        assertAcupunctFormat(14, 16)
    }

    private fun assertAcupunctFormat(from: Int, to: Int) {
        from.rangeTo(to).forEach {
            MatcherAssert.assertThat("Expected character at position $it which is '${textArea.text[it]}' to be formated as acupunct in text: [${textArea.text}]",
                    textArea.isAcupunctFormatAt(it), Matchers.equalTo(true))
        }
    }
    private fun assertNotAcupunctFormat(from: Int, to: Int) {
        from.rangeTo(to).forEach {
            MatcherAssert.assertThat(textArea.isAcupunctFormatAt(it), Matchers.equalTo(false))
        }
    }

    private fun testee(): RichTextArea {
        container.removeAll()
        val testee = RichTextArea(maxChars = MAX_CHARS, viewName = VIEWNAME, bus = EventBus())
        container.add(testee, BorderLayout.CENTER)
        return testee
    }


    private class ModificationTestContext(val client: Client) : ModificationAware {

        val modificationChecker = ModificationChecker(this)
        val fields = Fields<Client>(modificationChecker)
        val inpNote = fields.newTextArea("Notiz", { it.note }, VIEWNAME, EventBus())

        override fun isModified() = inpNote.isModified(client)

    }

}