package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.IClient
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.differenceDaysWithinYear
import at.cpickl.gadsu.service.formatDateTimeSemiLong
import at.cpickl.gadsu.service.isBetweenInclusive
import at.cpickl.gadsu.service.wrapParenthesisIf
import at.cpickl.gadsu.view.Images
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.enforceSize
import at.cpickl.gadsu.view.swing.transparent
import at.cpickl.gadsu.view.swing.withFont
import at.cpickl.gadsu.view.swing.withFontSize
import org.joda.time.DateTime
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ExtendedClient(
        var client: Client,
        var countTreatments: Int,
        var upcomingAppointment: DateTime?,
        var differenceDaysToRecentTreatment: Int?
) : IClient, Comparable<ExtendedClient> {

    override fun compareTo(other: ExtendedClient): Int {
        return this.client.compareTo(other.client)
    }

    override fun toString(): String {
        return "ExtendedClient(client=$client, countTreatments=$countTreatments, upcomingAppointment=$upcomingAppointment)"
    }

    // by client delegation does not work for mutable var fields :-/
    override val id: String? get() = client.id
    override val yetPersisted: Boolean get() = client.yetPersisted
    override val created: DateTime get() = client.created
    override val firstName: String get() = client.firstName
    override val lastName: String get() = client.lastName
    override val nickName: String get() = client.nickName
    override val preferredName: String get() = client.preferredName
    override val fullName: String get() = client.fullName
    override val state: ClientState get() = client.state
    override val contact: Contact get() = client.contact
    override val wantReceiveDoodleMails = client.wantReceiveDoodleMails
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

    override val textMainObjective: String get() = client.textMainObjective
    override val textSymptoms: String get() = client.textSymptoms
    override val textFiveElements: String get() = client.textFiveElements
    override val textSyndrom: String get() = client.textSyndrom

    override val tcmNote: String get() = client.tcmNote
    override val picture: MyImage get() = client.picture
    override val cprops: CProps get() = client.cprops


}

enum class TreatCount(number: Int) {
    Count1(1),
    Count5(5),
    Count10(10);

    val icon = Images.loadFromClasspath("/gadsu/images/treatment_count_$number.png")
}

class ClientCell(val client: ExtendedClient) : DefaultCellView<ExtendedClient>(client) {

    companion object {
        private val BIRTHDAY_ICON = Images.loadFromClasspath("/gadsu/images/birthday.png")
    }

    private val nameLbl = JLabel(client.preferredName.wrapParenthesisIf(client.state == ClientState.INACTIVE)).withFont(Font.BOLD, 16)
    private val upcomingAppointment = JLabel("Wiedersehen: ${client.upcomingAppointment?.formatDateTimeSemiLong()}")

    private val detailLabels = arrayOf(upcomingAppointment)
    override val applicableForegrounds: Array<JComponent> = arrayOf(nameLbl, upcomingAppointment)

    private fun ExtendedClient.hasSoonBirthday() = birthday != null && DateTime.now().differenceDaysWithinYear(birthday!!).isBetweenInclusive(0, 14)

    private val recentPanel = if(client.differenceDaysToRecentTreatment == null) null else RecentTreatmentPanel(client.differenceDaysToRecentTreatment!!)
    private val countPanel = TreatmentCountPanel(client.countTreatments)

    override fun onChangeForeground(foreground: Color) {
        countPanel.countLabel.foreground = foreground
        recentPanel?.apply { labelColor = foreground }
    }

    init {
        val detailFont = nameLbl.font.deriveFont(9.0F)
        detailLabels.forEach { it.font = detailFont }

        val calculatedRows =
                1 + // name
                        1 + // count treatments
                        (if (client.upcomingAppointment == null) 0 else 1) +
                        (if (client.differenceDaysToRecentTreatment == null) 0 else 1) +
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
        if (client.hasSoonBirthday()) {
            add(GridPanel().apply {
                c.insets = Pad.NONE
                c.fill = GridBagConstraints.NONE
                c.weightx = 0.0
                c.anchor = GridBagConstraints.WEST
                add(JLabel(BIRTHDAY_ICON))

                c.gridx++
                c.weightx = 1.0
                c.insets = Pad.LEFT
                c.anchor = GridBagConstraints.SOUTHWEST
                add(nameLbl)
            })
        } else {
            add(nameLbl)
        }

        c.gridy++
        c.insets = Pad.ZERO
        add(countPanel)

        if (client.differenceDaysToRecentTreatment != null) {
            c.gridy++
            c.insets = Insets(2, 0, 2, 0)
            add(JPanel(BorderLayout()).apply { transparent(); add(recentPanel!!, BorderLayout.WEST) })
        }

        if (client.upcomingAppointment != null) {
            c.insets = Pad.ZERO
            c.gridy++
            add(upcomingAppointment)
        }

        // fill south gap with a UI hack ;)
        c.gridy++
        c.insets = Pad.ZERO
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(JPanel().transparent())
    }

}

private class RecentTreatmentPanel(private val days: Int) : JPanel() {
    companion object {
        private fun labelTextForRecentTreatment(days: Int): String {
            if (days < 0) {
                return "Funny?!"
            }
            return when (days) {
                0 -> "Heute"
                1 -> "Gestern"
                2 -> "Vorgestern"
                else -> "Vor $days Tagen"
            }
        }

        private fun calculateColor(days: Int): Color {
            return if (days < 10) Color.GREEN
            else if (days < 20) Color.YELLOW
            else if (days < 30) Color.ORANGE
            else if (days < 100) Color.RED
            else Color.PINK
        }

        private val relativeMaxPivot = 40 // will end up in 100% horizontal filled bar
    }

    private val labelText = labelTextForRecentTreatment(days)
    private val color = calculateColor(days)
    private val color2 = color.darker()
    private val percentWidth = Math.min(days.toFloat() / relativeMaxPivot, 1.0F)
    var labelColor = Color.BLACK!!

    init {
        enforceSize(138, 12)
    }

    override fun paint(g: Graphics) {
        super.paint(g)

        g.color = color
        g.fillRect(0, 0, width, height)

        g.color = color2
        g.fillRect(0, 0, (width * percentWidth).toInt(), height)
        g.drawRect(0, 0, width - 1, height - 1)

        g.color = labelColor
        g.font = g.font.deriveFont(9.0F)
        g.drawString(labelText, 4, 9)
    }

}

fun JPanel.add(count: TreatCount) {
    add(JLabel(count.icon))
}

private class TreatmentCountPanel(count: Int) : GridPanel() {

    companion object {
        private val GAP_SMALL = Pad.right(1)
        private val GAP_BIG = Pad.right(4)
    }

    val countLabel = JLabel("Bhdlg: $count").withFontSize(11)



    init {
        c.weightx = 0.0
        c.weighty = 0.0
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.WEST
        c.insets = Pad.right(5)

        add(countLabel)

        val treatCounts = count.downTo(1).map { TreatCount.Count1 }
        if (count >= 10) {
            c.gridx++
            add(TreatCount.Count10)
        } else if (count >= 5) {
            c.gridx++
            add(TreatCount.Count5)
        }

        treatCounts.forEachIndexed { i, treatCount ->
            c.gridx++
            c.insets = if ((i + 1) % 5 == 0) GAP_BIG else GAP_SMALL
            add(treatCount)
        }

        // fill east gap with a UI hack ;)
        c.gridx++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        add(JPanel().transparent())
    }
}
