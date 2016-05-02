package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.Pad
import at.cpickl.gadsu.view.language.Labels
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.GridBagConstraints
import javax.swing.JLabel


class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        treatmentSubview: TreatmentsInClientView
) : DefaultClientTab(Labels.Tabs.ClientMain) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    val inpFirstName = fields.newTextField("Vorname", {it.firstName}, ViewNames.Client.InputFirstName)
    val inpLastName = fields.newTextField("Nachname", {it.lastName}, ViewNames.Client.InputLastName)
    val inpGender = fields.newComboBox(Gender.orderedValues(), initialClient.gender, "Geschlecht", {it.gender}, ViewNames.Client.InputGender)
    val inpBirthday = fields.newDatePicker(initialClient.birthday, "Geburtstag", {it.birthday}, ViewNames.Client.InputBirthdayPrefix)
    val inpCountryOfOrigin = fields.newTextField("Herkunftsland", {it.countryOfOrigin}, ViewNames.Client.InputCountryOfOrigin)
    val inpRelationship = fields.newComboBox(Relationship.orderedValues(), initialClient.relationship, "Beziehungsstatus", {it.relationship}, ViewNames.Client.InputRelationship)
    val inpJob = fields.newTextField("Beruf", {it.job}, ViewNames.Client.InputJob)
    val inpChildren = fields.newTextField("Kinder", {it.children}, ViewNames.Client.InputChildren)
    val inpMail = fields.newTextField("Mail", {it.contact.mail}, ViewNames.Client.InputMail)
    val inpPhone = fields.newTextField("Telefon", {it.contact.phone}, ViewNames.Client.InputPhone)
    val inpStreet = fields.newTextField("Strasse", {it.contact.street}, ViewNames.Client.InputStreet)
    val inpZipCode = fields.newTextField("PLZ", {it.contact.zipCode}, ViewNames.Client.InputZipCode)
    val inpCity = fields.newTextField("Stadt", {it.contact.city}, ViewNames.Client.InputCity)
    val inpNote = fields.newTextArea("Notiz", {it.note}, ViewNames.Client.InputNote)
    val outCreated = JLabel("")

    init {
        debugColor = Color.ORANGE

        val form1Panel = FormPanel()
        with(form1Panel) {
            debugColor = Color.CYAN
            addFormInput(inpFirstName)
            addFormInput(inpLastName)
            addFormInput(inpGender)
            addFormInput(inpBirthday)
            addFormInput(inpCountryOfOrigin)
            addFormInput(inpRelationship)
            addFormInput(inpJob)
            addFormInput(inpChildren)
            addFormInput("Erstellt am", outCreated)
        }

        val form2Panel = FormPanel()
        with(form2Panel) {
            addFormInput(inpMail)
            addFormInput(inpPhone)
            addFormInput(inpStreet)
            addFormInput(inpZipCode)
            addFormInput(inpCity)
        }

        c.fill = GridBagConstraints.HORIZONTAL
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 0.5
        c.weighty = 0.0
        add(form1Panel)

        c.insets = Pad.LEFT
        c.gridx++
        add(form2Panel)

        c.gridx++
        c.fill = GridBagConstraints.BOTH
        c.weightx = 0.0
        c.weighty = 0.0
        add(treatmentSubview)

        c.insets = Pad.TOP
        c.gridx = 0
        c.gridy++
        c.gridwidth = 3
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 1.0
        add(inpNote.toComponent())
    }

    override fun isModified(client: Client): Boolean {
        return fields.isAnyModified(client)
    }

    override fun updateFields(client: Client) {
        log.trace("updateFields(client={})", client)
        fields.updateAll(client)
        outCreated.text = client.created.formatDate()
    }

}
