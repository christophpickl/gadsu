package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.IClient
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.ImageSize
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.clearTime
import at.cpickl.gadsu.service.differenceDaysWithinYear
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.service.formatDateNoYear
import at.cpickl.gadsu.service.formatTimeWithoutSeconds
import at.cpickl.gadsu.service.isBetweenInclusive
import at.cpickl.gadsu.service.wrapParenthesisIf
import at.cpickl.gadsu.view.Images
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.transparent
import at.cpickl.gadsu.view.swing.withFont
import at.cpickl.gadsu.view.swing.withFontSize
import com.google.common.annotations.VisibleForTesting
import org.joda.time.DateTime
import org.joda.time.Days
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.image.BufferedImage
import javax.inject.Inject
import javax.swing.Icon
import javax.swing.ImageIcon
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
    override val hasMail: Boolean get() = client.hasMail
    override val hasMailAndWantsMail: Boolean get() = client.hasMailAndWantsMail
    override val wantReceiveMails = client.wantReceiveMails
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
    override val category: ClientCategory get() = client.category
    override val donation: ClientDonation get() = client.donation

    override val tcmNote: String get() = client.tcmNote
    override val picture: MyImage get() = client.picture
    override val cprops: CProps get() = client.cprops


}

private val clientDetailFont = JLabel().withFont(Font.BOLD, 9).font

enum class TreatCount(number: Int) {
    Count1(1),
    Count5(5),
    Count10(10);

    val icon = Images.loadFromClasspath("/gadsu/images/treatment_count_$number.png")
}


class ThresholdColorCalculator @Inject constructor(private val prefs: Prefs) {

    private val LIMIT_MODIFIER_A = 0.6
    private val LIMIT_MODIFIER_B = 1.0
    private val LIMIT_MODIFIER_C = 1.4

    fun calc(client: ExtendedClient): ThresholdColor {
        val days = client.differenceDaysToRecentTreatment!!
        val category = client.category
        val nextAppointment = client.upcomingAppointment

        // FIXME #112 implement me
//        if (nextAppointment != null) {
//            return ThresholdColor.GotNextAppointment
//        }
//        val limitModifier = if (category == ClientCategory.A) LIMIT_MODIFIER_A else if (category == ClientCategory.B) LIMIT_MODIFIER_B else LIMIT_MODIFIER_C
//
//        val limitOk = (RecentState.Ok.baseLimit * limitModifier).toInt()
//        val limitAttention = (RecentState.Attention.baseLimit * limitModifier).toInt()
//        val limitWarn = (RecentState.Warn.baseLimit * limitModifier).toInt()
//        val limitCritical = (RecentState.Critical.baseLimit * limitModifier).toInt()
//
//        return if (days < limitOk) RecentState.Ok
//        else if (days < limitAttention) RecentState.Attention
//        else if (days < limitWarn) RecentState.Warn
//        else if (days < limitCritical) RecentState.Critical
//        else RecentState.Fatal

        return ThresholdColor.Attention
    }
}


class ClientCell(val client: ExtendedClient, colorCalc: ThresholdColorCalculator) : DefaultCellView<ExtendedClient>(client) {

    companion object {
        private val BIRTHDAY_ICON = Images.loadFromClasspath("/gadsu/images/birthday.png")

        @VisibleForTesting
        fun upcomingAppointmentLabel(now: DateTime, date: DateTime): String {
            if (date.isBefore(now)) {
                return "N/A"
            }
            val days = Days.daysBetween(now.clearTime(), date.clearTime()).days
            return when (days) {
                0 -> "Heute, um ${date.formatTimeWithoutSeconds()} Uhr"
                1 -> "Morgen, um ${date.formatTimeWithoutSeconds()} Uhr"
                2 -> "Übermorgen, um ${date.formatTimeWithoutSeconds()} Uhr"
                else -> {
                    "In $days Tage, am ${date.formatDateNoYear()}"
                }
            }
        }

        private val CATEGORY_A_IMAGE = Images.loadFromClasspath("/gadsu/images/clientcategory_indicator_up.png")
        private val CATEGORY_C_IMAGE = Images.loadFromClasspath("/gadsu/images/clientcategory_indicator_down.png")
        private val DONATION_UNKNOWN_IMAGE = Images.loadFromClasspath("/gadsu/images/client_donation_unknown.png")
        private val DONATION_NONE_IMAGE = Images.loadFromClasspath("/gadsu/images/client_donation_none.png")
        private val DONATION_PRESENT_IMAGE = Images.loadFromClasspath("/gadsu/images/client_donation_present.png")
        private val DONATION_MONEY_IMAGE = Images.loadFromClasspath("/gadsu/images/client_donation_money.png")
        private val CATEGORY_IMAGE_SIZE = 15
    }

