package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.addFormInput
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.ModificationChecker
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JLabel



// FIXME implement me
//class ElDateField(private val viewName: String, override val formLabel: String) : ElField {
//    private val delegate = SwingFactory.
//    override fun asComponent(): Component {
//
//    }
//
//}

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        treatmentSubview: TreatmentsInClientView
) : DefaultClientTab(Labels.Tabs.ClientMain) {

    private val log = LoggerFactory.getLogger(javaClass)

    private val fields = Fields<Client>(modificationChecker)

    val inpFirstName = fields.newTextField("Vorname", {it.firstName}, ViewNames.Client.InputFirstName)
    val inpLastName = fields.newTextField("Nachname", {it.lastName}, ViewNames.Client.InputLastName)
    val inpGender = fields.newComboBox(Gender.values(), initialClient.gender, "Geschlecht", {it.gender}, ViewNames.Client.InputGender)
    val inpBirthday = fields.newDatePicker(initialClient.birthday, "Geburtstag", {it.birthday},
            ViewNames.Client.InputBirthdayButton, ViewNames.Client.InputBirthdayPanel, ViewNames.Client.InputBirthdayText)
    val inpCountryOfOrigin = fields.newTextField("Herkunftsland", {it.countryOfOrigin}, ViewNames.Client.InputCountryOfOrigin)
    val inpRelationship = fields.newComboBox(Relationship.values(), initialClient.relationship, "Beziehungsstatus", {it.relationship}, ViewNames.Client.InputRelationship)
    val inpJob = fields.newTextField("Beruf", {it.job}, ViewNames.Client.InputJob)
    val inpChildren = fields.newTextField("Kinder", {it.children}, ViewNames.Client.InputChildren)
    val inpMail = fields.newTextField("Mail", {it.contact.mail}, ViewNames.Client.InputMail)
    val inpPhone = fields.newTextField("Telefon", {it.contact.phone}, ViewNames.Client.InputPhone)
    val inpStreet = fields.newTextField("Strasse", {it.contact.street}, ViewNames.Client.InputStreet)
    val inpZipCode = fields.newTextField("PLZ", {it.contact.zipCode}, ViewNames.Client.InputZipCode)
    val inpCity = fields.newTextField("Stadt", {it.contact.city}, ViewNames.Client.InputCity)
    val inpNote = fields.newTextArea("Notiz", {it.note}, ViewNames.Client.InputNote)
    // FIXME bigger note
    val outCreated = JLabel("")

    init {
        debugColor = Color.ORANGE

        val form1Panel = FormPanel()
        form1Panel.debugColor = Color.CYAN
        form1Panel.addFormInput(inpFirstName)
        form1Panel.addFormInput(inpLastName)

        form1Panel.addFormInput(inpGender)
        form1Panel.addFormInput(inpBirthday)
        form1Panel.addFormInput(inpCountryOfOrigin)
        form1Panel.addFormInput(inpRelationship)
        form1Panel.addFormInput(inpJob)
        form1Panel.addFormInput(inpChildren)
        form1Panel.addFormInput("Erstellt am", outCreated)
        form1Panel.addLastColumnsFilled()


        val form2Panel = FormPanel()
        form2Panel.addFormInput(inpMail)
        form2Panel.addFormInput(inpPhone)
        form2Panel.addFormInput(inpStreet)
        form2Panel.addFormInput(inpZipCode)
        form2Panel.addFormInput(inpCity)

        form2Panel.addFormInput(inpNote)
        form2Panel.addLastColumnsFilled()

        addColumned(
                Pair<Double, JComponent>(0.5, form1Panel),
                Pair<Double, JComponent>(0.5, form2Panel),
                Pair<Double, JComponent>(0.0, treatmentSubview)
        )

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
