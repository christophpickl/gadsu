package at.cpickl.gadsu.preferences.view

import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.version.CheckForUpdatesEvent
import at.cpickl.gadsu.view.KTab
import at.cpickl.gadsu.view.SwingFactory
import at.cpickl.gadsu.view.components.MyTextArea
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.inputs.NumberField
import at.cpickl.gadsu.view.components.newEventButton
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.swing.disableFocusable
import at.cpickl.gadsu.view.swing.disabled
import at.cpickl.gadsu.view.swing.leftAligned
import at.cpickl.gadsu.view.swing.scrolled
import at.cpickl.gadsu.view.swing.selectAllOnFocus
import at.cpickl.gadsu.view.swing.transparent
import at.cpickl.gadsu.view.swing.viewName
import java.awt.GridBagConstraints
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField


abstract class PrefsTab(override val tabTitle: String, override val scrolled: Boolean = true) : KTab {

    protected val VGAP_BETWEEN_COMPONENTS = 10

}

class PrefsTabGeneral(swing: SwingFactory) : PrefsTab("Allgemein") {

    val inpUsername = JTextField().viewName { Preferences.InputUsername }
    val inpCheckUpdates = JCheckBox("Beim Start prüfen")
    val inpTreatmentGoal = NumberField(4).selectAllOnFocus().leftAligned()

    val inpApplicationDirectory = JTextField().disabled().disableFocusable()
    val inpLatestBackup = JTextField().disabled().disableFocusable()

    val btnCheckUpdate = swing.newEventButton("Jetzt prüfen", "", { CheckForUpdatesEvent() })

    override fun asComponent() = FormPanel(fillCellsGridy = false, labelAnchor = GridBagConstraints.NORTHWEST).apply {
        border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

        addDescriptiveFormInput("Dein Name", inpUsername, "Dein vollständiger Name wird unter anderem<br/>auf Rechnungen und Berichte (Protokolle) angezeigt.")
        addDescriptiveFormInput("Auto Update", initPanelCheckUpdates(), "Um immer am aktuellsten Stand zu bleiben,<br/>empfiehlt es sich diese Option zu aktivieren.",
                GridBagFill.None, addTopInset = VGAP_BETWEEN_COMPONENTS)
        addDescriptiveFormInput("Behandlungsziel*", inpTreatmentGoal, "Setze dir ein Ziel wieviele (unprotokollierte) Behandlungen du schaffen m\u00f6chtest.")

        addDescriptiveFormInput("Programm Ordner", inpApplicationDirectory, "Hier werden die progamm-internen Daten gespeichert.",
                addTopInset = VGAP_BETWEEN_COMPONENTS)
        addDescriptiveFormInput("Letztes Backup", inpLatestBackup, "Gadsu erstellt für dich täglich ein Backup aller Informationen.",
                addTopInset = VGAP_BETWEEN_COMPONENTS)

        c.gridwidth = 2
        add(HtmlEditorPane("<b>*</b> ... <i>Neustart erforderlich</i>").disableFocusable())
        addLastColumnsFilled()
    }

    private fun initPanelCheckUpdates() = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        transparent()

        add(inpCheckUpdates)
        add(btnCheckUpdate)
    }

}

class PrefsTabConnectivity : PrefsTab("Connectivity") {

    val inpProxy = JTextField()
    val inpGcalName = JTextField()
    val inpGmailAddress = JTextField()
    val inpGapiClientId = JTextField()
    val inpGapiClientSecret = JTextField()
    val inpConfirmMailSubject = JTextField()
    val inpConfirmMailBody = MyTextArea("", visibleRows = 6)

    override fun asComponent() = FormPanel(
            fillCellsGridy = false,
            labelAnchor = GridBagConstraints.NORTHWEST,
            inputAnchor = GridBagConstraints.NORTHWEST).apply {
        border = BorderFactory.createEmptyBorder(10, HGAP_FROM_WINDOW, 0, HGAP_FROM_WINDOW)

        addDescriptiveFormInput("HTTP Proxy*", inpProxy, "Falls du \u00fcber einen Proxy ins Internet gelangst,<br/>dann konfiguriere diesen bitte hier. (z.B.: <tt>proxy.heim.at:8080</tt>)")
        addDescriptiveFormInput("Google Calendar*", inpGcalName, "Trage hier den Kalendernamen ein um die Google Integration einzuschalten.")
        addDescriptiveFormInput("GMail Addresse", inpGmailAddress, "Trage hier deine GMail Adresse ein für das Versenden von E-Mails.")
        addDescriptiveFormInput("Google API ID", inpGapiClientId, "Um die Google API nutzen zu können, brauchst du eine Zugangs-ID.<br/>" +
                "Credentials sind erstellbar in der Google API Console.<br/>" +
                "Bsp.: <tt>123456789012-aaaabbbbccccddddeeeefffffaaaabb.apps.googleusercontent.com</tt>")
        addDescriptiveFormInput("Google API Secret", inpGapiClientSecret, "Das zugehörige Passwort.<br/>" +
                "Bsp.: <tt>AABBCCDDDaabbccdd12345678</tt>")
        addDescriptiveFormInput("Mail Betreff", inpConfirmMailSubject, "Bestätigungsmail Freemarker Template für den Betreff. Mögliche Variablen: name, gender, dateStart, dateEnd")
        // for available variables see: AppointmentConfirmationerImpl
        addDescriptiveFormInput(
                label = "Mail Text",
                input = inpConfirmMailBody.scrolled(),
                description = "Bestätigungsmail Freemarker Template für den Text, welcher die selben Variablen nutzen kann wie die Betreff Vorlage.",
                fillType = GridBagFill.Both,
                inputWeighty = 1.0
        )
    }

}
