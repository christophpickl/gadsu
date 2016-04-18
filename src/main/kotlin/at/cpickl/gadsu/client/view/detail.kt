package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.SaveClientEvent
import at.cpickl.gadsu.debugColor
import at.cpickl.gadsu.image.ImagePickerFactory
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.treatment.inclient.TreatmentsInClientView
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.FormPanel
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.ModificationAware
import at.cpickl.gadsu.view.components.ModificationChecker
import at.cpickl.gadsu.view.components.SwingFactory
import at.cpickl.gadsu.view.components.changeSize
import at.cpickl.gadsu.view.components.newPersistableEventButton
import com.google.common.collect.ComparisonChain
import com.google.inject.Inject
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

interface ClientDetailView {
    val imageViewNamePrefix: String get() = ViewNames.Client.ImagePrefix

    fun readClient(): Client
    fun writeClient(client: Client)
    fun isModified(): Boolean
    fun changeImage(newImage: MyImage)
    fun focusFirst()
    fun asComponent(): Component
}

class SwingClientDetailView @Inject constructor(
        swing: SwingFactory,
        treatmentTable: TreatmentsInClientView,
        imagePickerFactory: ImagePickerFactory,
        prefs: Prefs
) : GridPanel(), ClientDetailView, ModificationAware {


    private var originalClient = Client.INSERT_PROTOTYPE

    private val log = LoggerFactory.getLogger(javaClass)

    private val btnSave = swing.newPersistableEventButton(ViewNames.Client.SaveButton, { SaveClientEvent() })
    private val btnCancel = JButton("Abbrechen")

    private val modificationChecker = ModificationChecker(this, btnSave, btnCancel)

    // attention: must come AFTER list of buttons due to hacky design nature ;)
    private val inpFirstName = modificationChecker.enableChangeListener(JTextField())
    private val inpLastName = modificationChecker.enableChangeListener(JTextField())

    private var originalImage = Images.DEFAULT_PROFILE_MAN
    private var imageChanged = false
    private val imageContainer = JLabel(originalImage.toViewBigRepresentation())

    init {
        modificationChecker.disableAll()

        inpFirstName.name = ViewNames.Client.InputFirstName
        inpLastName.name = ViewNames.Client.InputLastName
        imageContainer.name = ViewNames.Client.ImageContainer

        btnCancel.name = ViewNames.Client.CancelButton
        btnCancel.addActionListener {
            log.debug("btnCancel clicked")
            updateFields()
        }
        val newSize = Dimension(btnSave.preferredSize.width + 20, btnSave.preferredSize.height)
        btnSave.changeSize(newSize)
        btnCancel.changeSize(newSize)

        val formPanel = FormPanel()
        formPanel.addFormInput("Vorname", inpFirstName)
        formPanel.addFormInput("Nachname", inpLastName)

        val imagePicker = imagePickerFactory.create(imageViewNamePrefix, prefs.clientPictureDefaultFolder)

        val imagePanel = GridPanel()
        imagePanel.add(imageContainer)
        imagePanel.c.gridy++
        imagePanel.add(imagePicker.asComponent())
        formPanel.addFormInput("Bild", imagePanel)

        val buttonPanel = JPanel()
        buttonPanel.layout = BoxLayout(buttonPanel, BoxLayout.X_AXIS)
        buttonPanel.debugColor = Color.BLUE

        buttonPanel.add(btnSave)
        buttonPanel.add(btnCancel)

        c.fill = GridBagConstraints.HORIZONTAL
        c.weightx = 1.0
        c.weighty = 0.0
        add(formPanel)

        c.gridy++
        c.fill = GridBagConstraints.BOTH
        c.weighty = 1.0
        add(treatmentTable)


        c.gridy++
        c.fill = GridBagConstraints.NONE
        c.anchor = GridBagConstraints.CENTER
        c.weighty = 0.0
        add(buttonPanel)
    }

    override fun readClient(): Client {
        return Client(
                originalClient.id,
                originalClient.created,
                inpFirstName.text,
                inpLastName.text,

                // FIXME these fields got no UI yet!
                originalClient.contact,
                originalClient.birthday,
                originalClient.gender,
                originalClient.countryOfOrigin,
                originalClient.relationship,
                originalClient.job,
                originalClient.children,
                originalClient.note,

                if (originalImage.toViewBigRepresentation() === imageContainer.icon) originalImage
                else (imageContainer.icon as ImageIcon).toMyImage()
        )
    }

    override fun writeClient(client: Client) {
        log.trace("set currentClient(client={})", client)

        imageChanged = false
        originalClient = client
        btnSave.changeLabel(client)
        updateFields()
        modificationChecker.trigger()
    }

    override fun isModified(): Boolean {
        return imageChanged ||
                ComparisonChain.start()
                    .compare(originalClient.firstName, inpFirstName.text)
                    .compare(originalClient.lastName, inpLastName.text)
                    .result() != 0
    }

    override fun focusFirst() {
        log.trace("focusFirst()")
        val requested = inpFirstName.requestFocusInWindow()
        if (!requested) {
            log.warn("Requesting focus failed for: {}", inpFirstName)
        }
    }

    override fun changeImage(newImage: MyImage) {
        log.debug("changeImage(newImage)")
        imageChanged = true
        originalImage = newImage
        imageContainer.icon = originalImage.toViewBigRepresentation()

        modificationChecker.trigger()
    }

    override fun asComponent() = this

    private fun updateFields() {
        log.debug("updateFields()")

        inpFirstName.text = originalClient.firstName
        inpLastName.text = originalClient.lastName
        imageChanged = false
        imageContainer.icon = originalClient.picture.toViewBigRepresentation()

        modificationChecker.trigger()
    }

}
