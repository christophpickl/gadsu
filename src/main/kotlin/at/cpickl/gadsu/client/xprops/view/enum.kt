package at.cpickl.gadsu.client.xprops.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.ElField
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.ScrollPaneConstants

interface ElFieldForProps<in V> : ElField<V> {
    fun enableFor(modifications: ModificationChecker)
}

class CPropEnumView(
        override val icon: ImageIcon?,
        private val xprop: XPropEnum,
        bus: EventBus
) : CPropView, ElFieldForProps<Client> {

    override val formLabel = xprop.label
    override val fillType = GridBagFill.Both

    private val containerPanel = JPanel(BorderLayout())
    private val rendererView = CPropEnumRendererView(xprop, { changeToEditor() })
    private val editorView = CPropEnumEditorView(xprop, bus, { changeToRenderer() })

    init {
        changeContainerContent(rendererView)
    }

    private fun changeToRenderer() {
        rendererView.updateValue(CProps(mapOf(xprop to toCProp())))
        changeContainerContent(rendererView)
    }
    private fun changeToEditor() {
        changeContainerContent(editorView)
    }

    private fun changeContainerContent(content: ContainerContent) {
        containerPanel.removeAll()
        containerPanel.add(content.toComponent(), BorderLayout.CENTER)
        containerPanel.revalidate()
        containerPanel.repaint()
    }

    override fun updateValue(value: Client) {
        rendererView.updateValue(value.cprops)
        editorView.updateValue(value.cprops)
    }

    override fun toCProp() = CPropEnum(xprop, editorView.list.selectedValuesList)

    override fun toComponent() = containerPanel

    override fun isModified(value: Client): Boolean {
        val selected = editorView.list.selectedValuesList
        val cprop = value.cprops.findOrNull(xprop) ?: return selected.isNotEmpty()

        if (selected.isEmpty() && cprop.isClientValueEmpty) {
            return false
        }

        val enumProp = cprop as CPropEnum
        return !enumProp.clientValue.containsAll(selected) ||
                !selected.containsAll(enumProp.clientValue)
    }

    override fun enableFor(modifications: ModificationChecker) {
        modifications.enableChangeListener(editorView.list)
    }
}


private interface ContainerContent {
    fun updateValue(cprops: CProps)
    fun toComponent(): JComponent
}

private class CPropEnumRendererView(
        private val xprop: XPropEnum,
        onEditClicked: () -> Unit
) : ContainerContent {
    private val panel = JPanel(BorderLayout())
    private val text = MyTextArea("", maxChars = null).apply {
        isEditable = false
        background = Colors.LIGHT_GRAY
    }

    init {
        panel.add(text.scrolled(vPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER)
        panel.add(JButton("Bearbeiten").apply {
            addActionListener{ onEditClicked() }
        }, BorderLayout.SOUTH)
    }

    override fun updateValue(cprops: CProps) {
        val cprop = cprops.findOrNull(xprop)
        if (cprop == null) {
            text.text = ""
            return
        }
        text.text = cprop.clientValue.map { "* " + it.label }.joinToString("\n")
        // FIXME #71 add xprop specific note
    }

    override fun toComponent() = panel
}


private class CPropEnumEditorView(
        private val xprop: XPropEnum,
        bus: EventBus,
        onDoneClicked: () -> Unit
) : ContainerContent {

    val list: MyList<XPropEnumOpt>
    private val panel: JPanel
    private val specificNote = MyTextArea("TcmNote.${xprop.key}", visibleRows = 3)

    init {
        val model = MyListModel<XPropEnumOpt>()
        model.resetData(xprop.options)
        list = MyList("CPropEnumView.XPropEnum.${xprop.key}", model, bus, object : MyListCellRenderer<XPropEnumOpt>(shouldHoverChangeSelectedBg = true) {
            override fun newCell(value: XPropEnumOpt) = XPropEnumCell(value)
        })
        list.enableToggleSelectionMode()
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 1

        panel = GridPanel().apply {
            c.weightx = 1.0
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(list.scrolled(hPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))

            c.gridy++
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(specificNote.scrolled())

            c.gridy++
            add(JButton("Fertig").apply {
                addActionListener{ onDoneClicked() }
            })
        }
    }

    override fun updateValue(cprops: CProps) {
        list.clearSelection()
        val cprop = cprops.findOrNull(xprop) ?: return
        list.addSelectedValues((cprop as CPropEnum).clientValue)
        // FIXME #71 text for specificNote = client.cpropsNote[xprop]
    }

    override fun toComponent() = panel

}

class XPropEnumCell(val xprop: XPropEnumOpt) : DefaultCellView<XPropEnumOpt>(xprop) {

    private val txtLabel = JLabel(xprop.label)
    override val applicableForegrounds: Array<JComponent> = arrayOf(txtLabel)

    init {
        c.anchor = GridBagConstraints.WEST
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtLabel)
    }

}
