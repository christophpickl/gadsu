package non_test._main_.view

import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MultiProperties
import at.cpickl.gadsu.view.components.MyListCellRenderer
import non_test.Framed
import javax.swing.JComponent
import javax.swing.JLabel


fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize { context ->
        MultiProperties<String>(
                initialData = 1.rangeTo(10).map { it.toString() },
                bus = context.bus,
                editorCellRenderer = object : MyListCellRenderer<String>() {
                    override fun newCell(value: String) = StringCell(value)
                },
                viewNameId = "x",
                createRenderText = { it },
                noteEnabled = false
        ).toComponent()
    }
}

private class StringCell(value: String) : DefaultCellView<String>(value) {
    private val label = JLabel(value)
    override val applicableForegrounds: Array<JComponent> get() = arrayOf(label)
    init {
        add(label)
    }

}
