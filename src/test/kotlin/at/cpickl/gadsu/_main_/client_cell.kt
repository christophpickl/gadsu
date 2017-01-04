package at.cpickl.gadsu._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.ClientList
import at.cpickl.gadsu.client.view.ExtendedClient
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.MyListModel
import org.joda.time.DateTime
import javax.swing.JPanel

fun main(args: Array<String>) {
    Framed.show(JPanel().apply {
        add(ClientList(MyListModel<ExtendedClient>().apply {
            resetData(listOf(
                    ExtendedClient(Client.savedValidInstance(), 0, DateTime.now(), null),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Rise", lastName = "Shine", picture = MyImage.DEFAULT_PROFILE_WOMAN), 1, null, null),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Oxy"), 3, null, 0),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Anna", lastName = "Nym", picture = MyImage.DEFAULT_PROFILE_ALIEN), 5, null, 1),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Thomas"), 9, null, 12),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Rolf"), 10, null, 40),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Oft"), 16, null, 80),
                    ExtendedClient(Client.savedValidInstance().copy(firstName = "Ur oft", birthday = DateTime.now().plusDays(1)), 39, null, 150)
            ))
        }))
    })
}
