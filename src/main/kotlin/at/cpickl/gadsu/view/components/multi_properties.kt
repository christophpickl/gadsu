package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.client.xprops.view.formatData
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.ScrollPaneConstants

class MultiProperties(
        private val xprop: XPropEnum,
        private val valueReaderFunction: () -> CPropEnum,
        bus: EventBus,
        editorCellRenderer: MyListCellRenderer<XPropEnumOpt>
) {

    private val containerPanel = JPanel(BorderLayout())
    private val rendererView = MultiPropertiesRenderer(xprop, { changeToEditor() })
    private val editorView = MultiPropertiesEditor(xprop, editorCellRenderer, bus, { changeToRenderer() })

    init {
        changeContainerContent(rendererView)
    }

    val selectedValues: List<XPropEnumOpt> get() = editorView.list.selectedValuesList
    val note: String get() = editorView.note.text

    fun updateValue(value: Client) {
        rendererView.updateValue(value.cprops)
        editorView.updateValue(value.cprops)
    }

    fun toComponent() = containerPanel

    fun enableFor(modifications: ModificationChecker) {
        modifications.enableChangeListener(editorView.list)
        modifications.enableChangeListener(editorView.note)
    }

    private fun changeToRenderer() {
        rendererView.updateValue(CProps(mapOf(xprop to valueReaderFunction())))
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

}

private interface ContainerContent {
    fun updateValue(cprops: CProps)
    fun toComponent(): JComponent
}

private class MultiPropertiesRenderer(
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
            addActionListener { onEditClicked() }
        }, BorderLayout.SOUTH)
    }

    override fun updateValue(cprops: CProps) {
        text.text = cprops.findOrNull(xprop).formatData()
    }

    override fun toComponent() = panel
}



private class MultiPropertiesEditor(
        private val xprop: XPropEnum,
        myCellRenderer: MyListCellRenderer<XPropEnumOpt>,
        bus: EventBus,
        onDoneClicked: () -> Unit
) : ContainerContent {

    val list: MyList<XPropEnumOpt>
    private val panel: JPanel
    val note = MyTextArea("TcmNote.${xprop.key}", visibleRows = 3)

    init {
        val model = MyListModel<XPropEnumOpt>()
        model.resetData(xprop.options)
        list = MyList("CPropEnumView.XPropEnum.${xprop.key}", model, bus, myCellRenderer)
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
            add(note.scrolled())

            c.gridy++
            add(JButton("Fertig").apply {
                addActionListener { onDoneClicked() }
            })
        }
    }

    override fun updateValue(cprops: CProps) {
        list.clearSelection()
        val cprop = cprops.findOrNull(xprop)
        if (cprop == null) {
            note.text = ""
            return
        }
        list.addSelectedValues((cprop as CPropEnum).clientValue)
        note.text = cprop.note
    }

    override fun toComponent() = panel

}
