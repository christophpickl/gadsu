package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.LiveSearchField
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.ExpandCollapseListener
import at.cpickl.gadsu.view.components.ExpandCollapsePanel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.fillAll
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.tree.TreeSearcher
import at.cpickl.gadsu.view.tree.buildTree
import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel


class ClientTabTcm2(
        initialClient: Client
) : DefaultClientTab(
        tabTitle = Labels.Tabs.ClientTcm,
        type = ClientTabType.TCM,
        scrolled = false
) {

    private val container = JPanel().apply {
        layout = BorderLayout()
    }

    private val xpropEnums = listOf(
            listOf(XProps.Hungry, XProps.BodyConception),
            listOf(XProps.ChiStatus, XProps.Digestion, XProps.Temperature),
            listOf(XProps.Impression, XProps.Liquid, XProps.Menstruation, XProps.Sleep)
    )

    private val viewPanel = TcmViewPanel(xpropEnums)
    private val editPanel = TcmEditPanel(xpropEnums)

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
        // TODO implement me
        return false
    }

    override fun updateFields(client: Client) {
        // TODO implement me
    }

    private fun changeContentTo(panel: JPanel) {
        container.removeAll()
        container.add(panel, BorderLayout.CENTER)
        container.revalidate()
        container.repaint()
    }

}

private class TcmViewPanel(xpropEnums: List<List<XPropEnum>>) : GridPanel() {

    // TODO buttons same width
    val btnStartEdit = JButton("Bearbeiten")

    init {
        add(btnStartEdit)

        c.gridy++
        c.fillAll()
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            xpropEnums.forEach {
                add(JLabel(it[0].label))
                // go through all, and check if client has selected => render label (with header); and note
            }
        })
    }

    fun initClient(client: Client) {
        println("initClient(client=$client)")
    }

}

typealias XPropEnums = List<List<XPropEnum>>

private class TcmEditPanel(xPropEnums: XPropEnums) : GridPanel(), ExpandCollapseListener {

    private val log = LOG {}

    private val searchField = LiveSearchField(ViewNames.Client.InputTcmSearchField)
    val btnFinishEdit = JButton("Fertig")
    private val trees = xPropEnums.map { buildTree(it) }

    init {
        TreeSearcher(searchField, trees) // will work async

        debugColor = Color.GREEN
        // TODO tree.initSelected

        c.weightx = 0.0
        add(btnFinishEdit)
        c.gridx++
        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        add(searchField.asComponent())
        c.gridx++
        c.weightx = 0.0
        add(ExpandCollapsePanel(this))

        c.gridx = 0
        c.gridy++
        c.gridwidth = 3
        c.fillAll()
        add(GridPanel().apply {
            c.anchor = GridBagConstraints.NORTH
            c.weightx = 0.3
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            trees.forEach { tree ->
                add(tree.scrolled())
                c.gridx++
            }
        })
    }

    override fun onExpand() {
        log.trace { "onExpand()" }
        trees.forEach { tree ->
            tree.expandAll()
        }
    }

    override fun onCollapse() {
        log.trace { "onCollapse()" }
        trees.forEach { tree ->
            tree.collapseAll()
        }
    }

    fun initClient(client: Client) {
        log.trace { "initClient(client)" }
        trees.forEach { tree ->
            tree.initSelected(client.cprops.map { it.clientValue }.flatten().toSet())
        }
    }
}
