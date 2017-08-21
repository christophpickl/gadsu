package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.image.ImageSize
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
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

private val clientDetailFont = JLabel().withFont(Font.BOLD, 9).font

enum class TreatCount(number: Int) {
    Count1(1),
    Count5(5),
    Count10(10);

    val icon = Images.loadFromClasspath("/gadsu/images/treatment_count_$number.png")
}


class ClientCell(val client: ExtendedClient, calc: ThresholdCalculator) : DefaultCellView<ExtendedClient>(client) {

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
        RecentTreatmentPanel(client.differenceDaysToRecentTreatment!!, calc.calc(client))

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
        add(JLabel(drawClientPicture()))

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

    private fun drawClientPicture(): Icon {
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
        // MINOR UI - properly left-align (off with label below)
//        // MINOR UI - could colorize the client created background (if clients.treatmentCnt == 0 ==> calc diff of days)
        add(lbl)
    }
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

    private fun JPanel.add(count: TreatCount) {
        add(JLabel(count.icon))
    }

}
