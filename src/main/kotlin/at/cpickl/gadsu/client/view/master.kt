package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.Development
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.enablePopup
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.scrolled
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.ListSelectionModel

@Suppress("UNUSED") val ClientViewNames.List: String get() = "Client.List"
@Suppress("UNUSED") val ClientViewNames.CreateButton: String get() = "Client.CreateButton"

interface ClientMasterView {
    val model: MyListModel<Client>

    fun initClients(clients: List<Client>)
    fun insertClient(index: Int, client: Client)
    /**
     * @param client null to deselect any selected entry
     */
    fun selectClient(client: Client?)
    fun changeClient(client: Client)
    fun deleteClient(client: Client)

    fun asComponent(): Component

}

class SwingClientMasterView @Inject constructor(
        bus: EventBus,
        swing: SwingFactory
) : GridPanel(), ClientMasterView {

    override val model = MyListModel<Client>()

    private val log = LoggerFactory.getLogger(javaClass)
    private val list = JList<Client>(model)
    private var previousSelected: Client? = null

    init {
        if (Development.ENABLED) background = Color.RED

        list.name = ViewNames.Client.List
        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (list.selectedIndex == -1) {
                    // MINOR what to do if selection changes to none? what actually triggers this? deletion of client?!
                } else {
                    log.trace("List selection changed from ({}) to ({})", previousSelected, list.selectedValue)
                    if (!list.selectedValue.equals(previousSelected)) {
                        bus.post(ClientSelectedEvent(list.selectedValue, previousSelected))
                        previousSelected = list.selectedValue
                    } else {
                        log.trace("Suppressing selection event as the very same entry was selected again (changes detected).")
                    }
                }
            }
        }
        list.enablePopup(bus, "L\u00f6schen", { DeleteClientEvent(it) })

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
        add(list.scrolled())

        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        c.gridy++
        c.insets = Insets(5, 0, 0, 0)
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

    override fun selectClient(client: Client?) {
        log.trace("selectClient(client={})", client)
        if (client == null) {
            previousSelected = null
            list.clearSelection()
        } else {
            list.setSelectedValue(client, true)
        }
    }

    override fun changeClient(client: Client) {
        log.trace("changeClient(client={})", client)
        model.setElementByComparator(client, client.idComparator)
    }

    override fun deleteClient(client: Client) {
        log.trace("deleteClient(client={})", client)
        model.removeElementByComparator(client.idComparator)
    }

}
