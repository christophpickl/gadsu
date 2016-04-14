package at.cpickl.gadsu.client

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.SimpleListModel
import at.cpickl.gadsu.view.components.newEventButton
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.joda.time.DateTime
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListCellRenderer
import javax.swing.ListSelectionModel


object ClientViewNames {
    val List = "Client.List"
}
val ViewNames.Client: ClientViewNames
    get() = ClientViewNames


class ClientViewController {
    // what goes in here?!
}

class ClientView @Inject constructor(
        private val masterView: ClientMasterView,
        private val detailView: ClientDetailView
) : GridPanel() {
    init {
        if (Development.ENABLED) background = Color.YELLOW

        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0

        c.weightx = 0.3
        c.gridx = 0
        add(masterView)

        c.weightx = 0.7
        c.gridx++
        add(detailView)
    }
}


class ClientMasterView @Inject constructor(
        eventBus: EventBus,
        swing: SwingFactory
) : GridPanel() {
    init {
        if (Development.ENABLED) background = Color.RED

        val model = SimpleListModel(arrayListOf(Client("", "first", "last", DateTime.now())))
        val list = JList<Client>(model)
        list.name = ViewNames.Client.List

        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (list.selectedIndex == -1) {
                    // MINOR what to do if selection changes to none? what actually triggers this? deletion of client?!
                } else {
                    eventBus.post(ClientSelectedEvent(list.selectedValue))
                }
            }
        }

        val oldRenderer = list.cellRenderer
        list.cellRenderer = ListCellRenderer<Client> { list, client, index, isSelected, cellHasFocus ->
            val oldComponent = oldRenderer.getListCellRendererComponent(list, client, index, isSelected, cellHasFocus) as JLabel
            oldComponent.text = client.fullName
            oldComponent
        }
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.layoutOrientation = JList.VERTICAL
        list.visibleRowCount = -1

        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(JScrollPane(list))

        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        c.gridy++

        add(swing.newEventButton("Neuen Klienten anlegen", { CreateNewClientEvent() }))
    }
}


open class GridPanel : JPanel() {
    protected val c = GridBagConstraints()
    init {
        if (Development.ENABLED) background = Color.GREEN

        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        gridBagLayout.setConstraints(this, c)

        c.gridx = 0
        c.gridy = 0
    }

    override fun add(comp: Component): Component? {
        super.add(comp, c)
        return null
    }

}


class ClientDetailView : JPanel() {
    init {
        add(JLabel("Detail"))
    }
}
