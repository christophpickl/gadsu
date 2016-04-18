package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.ImageSelectedEvent
import at.cpickl.gadsu.image.toMyImage
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.PROFILE_PICTURE_CLASSPATH_1
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.clickAndDisposeDialog
import at.cpickl.gadsu.view.ViewNames
import org.slf4j.LoggerFactory
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
import javax.swing.ImageIcon
import javax.swing.JLabel

class ClientDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    private val log = LoggerFactory.getLogger(javaClass)

    val list = window.getListBox(ViewNames.Client.List)
    val createButton = window.getButton(ViewNames.Client.CreateButton)

    val inputFirstName = window.getInputTextBox(ViewNames.Client.InputFirstName)
    val inputLastName = window.getInputTextBox(ViewNames.Client.InputLastName)
    val saveButton = window.getButton(ViewNames.Client.SaveButton)
    val cancelButton = window.getButton(ViewNames.Client.CancelButton)

    private val imageContainer = window.getTextBox(ViewNames.Client.ImageContainer)

    fun saveClient(client: Client) {
        createButton.click()

        inputFirstName.text = client.firstName
        inputLastName.text = client.lastName

        saveButton.click()
    }

    fun deleteClient(client: Client) {
        log.debug("deleteClient(client={})", client)
        val popup = PopupMenuInterceptor.run(list.triggerRightClick(listIndexOf(client.fullName)))
        val popupMenuItemDelete = popup.getSubMenu("L\u00F6schen")
        popupMenuItemDelete.clickAndDisposeDialog("L\u00F6schen")
    }

    fun listIndexOf(findLabel: String): Int {
        for (i in 0.rangeTo(list.size - 1)) {
            val label = (list.getSwingRendererComponentAt(i) as JLabel).text
            if (findLabel.equals(label)) {
                return i
            }
        }
        throw AssertionError("Not found list entry with label: '$findLabel'!")
    }

    fun assertListContains(client: Client) {
        test.assertThat(list.contains(client.fullName))
    }

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Client.MainPanel)
    }

    fun changeImage(fileClassPath: String = PROFILE_PICTURE_CLASSPATH_1) {
        log.debug("changeImage(fileClassPath='{}')", fileClassPath)

        postEvent(ImageSelectedEvent(ViewNames.Client.ImagePrefix, fileClassPath.toMyImage()))
    }


    fun readImage(): ImageIcon? {
        val label = imageContainer.awtComponent as JLabel
        return label.icon as ImageIcon?
    }

}
