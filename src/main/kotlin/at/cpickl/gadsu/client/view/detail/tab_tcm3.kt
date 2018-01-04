package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.LiveSearchField
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.fillAll
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.weightxy
import at.cpickl.gadsu.view.swing.withFont
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class ClientTabTcm3(
        initialClient: Client,
        bus: EventBus
) : DefaultClientTab(
        tabTitle = Labels.Tabs.ClientTcm,
        type = ClientTabType.TCM,
        scrolled = false
) {
    private val container = JPanel().apply {
        layout = BorderLayout()
    }

    private val viewPanel = TcmViewPanel3()
    private val editPanel = TcmEditPanel3(listOf(XProps.ChiStatus.options, XProps.Hungry.options), bus)

    init {
        container.debugColor = Color.RED
        c.fillAll()
        add(container)

        viewPanel.initClient(initialClient)
        editPanel.initClient(initialClient)

        changeContentTo(editPanel)
        editPanel.btnFinishEdit.addActionListener {
            changeContentTo(viewPanel)
        }
        viewPanel.btnStartEdit.addActionListener {
            changeContentTo(editPanel)
        }
    }

    override fun isModified(client: Client): Boolean {
        return false
    }

    override fun updateFields(client: Client) {

    }

    private fun changeContentTo(panel: JPanel) {
        container.removeAll()
        container.add(panel, BorderLayout.CENTER)
        container.revalidate()
        container.repaint()
    }
}


private class TcmViewPanel3() : GridPanel() {

    private val log = LOG {}

    val btnStartEdit = JButton("Bearbeiten")

    init {
        add(btnStartEdit)
    }

    fun initClient(client: Client) {
        log.trace { "initClient(client)" }
        // only render values from client cprops
    }

}


private class XPropCell(foo: XPropEnumOpt) : DefaultCellView<XPropEnumOpt>(foo) {
    private val nameLbl = JLabel(value.label).withFont(Font.BOLD, 13)
    override val applicableForegrounds: Array<JComponent> = arrayOf(nameLbl)

    init {
        c.weightxy(1.0)
        c.fill = GridBagConstraints.BOTH
        add(nameLbl)
    }
}

private typealias XPropsGroup = List<List<XPropEnumOpt>>

private class TcmEditPanel3(private val xpropGroups: XPropsGroup, bus: EventBus) : GridPanel() {

    private val log = LOG {}

    private val searchField = LiveSearchField(ViewNames.Client.InputTcmSearchField)
    val btnFinishEdit = JButton("Fertig")
    private val tcmProps = xpropGroups.map {
        MyList(
                viewName = "a",
                myModel = MyListModel(it),
                bus = bus,
                myCellRenderer = object : MyListCellRenderer<XPropEnumOpt>() {
                    override fun newCell(value: XPropEnumOpt) = XPropCell(value)
                }
        )
    }

    fun initClient(client: Client) {
        log.trace { "initClient(client)" }
    }

    private fun clearSearch() {
        log.trace { "clearSearch()" }
//        tcmProps.forEach { it.resetData() }
    }

    private fun doSearch(terms: List<String>) {
        log.trace { "doSearch(terms=$terms)" }
    }

    init {
        searchField.addListener { searchText ->
            val search = searchText.trim()
            if (search.isEmpty()) {
                clearSearch()
            } else {
                doSearch(search.split(" "))
            }
        }

        c.weightx = 1.0
        c.weighty = 0.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(searchField.asComponent())

        c.gridx++
        c.weightx = 0.0
        c.fill = GridBagConstraints.NONE
        add(btnFinishEdit)

        c.gridx = 0
        c.gridy++
        c.gridwidth = 2
        c.weightxy(1.0)
        c.fill = GridBagConstraints.BOTH
        c.fillAll()
        add(GridPanel().apply {
            c.anchor = GridBagConstraints.NORTH
            c.weightx = 1.0
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            tcmProps.forEach { list ->
                add(list.scrolled())
                c.gridx++
            }
        })
    }
}
