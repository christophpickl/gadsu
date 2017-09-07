package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.StarSignCalculator
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.DisabledTextField
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.components.panels.VFillFormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.titledBorder
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import org.joda.time.Years
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import javax.swing.JLabel
import javax.swing.JPanel

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        appointmentsSubView: AppoinmentsInClientView,
        treatmentsSubview: TreatmentsInClientView,
        bus: EventBus
) : DefaultClientTab(
        tabTitle = Labels.Tabs.ClientMain,
        type = ClientTabType.MAIN
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    // base
    val inpFirstName = fields.newTextField("Vorname", { it.firstName }, ViewNames.Client.InputFirstName)
    val inpLastName = fields.newTextField("Nachname", { it.lastName }, ViewNames.Client.InputLastName)
    val inpNickNameExt = fields.newTextField("Spitzname (ext.)", { it.nickNameExt }, ViewNames.Client.InputNickNameExt)
    val inpNickNameInt = fields.newTextField("Spitzname (int.)", { it.nickNameInt }, ViewNames.Client.InputNickNameInt)
    val inpGender = fields.newComboBox(Gender.Enum.orderedValues, initialClient.gender, "Geschlecht", { it.gender }, ViewNames.Client.InputGender)
    val inpBirthday = fields.newDatePicker(initialClient.birthday, "Geburtstag", { it.birthday }, ViewNames.Client.InputBirthdayPrefix)
    val outAge = JLabel().bold()
    val outStarsign = DisabledTextField()
    val inpCountryOfOrigin = fields.newTextField("Geburtsort", { it.countryOfOrigin }, ViewNames.Client.InputCountryOfOrigin)
    val inpOrigin = fields.newTextField("Wohnort", { it.origin }, ViewNames.Client.InputOrigin)
    val inpRelationship = fields.newComboBox(Relationship.Enum.orderedValues, initialClient.relationship, "Beziehungsstatus", { it.relationship }, ViewNames.Client.InputRelationship)
    val inpJob = fields.newTextField("Beruf", { it.job }, ViewNames.Client.InputJob)
    val inpChildren = fields.newTextField("Kinder", { it.children }, ViewNames.Client.InputChildren)
    val inpHobbies = fields.newTextField("Hobbies", { it.hobbies }, ViewNames.Client.InputHobbies)
    // FIXME known by
    val inpKnownBy = fields.newTextField("Bekannt durch", { "N/A" }, ViewNames.Client.InputKnownBy)

    // contact
    val inpMail = fields.newTextField("Mail", { it.contact.mail }, ViewNames.Client.InputMail)
    val inpPhone = fields.newTextField("Telefon", { it.contact.phone }, ViewNames.Client.InputPhone)
    val inpStreet = fields.newTextField("Strasse", { it.contact.street }, ViewNames.Client.InputStreet)
    val inpZipCode = fields.newTextField("PLZ", { it.contact.zipCode }, ViewNames.Client.InputZipCode)
    val inpCity = fields.newTextField("Stadt", { it.contact.city }, ViewNames.Client.InputCity)
    val inpWantReceiveMails = fields.newCheckBox("Mails", "Empfangen", { it.wantReceiveMails }, ViewNames.Client.InputReceiveMails, true)
    val inpNote = fields.newTextArea("Notiz", { it.note }, ViewNames.Client.InputNote, bus)

    // texts
    val inpMainObjective = fields.newTextField("Hauptanliegen", { it.textMainObjective }, ViewNames.Client.InputTextMainObjective)
    val inpSymptoms = fields.newTextField("Symptome", { it.textSymptoms }, ViewNames.Client.InputTextSymptoms)
    val inpFiveElements = fields.newTextField("5 Elemente", { it.textFiveElements }, ViewNames.Client.InputTextFiveElements)
    val inpSyndrom = fields.newTextField("Syndrom", { it.textSyndrom }, ViewNames.Client.InputTextSyndrom)

    init {
        debugColor = Color.ORANGE

        val baseForm = FormPanel()
        with(baseForm) {
            titledBorder("Basisdaten")
            debugColor = Color.CYAN
            addFormInput(inpFirstName)
            addFormInput(inpLastName)
            addFormInput(inpNickNameInt)
            addFormInput(inpNickNameExt)
            addFormInput(inpGender)
//            addFormInput(inpBirthday)
            addFormInput(inpBirthday.formLabel, JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                transparent()
                add(inpBirthday.toComponent())
                add(outAge)
            })

            addFormInput("Sternzeichen", outStarsign)
            addFormInput(inpCountryOfOrigin)
            addFormInput(inpOrigin)
            addFormInput(inpRelationship)
            addFormInput(inpJob)
            addFormInput(inpChildren)
            addFormInput(inpHobbies)

        }

        val contactForm = FormPanel().apply {
            titledBorder("Kontaktdaten")
            debugColor = Color.BLUE
            addFormInput(inpMail)
            addFormInput(inpPhone)
            addFormInput(inpStreet)
            addFormInput(inpZipCode)
            addFormInput(inpCity)
            addFormInput(inpWantReceiveMails)
        }
        val additionalTopForm = FormPanel().apply {
            addFormInput(inpMainObjective)
            addFormInput(inpSymptoms)
            addFormInput(inpFiveElements)
            addFormInput(inpSyndrom)
        }
        val additionalBottomForm = FormPanel().apply {
            addFormInput(inpKnownBy)
        }
        val sideForm = GridPanel().apply {
            titledBorder("Zusatzdaten")
//            enforceWidth(200) // this is nearly the same, as the min width of the baseForm (135 for the textfield column, and some more for the label width)
            c.weightx = 1.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.HORIZONTAL
            add(additionalTopForm)
            c.gridy++
            add(contactForm)
            c.gridy++
            add(additionalBottomForm)
        }

        // add content

        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.5
        c.weighty = 0.0
        add(baseForm)

        c.gridx++
        c.insets = Pad.LEFT
        add(sideForm)

        c.gridx++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.0
        c.weighty = 0.0
        add(initListsPanel(appointmentsSubView, treatmentsSubview))

        c.gridx = 0
        c.gridy++
        c.gridwidth = 3
        c.insets = Pad.TOP
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0

        add(VFillFormPanel().apply {
            addFormInput(inpNote)
        })
    }

    private fun initListsPanel(appointmentsSubView: AppoinmentsInClientView, treatmentsSubview: TreatmentsInClientView) = GridPanel().apply {
        with(c) {
            weightx = 1.0
            weighty = 0.2
            fill = GridBagConstraints.BOTH
            add(appointmentsSubView)

            gridy++
            weighty = 0.8
            add(treatmentsSubview)
        }
    }

    override fun isModified(client: Client) = fields.isAnyModified(client)

    override fun updateFields(client: Client) {
        log.trace("updateFields(client={})", client)
        fields.updateAll(client)

        outAge.text = if (client.birthday == null) "" else Years.yearsBetween(client.birthday, DateTime.now()).years.toString() + " Jahre"
        outStarsign.text = if (client.birthday == null) "" else StarSignCalculator.signFor(client.birthday).label
    }

}
