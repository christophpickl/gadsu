package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.Pad
import at.cpickl.gadsu.view.components.bold
import at.cpickl.gadsu.view.components.scrolled
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel


fun main(args: Array<String>) {
    val model = MyListModel<Client>()
    val _contact = Client.INSERT_PROTOTYPE.contact
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Max", lastName = "Mustermann", contact = _contact.copy(mail = "max@muster.com")))
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Anna", lastName = "Nym", picture = Images.DEFAULT_PROFILE_WOMAN))
    val list = JList<Client>(model)
    list.cellRenderer = ClientListCellRender()
    Framed.show(list.scrolled(), Dimension(300, 500))
}


class ClientCell(private val client: Client) : DefaultCellView<Client>(client) {

    private val name = JLabel(client.fullName).bold()
    private val mail = JLabel("Mail: ${client.contact.mail}")
    override val applicableForegrounds: Array<JComponent>

    init {
        applicableForegrounds = arrayOf(name, mail)

        val mailPresent = client.contact.mail.isNotEmpty()
        val calculatedRows =
                1 + // name
                (if (mailPresent) 1 else 0 ) +
                1 // ui hack to fill vertical space


        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.RIGHT
        c.gridheight = calculatedRows
        add(JLabel(client.picture.toViewLilRepresentation()))

        c.gridheight = 1
        c.insets = Pad.ZERO
        c.weightx = 1.0
        c.gridx++
        c.fill = GridBagConstraints.HORIZONTAL
        add(name)

        if (mailPresent) {
            c.gridy++
            add(mail)
        }

        // fill south gap with a UI hack ;)
        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        val fillGap = JPanel()
        fillGap.isOpaque = false
        add(fillGap)
    }

}

class ClientListCellRender : MyListCellRenderer<Client>() {
    override fun newCell(value: Client) = ClientCell(value)
}
