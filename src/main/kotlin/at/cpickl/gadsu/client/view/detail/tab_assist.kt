package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.client.forClient
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.tcm.patho.SyndromeGuesser
import at.cpickl.gadsu.tcm.patho.SyndromeReport
import at.cpickl.gadsu.treatment.TreatmentService
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.EventButton
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.noBorder
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import java.awt.GridBagConstraints

// future:
// - analyze treatment data
// - maybe some stats (not really assisting, but general info)

object RecalculateAssistentEvent : UserEvent()


class ClientTabAssist @Inject constructor(
        bus: EventBus
) : DefaultClientTab(
        tabTitle = Labels.Tabs.ClientAssist,
        type = ClientTabType.ASSIST
) {

    private val textOutput = HtmlEditorPane()

    init {
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(textOutput.scrolled().transparent().noBorder().apply { viewport.transparent() }, c)

        c.gridy++
        c.anchor = GridBagConstraints.EAST
        c.fill = GridBagConstraints.NONE
        c.weightx = 0.0
        c.weighty = 0.0

        add(EventButton("Neu berechnen", ViewNames.Assistent.ButtonRecalculate, { RecalculateAssistentEvent }, bus), c)
    }

    fun updateReport(client: Client, report: SyndromeReport) {


        // TODO also include pulse/tongue results in this list
        textOutput.text = """
            |<h1>Assistenz-Bericht f&uuml;r ${client.preferredName}</h1>
            |<h2>Klienten Symptome</h2>
            |<p>${
        if (client.cprops.isEmpty()) "<i>Keine eingetragen.</i>"
        else client.cprops.map { it.clientValue.map { it.label }.sorted().joinToString() }.joinToString()
        }</p>
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
        private val view: ClientTabAssist,
        private val treatmentService: TreatmentService

) {
    private val guesser = SyndromeGuesser()
    private var recentReport: SyndromeReport? = null

    @Subscribe open fun onRecalculateAssistentEvent(event: RecalculateAssistentEvent) {
        recalculateAndUpdateView()
    }

    @Subscribe open fun onClientTabSelected(event: ClientTabSelected) {
        if (event.tab.type == ClientTabType.ASSIST && recentReport == null) {
            recalculateAndUpdateView()
        }
    }

    @Subscribe open fun onCurrentEvent(event: CurrentEvent) {
        event.forClient { recalculateAndUpdateView() }
    }

    private fun recalculateAndUpdateView() {
        val client = currentClient.data
        if (!client.yetPersisted) {
            view.updateReport(client, SyndromeReport.empty)
            recentReport = null
            return
        }
        val treatments = treatmentService.findAllFor(client)

        recentReport = guesser.guess(client, treatments)
        view.updateReport(client, recentReport!!)
    }
}
