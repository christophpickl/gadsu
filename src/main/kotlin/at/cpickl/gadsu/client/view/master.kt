package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.UserEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.client.DeleteClientEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.image.DeleteImageEvent
import at.cpickl.gadsu.image.SelectImageEvent
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.enableSmartPopup
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JList
import javax.swing.ListSelectionModel


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
        private val bus: EventBus,
        swing: SwingFactory
) : GridPanel(), ClientMasterView {

    override val model = MyListModel<Client>()

    private val log = LoggerFactory.getLogger(javaClass)
    private val list = JList<Client>(model)
    private var previousSelected: Client? = null

    init {
        name = ViewNames.Client.MainPanel
        debugColor = Color.RED
        enforceWidth(200)
        initList()

//        c.fill = GridBagConstraints.HORIZONTAL
//        c.weightx = 1.0
//        c.weighty = 1.0
//        val searchField = SearchTextField()
//        add(searchField)
//        c.gridy++

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

    private fun initList() {
        list.name = ViewNames.Client.List
        list.cellRenderer =  object : MyListCellRenderer<Client>() {
            override fun newCell(value: Client) = ClientCell(value)
        }
        list.selectionMode = ListSelectionModel.SINGLE_SELECTION
        list.layoutOrientation = JList.VERTICAL

        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (list.selectedIndex === -1) {
                    // MINOR what to do if selection changes to none? what actually triggers this? deletion of client?!
                    // might also happen because of future search, but this should then lead to NO event publishing (client should stay displayed in detail view)
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

        list.enableSmartPopup(bus, { selectedClient ->
            val list: List<Pair<String, () -> UserEvent>>
            val menuDeleteClient = Pair<String, () -> UserEvent>("Klient L\u00f6schen", { DeleteClientEvent(selectedClient) })

            if (selectedClient.picture.isUnsavedDefaultPicture) {
                list = listOf(
                        Pair<String, () -> UserEvent>("Bild hinzuf\u00fcgen", { SelectImageEvent() }),
                        menuDeleteClient
                )
            } else {
                list = listOf(
                        Pair<String, () -> UserEvent>("Bild \u00e4ndern", { SelectImageEvent() }),
                        Pair<String, () -> UserEvent>("Bild l\u00f6schen", { DeleteImageEvent(selectedClient) }),
                        menuDeleteClient
                )
            }
            list

        })
    }

    override fun asComponent() = this

    override fun initClients(clients: List<Client>) {
        log.trace("initClients(clients={})", clients)
        model.resetData(clients)
    }

    override fun insertClient(index: Int, client: Client) {
        log.trace("insertClient(index={}, client={})", index, client)
        model.add(index, client)
    }

    override fun selectClient(client: Client?) {
        log.trace("selectClient(client={})", client)
        if (client === null) {
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
