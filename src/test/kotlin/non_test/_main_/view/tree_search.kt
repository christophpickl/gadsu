package non_test._main_.view

import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.tree.LiveSearchField
import at.cpickl.gadsu.view.tree.MyTree2
import at.cpickl.gadsu.view.tree.toTreeModel
import non_test.Framed
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.JScrollPane


fun main(args: Array<String>) {
    val searchField = LiveSearchField()

    Framed.showWithContextDefaultSize {
        JPanel().apply {
            layout = BorderLayout()
            add(searchField.asComponent(), BorderLayout.NORTH)
            add(JScrollPane(MyTree2(listOf(XProps.ChiStatus, XProps.BodyConception, XProps.Hungry).toTreeModel())))

        }
    }
}
