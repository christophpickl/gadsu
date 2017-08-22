package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.tcm.patho.SyndromeGuesser
import at.cpickl.gadsu.tcm.patho.SyndromeReport
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import java.awt.GridBagConstraints

// for syndrome guesser:
// - big HTML view rendering the content
// - button in the bottom corner to recalc result

// future:
// - analyze treatment data
// - maybe some stats (not really assisting, but general info)

object RecalculateAssistentEvent : UserEvent()


class ClientTabAssist @Inject constructor(
        bus: EventBus
) : DefaultClientTab(
        tabTitle = Labels.Tabs.ClientAssist,
        type = ClientTabType.ASSIST
//        scrolled = false
) {

    private val textOutput = HtmlEditorPane()

    init {
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(textOutput.scrolled(), c)

        c.gridy++
        c.anchor = GridBagConstraints.EAST
        c.fill = GridBagConstraints.NONE
        c.weightx = 0.0
        c.weighty = 0.0

        add(EventButton("Neu berechnen", ViewNames.Assistent.ButtonRecalculate, { RecalculateAssistentEvent }, bus), c)
    }

    fun updateReport(client: Client, report: SyndromeReport) {
        textOutput.text = """
            |<h1>Assistenz Bericht</h1>
            |<h2>Klienten Symptome</h2>
            |<p>${client.cprops.map { it.clientValue.map { it.label }.joinToString() }.joinToString()}</p>
            |
            |<h2>Vermutete Disharmoniemuster:</h2>
            |${report.asHtml}
            """.trimMargin()

    }

    override fun isModified(client: Client) = false
    override fun updateFields(client: Client) {}
}


@Logged
open class AssistentController @Inject constructor(
        private val currentClient: CurrentClient,
        private val view: ClientTabAssist

) {
    private val guesser = SyndromeGuesser()

    @Subscribe open fun onRecalculateAssistentEvent(event: RecalculateAssistentEvent) {
        val report = guesser.detect(currentClient.data)
        view.updateReport(currentClient.data, report)
    }
}
