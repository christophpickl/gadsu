package at.cpickl.gadsu.view

import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.transparent
import java.awt.Component
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane

interface KTab {

    val tabTitle: String
    val scrolled: Boolean

    fun asComponent(): JComponent

}

fun JTabbedPane.addKTabs(tabs: List<KTab>) {
    var i: Int = 0
    tabs.forEach { tab ->
        val tabContent: Component = if (tab.scrolled) JScrollPane(tab.asComponent()).transparent() else tab.asComponent()
        addTab("<html><body><table width='100'><span style='align:center'>${tab.tabTitle}</span></table></body></html>", tabContent)
        setTabComponentAt(i++, JLabel(tab.tabTitle, JLabel.CENTER).enforceWidth(100))
//        val tabContent: Component = JScrollPane(tab.asComponent().opaque()).transparent()
//        addTab("<html><body><table width='100'><span style='align:center'>${tab.tabTitle}</span></table></body></html>", tabContent)
//        setTabComponentAt(i++, JLabel(tab.tabTitle, JLabel.CENTER).enforceWidth(100))
    }
}
