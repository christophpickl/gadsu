package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.newEventButton
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListCellRenderer
import javax.swing.ListModel
import javax.swing.ListSelectionModel

@Suppress("UNUSED") val ClientViewNames.List: String get() = "Client.List"
@Suppress("UNUSED") val ClientViewNames.CreateButton: String get() = "Client.CreateButton"

interface ClientMasterView {
    val model: ListModel<Client>

    fun initClients(clients: List<Client>)
    fun insertClient(index: Int, client: Client)
    fun selectClient(client: Client)
    fun changeClient(client: Client)

    fun asComponent(): Component

}

class SwingClientMasterView @Inject constructor(
        eventBus: EventBus,
        swing: SwingFactory
) : GridPanel(), ClientMasterView {

    override val model = DefaultListModel<Client>()

    private val log = LoggerFactory.getLogger(javaClass)
    private val list = JList<Client>(model)

    init {
        if (Development.ENABLED) background = Color.RED

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

        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(JScrollPane(list))

        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        c.gridy++

        add(swing.newEventButton("Neuen Klienten anlegen", ViewNames.Client.CreateButton, { CreateNewClientEvent() }))
    }

    override fun asComponent() = this

    override fun initClients(clients: List<Client>) {
        log.trace("initClients(clients={})", clients)
        clients.forEach { model.addElement(it) }
    }

    override fun insertClient(index: Int, client: Client) {
        log.trace("insertClient(index={}, client={})", index, client)
        model.add(index, client)
    }

    override fun selectClient(client: Client) {
        log.trace("selectClient(client={})", client)
        list.setSelectedValue(client, true)
    }

    override fun changeClient(client: Client) {
        log.trace("changeClient(client={})", client)
        model.setElementAt(client, findModelIndex(client))
    }

    // MINOR change to extension function for model
    private fun findModelIndex(client: Client): Int {
        for (i in 0.rangeTo(model.size() - 1)) {
            val c = model.get(i)
            if (c.id.equals(client.id)) {
                return i
            }
        }
        throw GadsuException("Could not find element '$client' in model: $model")
    }

}
