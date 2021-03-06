package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientChangeCategory
import at.cpickl.gadsu.client.ClientChangeDonation
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientSearchEvent
import at.cpickl.gadsu.client.ClientSelectedEvent
import at.cpickl.gadsu.client.CreateNewClientEvent
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.global.GadsuException
import at.cpickl.gadsu.image.DeleteImageEvent
import at.cpickl.gadsu.image.SelectImageEvent
import at.cpickl.gadsu.service.Clock
import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.LiveSearchField
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.containsById
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.logic.addEnumMenu
import at.cpickl.gadsu.view.logic.addKPopup
import at.cpickl.gadsu.view.logic.addKPopupItem
import at.cpickl.gadsu.view.swing.enableHoverListener
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import org.joda.time.DateTime
import java.awt.Color
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import java.util.*
import javax.swing.JList
import javax.swing.ListSelectionModel


interface ClientMasterView {
    val model: MyListModel<ExtendedClient>

    fun initClients(clients: List<ExtendedClient>)
    fun insertClient(index: Int, client: ExtendedClient)
    /**
     * @param client null to deselect any selected entry
     */
    fun selectClient(client: Client?)

    fun trySelectClient(client: Client)
    fun selectedClient(): ExtendedClient?

    fun changeClient(client: Client)
    fun deleteClient(client: Client)

    fun asComponent(): Component

    fun hasPrevNextNeighbour(client: Client): Pair<Client?, Client?>
    fun selectPrevious()
    fun selectNext()

    fun treatmentCountIncrease(clientId: String)
    fun treatmentCountDecrease(clientId: String)

    fun changeUpcomingAppointment(clientId: String, date: DateTime?)
    fun changeDifferenceDaysToRecentTreatment(clientId: String, days: Int?)

}

class ClientList(model: MyListModel<ExtendedClient>, calc: ThresholdCalculator, clock: Clock) : JList<ExtendedClient>(model) {
    init {
        name = ViewNames.Client.List

        val myCellRenderer = object : MyListCellRenderer<ExtendedClient>() {
            override fun newCell(value: ExtendedClient) = ClientCell(value, calc, clock)
        }
        cellRenderer = myCellRenderer
        enableHoverListener(myCellRenderer)
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        layoutOrientation = JList.VERTICAL
    }
}

