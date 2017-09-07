package at.cpickl.gadsu.view.components

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.Colors
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.annotations.VisibleForTesting
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.ScrollPaneConstants
import javax.swing.event.ListSelectionListener

interface EditorRendererSwitchable {
    fun changeToEditor()
    fun changeToRenderer()
}

class MultiProperties<T : Comparable<T>>(
        initialData: List<T>,
        bus: EventBus,
        editorCellRenderer: MyListCellRenderer<T>,
        viewNameId: String,
        private val createRenderText: (List<T>) -> List<String>,
        noteEnabled: Boolean
) :
        EditorRendererSwitchable {

    companion object {
        @VisibleForTesting fun buildRenderText(values: List<String>, note: String): String {
            return values.map { "* " + it }
                    .joinToString("\n") + (
                    if (note.isEmpty()) "" else
                        (if (values.isNotEmpty()) "\n\n" else "") + "[NOTIZ]\n" + note
                    )
        }
    }

    private val containerPanel = JPanel(BorderLayout())
    private val rendererView = MultiPropertiesRenderer({ changeToEditor() }, viewNameId)
    private val editorView = MultiPropertiesEditor<T>(initialData, editorCellRenderer, bus, { changeToRenderer() }, viewNameId, noteEnabled)

    init {
        changeContainerContent(rendererView)
    }

    val selectedValues: List<T> get() = editorView.list.selectedValuesList
    val enteredNote: String get() = editorView.note.text

    fun updateValue(newSelectedValues: List<T>, newNote: String) {
        updateRendererText(newSelectedValues, newNote)
        editorView.updateValue(newSelectedValues, newNote)
    }

    fun toComponent() = containerPanel

    fun enableFor(modifications: ModificationChecker) {
        modifications.enableChangeListener(editorView.list)
        modifications.enableChangeListener(editorView.note)
    }

    override fun changeToRenderer() {
        updateRendererText(selectedValues, enteredNote)
        changeContainerContent(rendererView)
    }

    override fun changeToEditor() {
        changeContainerContent(editorView)
    }

    private fun updateRendererText(newSelectedValues: List<T>, newNote: String) {
        rendererView.updateValue(buildRenderText(createRenderText(newSelectedValues), newNote))
    }

    private fun changeContainerContent(content: ContainerContent) {
        containerPanel.removeAll()
        containerPanel.add(content.toComponent(), BorderLayout.CENTER)
        containerPanel.revalidate()
        containerPanel.repaint()
    }

    fun addListSelectionListener(listener: ListSelectionListener) {
        editorView.list.addListSelectionListener(listener)
    }

}

class MultiPropExpander(private val switchables: List<EditorRendererSwitchable>) : JPanel() {
    constructor(vararg switchablesArray: EditorRendererSwitchable) : this(switchablesArray.toList())

    companion object {
        private val log = LOG(javaClass)
    }

    init {
        log.trace("new MultiPropExpander with switchables: {}", switchables)

        transparent()
        add(JButton("+").apply {
            addActionListener { expandAll() }
        })
        add(JButton("-").apply {
            addActionListener { collapseAll() }
        })
    }

    fun expandAll() {
        switchables.forEach(EditorRendererSwitchable::changeToEditor)
    }

    fun collapseAll() {
        switchables.forEach(EditorRendererSwitchable::changeToRenderer)
    }


}

private interface ContainerContent {
    fun toComponent(): JComponent
}

private class MultiPropertiesRenderer(
        onEditClicked: () -> Unit,
        viewNameId: String
) : ContainerContent {
    private val panel = GridPanel()
    private val text = MyTextArea(ViewNames.Components.MultiProperties.RenderText(viewNameId), maxChars = null).apply {
        isEditable = false
        background = Colors.LIGHT_GRAY
    }

    init {
        with(panel) {
            c.weightx = 1.0
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(text.scrolled(vPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED))
            text.addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    if (e.button == 1) {
                        onEditClicked()
                    }
                }
            })
        }
    }

    fun updateValue(newText: String) {
        text.text = newText
    }

    override fun toComponent() = panel
}


private class MultiPropertiesEditor<T : Comparable<T>>(
        initialData: List<T>,
        myCellRenderer: MyListCellRenderer<T>,
        bus: EventBus,
        onDoneClicked: () -> Unit,
        viewNameId: String,
        noteEnabled: Boolean
) : ContainerContent {

    val list: MyList<T>
    private val panel: JPanel
    val note = MyTextArea(ViewNames.Components.MultiProperties.InputNote(viewNameId), visibleRows = 3)

    init {
        val model = MyListModel<T>()
        model.resetData(initialData)
        list = MyList(ViewNames.Components.MultiProperties.InputList(viewNameId), model, bus, myCellRenderer)
        list.enableToggleSelectionMode()
        list.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        list.visibleRowCount = 1

        panel = GridPanel().apply {
            c.weightx = 1.0
            c.weighty = 1.0
            c.fill = GridBagConstraints.BOTH
            add(list.scrolled(hPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER))

            if (noteEnabled) {
                c.gridy++
                c.weighty = 0.0
                c.fill = GridBagConstraints.HORIZONTAL
                add(note.scrolled())
            }

            c.gridy++
            c.weightx = 1.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(JButton("Fertig").apply {
                name = ViewNames.Components.MultiProperties.ButtonDone(viewNameId)
                addActionListener { onDoneClicked() }
            })
        }
    }

    fun updateValue(newValues: List<T>, newNote: String) {
        list.clearSelection()
        list.addSelectedValues(newValues)
        note.text = newNote
    }

    override fun toComponent() = panel

}
