package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.Labeled
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.MyComboBox
import com.google.common.collect.ComparisonChain
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField


interface ElField {
    val formLabel: String
    fun asComponent(): Component

}

class ElTextField(
        private val viewName: String,
        override val formLabel: String
) : JTextField(), ElField {

    init {
        name = viewName
    }

    override fun asComponent() = this
}

class ElTextArea(
        private val viewName: String,
        override val formLabel: String
) : JTextArea(), ElField {

    init {
        name = viewName
    }

    override fun asComponent() = this
}

class ElComboBox<T : Labeled>(private val delegate: MyComboBox<T>, override val formLabel: String) : ElField {

    override fun asComponent() = delegate
    var selectedItemTyped: T
        get() = delegate.selectedItemTyped
        set(value) {
            delegate.selectedItemTyped = value
        }
}

class Fields(private val modifications: ModificationChecker) {
    fun newTextField(viewName: String, label: String): ElTextField {
        val field = ElTextField(viewName, label)
        modifications.enableChangeListener(field)
        return field
    }

    fun newTextArea(viewName: String, label: String): ElTextArea {
        val field = ElTextArea(viewName, label)
        modifications.enableChangeListener(field)
        return field
    }

    fun <T: Labeled> newComboBox(values: Array<T>, initValue: T, viewName: String, label: String): ElComboBox<T> {
        val realField = MyComboBox<T>(values, initValue)
        val field = ElComboBox<T>(realField, label)
        realField.name = viewName
        modifications.enableChangeListener(realField)
        return field
    }

//    fun newDateField(initialDate: DateTime, viewName: String, label: String): ElDateField {
//
//    }
}

// FIXME implement me
//class ElDateField(private val viewName: String, override val formLabel: String) : ElField {
//    private val delegate = SwingFactory.
//    override fun asComponent(): Component {
//
//    }
//
//}

fun FormPanel.addFormInput(field: ElField) {
    addFormInput(field.formLabel, field.asComponent())
}

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        treatmentSubview: TreatmentsInClientView
//        private val imagePicker: ImagePicker
) : DefaultClientTab(Labels.Tabs.ClientMain) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val fields = Fields(modificationChecker)



    val inpFirstName = fields.newTextField(ViewNames.Client.InputFirstName, "Vorname")
    val inpLastName = fields.newTextField(ViewNames.Client.InputLastName, "Nachname")
    val inpGender = fields.newComboBox(Gender.values(), initialClient.gender, ViewNames.Client.InputGender, "Geschlecht")
//    var originalImage = Images.DEFAULT_PROFILE_MAN
//    val imageContainer = JLabel(originalImage.toViewBigRepresentation())
    //        imageContainer.name = ViewNames.Client.ImageContainer
    val inpBirthday = JTextField() // FIXME birthday responsible
    val inpCountryOfOrigin = fields.newTextField(ViewNames.Client.InputCountryOfOrigin, "Herkunftsland")
    val inpRelationship = fields.newComboBox(Relationship.values(), initialClient.relationship, ViewNames.Client.InputRelationship, "Beziehungsstatus")
    val inpJob = fields.newTextField(ViewNames.Client.InputJob, "Beruf")
    val inpChildren = fields.newTextField(ViewNames.Client.InputChildren, "Kinder")
    val inpMail = fields.newTextField(ViewNames.Client.InputMail, "Mail")
    val inpPhone = fields.newTextField(ViewNames.Client.InputPhone, "Telefon")
    val inpStreet = fields.newTextField(ViewNames.Client.InputStreet, "Strasse")
    val inpZipCode = fields.newTextField(ViewNames.Client.InputZipCode, "PLZ")
    val inpCity = fields.newTextField(ViewNames.Client.InputCity, "Stadt")
    val inpNote = fields.newTextArea(ViewNames.Client.InputNote, "Notiz")
    val outCreated = JLabel("")


//    var imageChanged = false

    init {
        debugColor = Color.ORANGE

        val form1Panel = FormPanel()
        form1Panel.debugColor = Color.CYAN
//        form1Panel.addFormInput("", createImagePanel())
        form1Panel.addFormInput(inpFirstName)
        form1Panel.addFormInput(inpLastName)

        form1Panel.addFormInput(inpGender)
        form1Panel.addFormInput("Geburtstag", inpBirthday)
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

        form2Panel.addFormInput("Notiz", inpNote) // FIXME bigger note
        form2Panel.addLastColumnsFilled()

        addColumned(
                Pair<Double, JComponent>(0.5, form1Panel),
                Pair<Double, JComponent>(0.5, form2Panel),
                Pair<Double, JComponent>(0.0, treatmentSubview)
        )

    }

//    val clientPicture: MyImage get() {
//        if (originalImage.toViewBigRepresentation() === imageContainer.icon) {
//            return originalImage
//        }
//        return (imageContainer.icon as ImageIcon).toMyImage()
//    }

    override fun isModified(client: Client): Boolean {
//        return imageChanged ||
        return ComparisonChain.start()
                .compare(client.firstName, inpFirstName.text)
                .compare(client.lastName, inpLastName.text)
                //                .compare(client.birthday, inp) FIXME two fields compare
                .compare(client.countryOfOrigin, inpCountryOfOrigin.text)
                .compare(client.relationship, inpRelationship.selectedItemTyped)
                .compare(client.job, inpJob.text)
                .compare(client.children, inpChildren.text)
                .compare(client.contact.mail, inpMail.text)
                .compare(client.contact.phone, inpPhone.text)
                .compare(client.contact.street, inpStreet.text)
                .compare(client.contact.zipCode, inpZipCode.text)
                .compare(client.contact.city, inpCity.text)
                .compare(client.note, inpNote.text)
                .compare(client.gender, inpGender.selectedItemTyped)
                .result() != 0
    }

    override fun updateFields(client: Client) {
        log.trace("updateFields(client={})", client)
        inpFirstName.text = client.firstName
        inpLastName.text = client.lastName
        //        inpBirthday FIXME set value
        inpCountryOfOrigin.text = client.countryOfOrigin
        inpRelationship.selectedItemTyped = client.relationship
        inpJob.text = client.job
        inpChildren.text = client.children
        inpMail.text = client.contact.mail
        inpPhone.text = client.contact.phone
        inpStreet.text = client.contact.street
        inpZipCode.text = client.contact.zipCode
        inpCity.text = client.contact.city
        inpNote.text = client.note
        outCreated.text = client.created.formatDate()
        inpGender.selectedItemTyped = client.gender

//        imageChanged = false
//        imageContainer.icon = client.picture.toViewBigRepresentation()
    }

//    private fun createImagePanel(): GridPanel {
//        val imagePanel = GridPanel()
//        imagePanel.add(imageContainer)
//        imagePanel.c.gridy++
//        imagePanel.add(imagePicker.asComponent())
//        return imagePanel
//    }

}
