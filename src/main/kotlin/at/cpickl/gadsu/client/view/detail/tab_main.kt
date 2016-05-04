package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.treatment.inclient.TreatmentList
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.language.Labels
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.titledBorder
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.JTextField

fun main(args: Array<String>) {
    Framed.showWithContext({ context ->
        ClientTabMain(
            Client.INSERT_PROTOTYPE,
            ModificationChecker(object : ModificationAware {
                override fun isModified() = true
            }),
            TreatmentsInClientView(context.swing, TreatmentList(context.bus), context.bus))
    }, Dimension(800, 600))
}

class DisabledTextField(initialValue: String = ""): JTextField(initialValue) {
    init {
        isEnabled = false
    }
}

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        treatmentSubview: TreatmentsInClientView
) : DefaultClientTab(Labels.Tabs.ClientMain) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    // base
    val inpFirstName = fields.newTextField("Vorname", {it.firstName}, ViewNames.Client.InputFirstName)
    val inpLastName = fields.newTextField("Nachname", {it.lastName}, ViewNames.Client.InputLastName)
    val inpGender = fields.newComboBox(Gender.orderedValues(), initialClient.gender, "Geschlecht", {it.gender}, ViewNames.Client.InputGender)
    val inpBirthday = fields.newDatePicker(initialClient.birthday, "Geburtstag", {it.birthday}, ViewNames.Client.InputBirthdayPrefix)
    val inpCountryOfOrigin = fields.newTextField("Herkunftsland", {it.countryOfOrigin}, ViewNames.Client.InputCountryOfOrigin)
    val inpRelationship = fields.newComboBox(Relationship.orderedValues(), initialClient.relationship, "Beziehungsstatus", {it.relationship}, ViewNames.Client.InputRelationship)
    val inpJob = fields.newTextField("Beruf", {it.job}, ViewNames.Client.InputJob)
    val inpChildren = fields.newTextField("Kinder", {it.children}, ViewNames.Client.InputChildren)
    val outCreated = DisabledTextField()

    // contact
    val inpMail = fields.newTextField("Mail", {it.contact.mail}, ViewNames.Client.InputMail)
    val inpPhone = fields.newTextField("Telefon", {it.contact.phone}, ViewNames.Client.InputPhone)
    val inpStreet = fields.newTextField("Strasse", {it.contact.street}, ViewNames.Client.InputStreet)
    val inpZipCode = fields.newTextField("PLZ", {it.contact.zipCode}, ViewNames.Client.InputZipCode)
    val inpCity = fields.newTextField("Stadt", {it.contact.city}, ViewNames.Client.InputCity)

    // note label will actually not be used
    val inpNote = fields.newTextArea("NOT USED", {it.note}, ViewNames.Client.InputNote)


    init {
        debugColor = Color.ORANGE

        val baseForm = FormPanel()
        with(baseForm) {
            titledBorder("Basisdaten")
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
        contactForm.enforceWidth(235)

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
        add(treatmentSubview)

        c.gridx = 0
        c.gridy++
        c.gridwidth = 3
        c.insets = Pad.TOP
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
