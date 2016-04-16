package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.CancelButton
import at.cpickl.gadsu.client.view.Client
import at.cpickl.gadsu.client.view.CreateButton
import at.cpickl.gadsu.client.view.InputFirstName
import at.cpickl.gadsu.client.view.InputLastName
import at.cpickl.gadsu.client.view.List
import at.cpickl.gadsu.client.view.SaveButton
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.clickAndDisposeDialog
import at.cpickl.gadsu.view.ViewNames
import org.junit.BeforeClass
import org.slf4j.LoggerFactory
import org.uispec4j.Window
import org.uispec4j.interception.PopupMenuInterceptor
import javax.swing.JLabel

class ClientDriver(private val test: UiTest, private val window: Window) {

    private val log = LoggerFactory.getLogger(javaClass)

    // MINOR or: val list: ListBox get() = test.mainWindow.getListBox(ViewNames.Client.List) ?
    val list = window.getListBox(ViewNames.Client.List)
    val createButton = window.getButton(ViewNames.Client.CreateButton)

    val inputFirstName = window.getInputTextBox(ViewNames.Client.InputFirstName)
    val inputLastName = window.getInputTextBox(ViewNames.Client.InputLastName)
    val saveButton = window.getButton(ViewNames.Client.SaveButton)
    val cancelButton = window.getButton(ViewNames.Client.CancelButton)

    fun saveClient(client: Client) {
        createButton.click()

        inputFirstName.setText(client.firstName, false)
        inputLastName.setText(client.lastName, false)

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

}