    private val nameLbl = JLabel(client.preferredName.wrapParenthesisIf(client.state == ClientState.INACTIVE)).withFont(Font.BOLD, 16)
    private val upcomingAppointment = JLabel(if (client.upcomingAppointment == null) "" else upcomingAppointmentLabel(DateTime.now(), client.upcomingAppointment!!))

    private val detailLabels = arrayOf(upcomingAppointment)
    override val applicableForegrounds: Array<JComponent> = arrayOf(nameLbl, upcomingAppointment)

    private fun ExtendedClient.hasSoonBirthday() = birthday != null && DateTime.now().differenceDaysWithinYear(birthday!!).isBetweenInclusive(0, 14)

    private val recentPanel = if (client.differenceDaysToRecentTreatment == null) null else
        RecentTreatmentPanel(client.differenceDaysToRecentTreatment!!, colorCalc.calc(client))

    private val createdPanel = if (client.differenceDaysToRecentTreatment != null) null else
        ClientCreatedPanel(client.created)

    private val countPanel = TreatmentCountPanel(client.countTreatments)

    override fun onChangeForeground(foreground: Color) {
        countPanel.countLabel.foreground = foreground
        recentPanel?.apply { labelColor = foreground }
        createdPanel?.apply { lbl.foreground = foreground }
    }

    init {
        detailLabels.forEach { it.font = clientDetailFont }

        val calculatedRows =
                1 + // name
                        1 + // count treatments
                        (if (client.upcomingAppointment == null) 0 else 1) +
                        (if (client.differenceDaysToRecentTreatment == null) 0 else 1) +
                        1 // ui hack to fill vertical space

        c.anchor = GridBagConstraints.NORTHWEST
        c.insets = Pad.RIGHT
        c.gridheight = calculatedRows
        add(JLabel(drawClientPictureWithCategoryIndicator()))

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

        c.gridy++
        c.insets = Insets(2, 0, 2, 0)
        val recentOrCreatedPanel = if (client.differenceDaysToRecentTreatment == null) {
            createdPanel!!
        } else {
            recentPanel!!
        }
        add(JPanel(BorderLayout()).apply { transparent(); add(recentOrCreatedPanel, BorderLayout.WEST) })

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

    private fun drawClientPictureWithCategoryIndicator(): Icon {
        val clientImage = client.picture.toViewLilRepresentation()
        val finalImage = BufferedImage(ImageSize.LITTLE.width, ImageSize.LITTLE.height, BufferedImage.TYPE_INT_ARGB)
        val g = finalImage.createGraphics()
        g.drawImage(clientImage.image, 0, 0, null)

        if (client.category != ClientCategory.B) {
            g.drawImage((if (client.category == ClientCategory.A) CATEGORY_A_IMAGE else CATEGORY_C_IMAGE).image,
                    ImageSize.LITTLE.width - CATEGORY_IMAGE_SIZE - 2,
                    ImageSize.LITTLE.height - CATEGORY_IMAGE_SIZE - 2, null)
        }
        val donationImage = when (client.donation) {
            ClientDonation.MONEY -> DONATION_MONEY_IMAGE
            ClientDonation.UNKNOWN -> DONATION_UNKNOWN_IMAGE
            ClientDonation.NONE -> DONATION_NONE_IMAGE
            ClientDonation.PRESENT -> DONATION_PRESENT_IMAGE
        }
        g.drawImage(donationImage.image,
                0,
                0, null)

        g.dispose()
        return ImageIcon(finalImage)
    }

}

private class ClientCreatedPanel(created: DateTime) : JPanel() {
    val lbl = JLabel("Erstellt am: ${created.formatDate()}").apply { font = clientDetailFont }
    init {
        transparent()
//        // MINOR could colorize the client created background (if clients.treatmentCnt == 0 ==> calc diff of days)
        add(lbl)
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
