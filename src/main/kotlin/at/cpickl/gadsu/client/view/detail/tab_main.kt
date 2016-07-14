package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.appointment.view.AppoinmentsInClientView
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.StarSignCalculator
import at.cpickl.gadsu.service.SuggesterController
import at.cpickl.gadsu.service.formatDate
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
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.titledBorder
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.GridBagConstraints

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        appointmentsSubView: AppoinmentsInClientView,
        treatmentsSubview: TreatmentsInClientView,
        suggester: SuggesterController
) : DefaultClientTab(
        title = Labels.Tabs.ClientMain,
        type = ClientTabType.MAIN
) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    // base
    val inpFirstName = fields.newTextField("Vorname", {it.firstName}, ViewNames.Client.InputFirstName)
    val inpLastName = fields.newTextField("Nachname", {it.lastName}, ViewNames.Client.InputLastName)
    val inpGender = fields.newComboBox(Gender.orderedValues, initialClient.gender, "Geschlecht", {it.gender}, ViewNames.Client.InputGender)
    val inpBirthday = fields.newDatePicker(initialClient.birthday, "Geburtstag", {it.birthday}, ViewNames.Client.InputBirthdayPrefix)
    val outStarsign= DisabledTextField()
    val inpCountryOfOrigin = fields.newTextField("Geburtsort", {it.countryOfOrigin}, ViewNames.Client.InputCountryOfOrigin)
    val inpOrigin = fields.newTextField("Herkunft", {it.origin}, ViewNames.Client.InputOrigin)
    val inpRelationship = fields.newComboBox(Relationship.orderedValues, initialClient.relationship, "Beziehungsstatus", {it.relationship}, ViewNames.Client.InputRelationship)
    val inpJob = fields.newTextField("Beruf", {it.job}, ViewNames.Client.InputJob)
    val inpChildren = fields.newTextField("Kinder", {it.children}, ViewNames.Client.InputChildren)
    val inpHobbies = fields.newTextField("Hobbies", {it.hobbies}, ViewNames.Client.InputHobbies)
    val outCreated = DisabledTextField()

    // contact
    val inpMail = fields.newTextField("Mail", {it.contact.mail}, ViewNames.Client.InputMail)
    val inpPhone = fields.newTextField("Telefon", {it.contact.phone}, ViewNames.Client.InputPhone)
    val inpStreet = fields.newTextField("Strasse", {it.contact.street}, ViewNames.Client.InputStreet)
    val inpZipCode = fields.newTextField("PLZ", {it.contact.zipCode}, ViewNames.Client.InputZipCode)
    val inpCity = fields.newTextField("Stadt", {it.contact.city}, ViewNames.Client.InputCity)

    val inpNote = fields.newTextArea("Notiz", {it.note}, ViewNames.Client.InputNote)

    init {
        debugColor = Color.ORANGE

        suggester.enableSuggestionsFor(inpJob, inpCountryOfOrigin, inpOrigin, inpChildren, inpZipCode, inpCity)

        val baseForm = FormPanel()
        with(baseForm) {
            titledBorder("Basisdaten")
            debugColor = Color.CYAN
            addFormInput(inpFirstName)
            addFormInput(inpLastName)
            addFormInput(inpGender)
            addFormInput(inpBirthday)
            addFormInput("Sternzeichen", outStarsign)
            addFormInput(inpCountryOfOrigin)
            addFormInput(inpOrigin)
            addFormInput(inpRelationship)
            addFormInput(inpJob)
            addFormInput(inpChildren)
            addFormInput(inpHobbies)
            addFormInput("Erstellt am", outCreated)
        }

        val contactForm = FormPanel()
        with(contactForm) {
            titledBorder("Kontaktdaten")
            debugColor = Color.BLUE
            addFormInput(inpMail)
            addFormInput(inpPhone)
            addFormInput(inpStreet)
            addFormInput(inpZipCode)
            addFormInput(inpCity)
        }
        // this is nearly the same, as the min width of the baseForm (135 for the textfield column, and some more for the label width)
        contactForm.enforceWidth(200)

        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.5
        c.weighty = 0.0
        add(baseForm)

        c.gridx++
        c.insets = Pad.LEFT
        add(contactForm)

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
        with (c) {
            weightx = 1.0
            weighty = 0.5
            fill = GridBagConstraints.BOTH
            add(appointmentsSubView)

            gridy++
            add(treatmentsSubview)
        }
    }

    override fun isModified(client: Client): Boolean {
        return fields.isAnyModified(client)
    }

    override fun updateFields(client: Client) {
        log.trace("updateFields(client={})", client)
        fields.updateAll(client)
        outStarsign.text = if (client.birthday == null) "" else StarSignCalculator.signFor(client.birthday).label
        outCreated.text = client.created.formatDate()
    }

}
