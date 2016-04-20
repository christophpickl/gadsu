package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.view.components.*
import java.awt.*
import javax.swing.*


fun main(args: Array<String>) {
    val model = MyListModel<Client>()
    val _contact = Client.INSERT_PROTOTYPE.contact
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Max", lastName = "Mustermann", contact = _contact.copy(mail = "max@muster.com")))
    model.addElement(Client.INSERT_PROTOTYPE.copy(firstName = "Anna", lastName = "Nym", picture = Images.DEFAULT_PROFILE_WOMAN))
    val list = JList<Client>(model)
    list.cellRenderer = ClientListCellRender()
    Framed.show(list.scrolled(), Dimension(300, 500))
}

class ClientCell(val client: Client) : GridPanel() {
    companion object {
        private val INSET_0 = Insets(0, 0, 0, 0)
        private val INSET_PIC = Insets(0, 0, 0, 4)
    }
    private val name = JLabel(client.fullName).bold()
    private val mail = JLabel("Mail: ${client.contact.mail}")
    init {
        val mailPresent = client.contact.mail.isNotEmpty()
        val calculatedRows =
                1 + // name
                (if (mailPresent) 1 else 0 ) +
                1 // ui hack to fill vertical space

        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = INSET_PIC
        c.gridheight = calculatedRows
        add(JLabel(client.picture.toViewLilRepresentation()))

        c.gridheight = 1
        c.insets = INSET_0
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

    fun changeForegrounds(foreground: Color) {
        name.foreground = foreground
        mail.foreground = foreground
    }
}

class ClientListCellRender : ListCellRenderer<Client> {
    override fun getListCellRendererComponent(list: JList<out Client>, client: Client, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val cell = ClientCell(client)

        if (isSelected) {
            cell.changeForegrounds(UIManager.getColor("List.selectionForeground"))
            cell.background = UIManager.getColor("List.selectionBackground")
        } else {
            cell.isOpaque = false
        }

        return cell
    }

}
