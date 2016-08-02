package at.cpickl.gadsu._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.ClientCell
import at.cpickl.gadsu.client.view.ExtendedClient
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.swing.scrolled
import java.awt.Dimension
import javax.swing.JList


fun main(args: Array<String>) {
    val model = MyListModel<Client>()
    val _contact = Client.INSERT_PROTOTYPE.contact
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Max", lastName = "Mustermann", contact = _contact.copy(mail = "max@muster.com")))
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Anna", lastName = "Nym", picture = MyImage.DEFAULT_PROFILE_WOMAN))
    val list = JList<Client>(model)
    list.cellRenderer = object : MyListCellRenderer<Client>() {
        override fun newCell(value: Client) = ClientCell(ExtendedClient(value, 42, null))
    }
    Framed.show(list.scrolled(), Dimension(300, 500))
}