class SwingClientMasterView @Inject constructor(
        private val bus: EventBus,
        swing: SwingFactory,
        calc: ThresholdCalculator,
        clock: Clock
) : GridPanel(), ClientMasterView {

    override val model = MyListModel<ExtendedClient>()

    private val log = LOG(javaClass)
    private val searchField = LiveSearchField("client_name_search").enableSearchVariant()
    private val list = ClientList(model, calc, clock)
    private var previousSelected: ExtendedClient? = null
    private val client2extended: MutableMap<String, ExtendedClient> = HashMap()

    init {
        name = ViewNames.Client.MainPanel
        debugColor = Color.RED
        enforceWidth(222)
        searchField.addListener {
            bus.post(ClientSearchEvent(it))
        }
        initList()

        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.weighty = 0.0

        add(searchField.asComponent())

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(list.scrolled())

        c.fill = GridBagConstraints.HORIZONTAL
        c.weighty = 0.0
        c.gridy++
        c.insets = Insets(5, 0, 0, 0)
        add(swing.newEventButton("Neuen Klienten anlegen", ViewNames.Client.CreateButton, { CreateNewClientEvent() }))
    }

    private fun initList() {
        list.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                if (list.selectedIndex == -1) {
                    // ??? what to do if selection changes to none? what actually triggers this? deletion of client?!
                    // might also happen because of future search, but this should then lead to NO event publishing (client should stay displayed in detail view)
                } else {
                    log.trace("List selection changed from ({}) to ({})", previousSelected, list.selectedValue)
                    if (list.selectedValue != previousSelected) {
                        bus.post(ClientSelectedEvent(list.selectedValue.client, previousSelected?.client))
                        previousSelected = list.selectedValue
                    } else {
                        log.trace("Suppressing selection event as the very same entry was selected again (changes detected).")
                    }
                }
            }
        }

        list.addKPopup { selectedClient ->
            if (selectedClient.picture.isUnsavedDefaultPicture) {
                addKPopupItem(bus, "Bild hinzuf\u00fcgen", { SelectImageEvent() })
            } else {
                addKPopupItem(bus, "Bild \u00e4ndern", { SelectImageEvent() })
                addKPopupItem(bus, "Bild l\u00f6schen", { DeleteImageEvent(selectedClient.client) })
            }
            addSeparator()
            addEnumMenu(bus, "Spende", ClientDonation.Enum, { ClientChangeDonation(selectedClient.client, it) }, { selectedClient.client.donation })
            addEnumMenu(bus, "Kategorie", ClientCategory.Enum, { ClientChangeCategory(selectedClient.client, it) }, { selectedClient.client.category })
        }
    }

    override fun asComponent() = this

    override fun initClients(clients: List<ExtendedClient>) {
        log.trace("initClients(clients.size={})", clients.size)

        model.resetData(clients)

        client2extended.clear()
        clients.forEach { client2extended[it.client.id!!] = it }
    }

    override fun insertClient(index: Int, client: ExtendedClient) {
        log.trace("insertClient(index={}, client={})", index, client)
        model.add(index, client)
        client2extended[client.client.id!!] = client
    }

    override fun selectClient(client: Client?) {
        log.trace("selectClient(client={})", client)
        if (client == null) {
            previousSelected = null
            list.clearSelection()
        } else {
            list.setSelectedValue(client.toExtended(), true)
        }
    }

    override fun trySelectClient(client: Client) {
        val extended = client.tryToExtended() ?: return // was previously selected, now it's gone ;)
        list.setSelectedValue(extended, true)
    }

    override fun selectedClient(): ExtendedClient? {
        return list.selectedValue
    }

    override fun hasPrevNextNeighbour(client: Client): Pair<Client?, Client?> {
        log.trace("hasPrevNextNeighbour(client={})", client)
        val index = model.indexOf(client.toExtended())
        if (index == -1) {
            throw GadsuException("Can not compute neighbours for not list containing client: $client")
        }
        val previousNeighbour = if (index == 0) null else model.elementAt(index - 1)
        val nextNeighbour = if (index == model.size - 1) null else model.elementAt(index + 1)
        return Pair(previousNeighbour?.client, nextNeighbour?.client)
    }

    override fun selectPrevious() {
        log.trace("selectPrevious()")
        list.selectedIndex = list.selectedIndex - 1
    }

    override fun selectNext() {
        log.trace("selectNext()")
        list.selectedIndex = list.selectedIndex + 1
    }

    override fun changeClient(client: Client) {
        log.trace("changeClient(client={})", client)
        val xclient = client.toExtended()
        xclient.client = client
        model.setElementByComparator(xclient, xclient.idXComparator)
    }

    override fun deleteClient(client: Client) {
        log.trace("deleteClient(client={})", client)
        val xclient = client.toExtended()
        if (model.containsById(xclient)) {
            model.removeElementByComparator(xclient.idXComparator)
            client2extended.remove(client.id!!)
        } else {
            log.trace("client not currently displaying in master list (show inactives is disabled)")
        }
    }

    override fun treatmentCountIncrease(clientId: String) {
        xclientById(clientId).countTreatments++
    }

    override fun treatmentCountDecrease(clientId: String) {
        xclientById(clientId).countTreatments--
    }

    override fun changeUpcomingAppointment(clientId: String, date: DateTime?) {
        xclientById(clientId).upcomingAppointment = date
        list.repaint()
    }

    override fun changeDifferenceDaysToRecentTreatment(clientId: String, days: Int?) {
        xclientById(clientId).differenceDaysToRecentTreatment = days
        list.repaint()
    }

    private fun xclientById(clientId: String) =
            client2extended.values.firstOrNull { it.id == clientId }
                    ?: throw GadsuException("Not found client by ID: '$clientId'! (available: ${client2extended.values}")

    private fun Client.toExtended(): ExtendedClient {
        return client2extended[this.id!!]
                ?: throw GadsuException("Internal state error! Could not find extended client by: $this (map: $client2extended)")
    }

    private fun Client.tryToExtended(): ExtendedClient? {
        return client2extended[this.id!!]
    }

    private val ExtendedClient.idXComparator: (ExtendedClient) -> Boolean
        get() = { that -> this.id.equals(that.id) }

}
