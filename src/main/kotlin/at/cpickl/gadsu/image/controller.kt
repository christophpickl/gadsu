package at.cpickl.gadsu.image

import at.cpickl.gadsu.client.ClientService
import at.cpickl.gadsu.client.CurrentClient
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.service.Logged
import at.cpickl.gadsu.view.MainFrame
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DialogType
import at.cpickl.gadsu.view.components.Dialogs
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Logged
open class ClientImageController @Inject constructor(
        private val bus: EventBus,
        private val frame: MainFrame,
        private val prefs: Prefs,
        private val currentClient: CurrentClient,
        private val clientService: ClientService,
        private val dialogs: Dialogs
) {
    companion object {
        private val CLIENT_VIEWNAME_PREFIX = ViewNames.Client.ImagePrefix
    }
    private val log = LoggerFactory.getLogger(javaClass)



    @Subscribe open fun onSelectImageEvent(event: SelectImageEvent) {
        val chooser = JFileChooser()
        chooser.currentDirectory = prefs.clientPictureDefaultFolder
        chooser.dialogTitle = "Bild ausw\u00e4hlen ..."
        chooser.fileFilter = FileNameExtensionFilter("Bilddateien (*.jpg)", "jpg")

        val result = chooser.showOpenDialog(frame.asJFrame())
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = chooser.selectedFile

            if (!selectedFile.name.toLowerCase().endsWith("jpg")) {
                log.debug("Ignoring invalid file (this patches the JFileChooser default behaviour of allowing *.* files although extension filter is set)")
                return
            }

            bus.post(ImageSelectedEvent(CLIENT_VIEWNAME_PREFIX, selectedFile))

        } else {
            log.debug("Choosing an image aborted by user.")
        }
    }

    @Subscribe open fun onImageSelectedEvent(event: ImageSelectedEvent) {
        if (!event.viewNamePrefix.equals(CLIENT_VIEWNAME_PREFIX)) {
            log.debug("Aborting image selection, as was not dispatched for client view.")
            return
        }
        prefs.clientPictureDefaultFolder = event.imageFile.parentFile ?: event.imageFile

        val file = event.imageFile
        val icon = file.readImageIcon() // MINOR catch exception if loading failed and show error dialog
        val size = icon.size()
        if (size.width != size.height) {
            dialogs.show(
                    title = "Ung\u00fcltige Datei",
                    message = "Das ausgew\u00e4hlte Bild muss gleiche Seitenverh\u00e4ltnisse haben. Die Bildgr\u00f6\u00dfe betr\u00e4gt: ${size.width}x${size.height}",
                    buttonLabels = arrayOf("Okay"),
                    type = DialogType.WARN
            )
            return
        }

        val newPicture = file.toMyImage()
        val newClient = currentClient.data.copy(picture = newPicture)
        clientService.savePicture(newClient)

        currentClient.data = newClient
    }

}
