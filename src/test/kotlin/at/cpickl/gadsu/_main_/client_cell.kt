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
        val xclient1 = ExtendedClient(Client.savedValidInstance(), 42, DateTime.now(), null)
        val xclient2 = ExtendedClient(Client.savedValidInstance().copy(firstName = "Rise", lastName = "Shine", picture = MyImage.DEFAULT_PROFILE_WOMAN), 0, null, null)
        val xclient3 = ExtendedClient(Client.savedValidInstance().copy(
                firstName = "Anna", lastName = "Nym", picture = MyImage.DEFAULT_PROFILE_ALIEN),
                0, null, 12
        )
        add(ClientList(MyListModel<ExtendedClient>().apply { resetData(listOf(xclient1, xclient2, xclient3)) }))
    })
}
