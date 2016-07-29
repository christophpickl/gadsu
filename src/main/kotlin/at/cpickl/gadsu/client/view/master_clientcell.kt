package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.IClient
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ExtendedClient(
    val client: Client,
    var countTreatments: Int
) : IClient by client, Comparable<ExtendedClient> {

    override fun compareTo(other: ExtendedClient): Int {
        return this.client.compareTo(other.client)
    }
}

class ClientCell(val client: ExtendedClient) : DefaultCellView<ExtendedClient>(client) {

    private val name = JLabel(if(client.state == ClientState.INACTIVE) "(${client.fullName})" else client.fullName).bold()
    private val countTreatments = JLabel("Behandlungen: ${client.countTreatments}")
    override val applicableForegrounds: Array<JComponent> = arrayOf(name, countTreatments)

    init {
//        if (client.state == ClientState.INACTIVE) {
//            name.foreground = Color.LIGHT_GRAY
//        }
//        applicableForegrounds = arrayOf(name, countTreatments)

        val calculatedRows =
                1 + // name
                1 + // count treatments
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

        c.gridy++
        add(countTreatments)

        // fill south gap with a UI hack ;)
        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        val fillGap = JPanel()
        fillGap.isOpaque = false
        add(fillGap)
    }

}

