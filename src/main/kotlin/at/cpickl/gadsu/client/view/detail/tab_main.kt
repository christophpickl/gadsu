package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.development.debugColor
import at.cpickl.gadsu.service.formatDate
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.Labels
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.MyComboBox
import com.google.common.collect.ComparisonChain
import org.slf4j.LoggerFactory
import java.awt.Color
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField


class ClientPropertyTextField(
        private val viewName: String,
        val formLabel: String
        ) : JTextField() {

    init {
        name = viewName
    }
}

class Fields(private val modifications: ModificationChecker) {
    fun newTextField(viewName: String, label: String): ClientPropertyTextField {
        val field = ClientPropertyTextField(viewName, label)
        modifications.enableChangeListener(field)
        return field
    }
}

fun FormPanel.addFormInput(field: ClientPropertyTextField) {
    addFormInput(field.formLabel, field)
}

class ClientTabMain(
        initialClient: Client,
        modificationChecker: ModificationChecker,
        treatmentSubview: TreatmentsInClientView
//        private val imagePicker: ImagePicker
) : DefaultClientTab(Labels.Tabs.ClientMain) {

    private val log = LoggerFactory.getLogger(javaClass)

    val inpFirstName = modificationChecker.enableChangeListener(JTextField())

    val inpLastName = modificationChecker.enableChangeListener(JTextField())

    val inpGender = modificationChecker.enableChangeListener(MyComboBox<Gender>(Gender.values(), initialClient.gender))
//    var originalImage = Images.DEFAULT_PROFILE_MAN
//    val imageContainer = JLabel(originalImage.toViewBigRepresentation())
    //        imageContainer.name = ViewNames.Client.ImageContainer

    val inpBirthday = JTextField()
    val inpCountryOfOrigin = JTextField()
    val inpRelationship = JTextField()
    // FIXME use this component!
    val inpJob: ClientPropertyTextField
    val inpChildren = JTextField()
    val inpMail = JTextField()
    val inpPhone = JTextField()
    val inpStreet = JTextField()
    val inpZipCode = JTextField()
    val inpCity = JTextField()
    val inpNote = JTextArea()
    val outCreated = JLabel("")

    private val fields = Fields(modificationChecker)

//    var imageChanged = false

    init {
        inpJob = fields.newTextField(ViewNames.Client.InputJob, "Beruf")

        debugColor = Color.ORANGE
        inpFirstName.name = ViewNames.Client.InputFirstName
        inpLastName.name = ViewNames.Client.InputLastName


        val form1Panel = FormPanel()
        form1Panel.debugColor = Color.CYAN
//        form1Panel.addFormInput("", createImagePanel())
        form1Panel.addFormInput("Vorname", inpFirstName)
        form1Panel.addFormInput("Nachname", inpLastName)
        form1Panel.addFormInput("Geschlecht", inpGender)
        form1Panel.addFormInput("Geburtstag", inpBirthday)
        form1Panel.addFormInput("Herkunftsland", inpCountryOfOrigin)
        form1Panel.addFormInput("Beziehungsstatus", inpRelationship)
        form1Panel.addFormInput(inpJob)

        form1Panel.addFormInput("Kinder", inpChildren)
        form1Panel.addFormInput("Erstellt am", outCreated)
        form1Panel.addLastColumnsFilled()


        val form2Panel = FormPanel()
        form2Panel.addFormInput("Mail", inpMail)
        form2Panel.addFormInput("Telefon", inpPhone)
        form2Panel.addFormInput("Strasse", inpStreet)
        form2Panel.addFormInput("PLZ", inpZipCode)
        form2Panel.addFormInput("Stadt", inpCity)
        form2Panel.addFormInput("Notiz", inpNote)
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
                //                .compare(client.birthday, inp) FIXME two fields
                .compare(client.countryOfOrigin, inpCountryOfOrigin.text)
                //                .compare(client.relationship, inp)
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
        //        inpBirthday FIXME
        inpCountryOfOrigin.text = client.countryOfOrigin
        //        inpRelationship.selected= client.relationship
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
