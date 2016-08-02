package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.*
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.formatDateTimeSemiLong
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import org.joda.time.DateTime
import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ExtendedClient(
    var client: Client,
    var countTreatments: Int,
    var upcomingAppointment: DateTime?
) : IClient, Comparable<ExtendedClient> {

    override fun compareTo(other: ExtendedClient): Int {
        return this.client.compareTo(other.client)
    }

    // by client delegation does not work for mutable var fields :-/
    override val id: String? get() = client.id
    override val yetPersisted: Boolean get() = client.yetPersisted
    override val created: DateTime get() = client.created
    override val firstName: String get() = client.firstName
    override val lastName: String get() = client.lastName
    override val fullName: String get() = client.fullName
    override val state: ClientState get() = client.state
    override val contact: Contact get() = client.contact
    override val birthday: DateTime? get() = client.birthday
    override val gender: Gender get() = client.gender
    override val countryOfOrigin: String get() = client.countryOfOrigin
    override val origin: String get() = client.origin
    override val relationship: Relationship get() = client.relationship
    override val job: String get() = client.job
    override val children: String get() = client.children
    override val hobbies: String get() = client.hobbies
    override val note: String get() = client.note
    override val textImpression: String get() = client.textImpression
    override val textMedical: String get() = client.textMedical
    override val textComplaints: String get() = client.textComplaints
    override val textPersonal: String get() = client.textPersonal
    override val textObjective: String get() = client.textObjective
    override val tcmNote: String get() = client.tcmNote
    override val picture: MyImage get() = client.picture
    override val cprops: CProps get() = client.cprops

}

class ClientCell(val client: ExtendedClient) : DefaultCellView<ExtendedClient>(client) {

    private val name = JLabel(if(client.state == ClientState.INACTIVE) "(${client.fullName})" else client.fullName).bold()
    private val countTreatments = JLabel("Behandlungen: ${client.countTreatments}")
    private val upcomingAppointment = JLabel("Wiedersehen: ${client.upcomingAppointment?.formatDateTimeSemiLong()}")
    override val applicableForegrounds: Array<JComponent> = arrayOf(name, countTreatments, upcomingAppointment)

    init {
//        if (client.state == ClientState.INACTIVE) {
//            name.foreground = Color.LIGHT_GRAY
//        }
//        applicableForegrounds = arrayOf(name, countTreatments)

        val calculatedRows =
                1 + // name
                1 + // count treatments
                (if (client.upcomingAppointment == null) 0 else 1) +
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

        if (client.upcomingAppointment != null) {
            c.gridy++
            add(upcomingAppointment)
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

