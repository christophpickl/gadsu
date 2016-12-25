package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnum
import at.cpickl.gadsu.client.xprops.model.XPropEnumOpt
import at.cpickl.gadsu.client.xprops.view.formatData
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.ViewNames
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
        bus: EventBus,
        editorCellRenderer: MyListCellRenderer<XPropEnumOpt>,
        viewNameId: String
) {

    private val containerPanel = JPanel(BorderLayout())
    private val rendererView = MultiPropertiesRenderer({ changeToEditor() }, viewNameId)
    private val editorView = MultiPropertiesEditor(xprop, editorCellRenderer, bus, { changeToRenderer() }, viewNameId)

    init {
        changeContainerContent(rendererView)
    }

    val selectedValues: List<XPropEnumOpt> get() = editorView.list.selectedValuesList
    val enteredNote: String get() = editorView.note.text

    fun updateValue(value: Client) {
        val cprop = value.cprops.findOrNull(xprop)

        rendererView.updateValue(cprop?.formatData() ?: "")
        editorView.updateValue(cprop?.clientValue ?: emptyList(), cprop?.note ?: "")
    }

    fun toComponent() = containerPanel

    fun enableFor(modifications: ModificationChecker) {
        modifications.enableChangeListener(editorView.list)
        modifications.enableChangeListener(editorView.note)
    }

    private fun changeToRenderer() {
        rendererView.updateValue(CPropEnum(xprop, selectedValues, enteredNote).formatData())
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
    fun toComponent(): JComponent
}

private class MultiPropertiesRenderer(
        onEditClicked: () -> Unit,
        viewNameId: String
) : ContainerContent {
    private val panel = JPanel(BorderLayout())
    private val text = MyTextArea(ViewNames.Components.MultiProperties.RenderText(viewNameId), maxChars = null).apply {
        isEditable = false
        background = Colors.LIGHT_GRAY
    }

    init {
        panel.add(text.scrolled(vPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER)
        panel.add(JButton("Bearbeiten").apply {
            name = ViewNames.Components.MultiProperties.ButtonEdit(viewNameId)
            addActionListener { onEditClicked() }
        }, BorderLayout.SOUTH)
    }

    fun updateValue(newText: String) {
        text.text = newText
    }

    override fun toComponent() = panel
}


private class MultiPropertiesEditor(
        xprop: XPropEnum,
        myCellRenderer: MyListCellRenderer<XPropEnumOpt>,
        bus: EventBus,
        onDoneClicked: () -> Unit,
        viewNameId: String
) : ContainerContent {

    val list: MyList<XPropEnumOpt>
    private val panel: JPanel
    val note = MyTextArea(ViewNames.Components.MultiProperties.InputNote(viewNameId), visibleRows = 3)

    init {
        val model = MyListModel<XPropEnumOpt>()
        model.resetData(xprop.options)
        list = MyList(ViewNames.Components.MultiProperties.InputList(viewNameId), model, bus, myCellRenderer)
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
                name = ViewNames.Components.MultiProperties.ButtonDone(viewNameId)
                addActionListener { onDoneClicked() }
            })
        }
    }

    fun updateValue(newValues: List<XPropEnumOpt>, newNote: String) {
        list.clearSelection()
        list.addSelectedValues(newValues)
        note.text = newNote
    }

    override fun toComponent() = panel

}
