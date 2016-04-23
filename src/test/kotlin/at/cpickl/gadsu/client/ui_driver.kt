package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.ImageSelectedEvent
import at.cpickl.gadsu.testinfra.BaseDriver
import at.cpickl.gadsu.testinfra.PROFILE_PICTURE_CLASSPATH_1
import at.cpickl.gadsu.testinfra.UiTest
import at.cpickl.gadsu.testinfra.deleteAtRow
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DatePickerDriver
import at.cpickl.gadsu.view.components.MyListModel
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.slf4j.LoggerFactory
import org.uispec4j.Window
import java.io.File


class ClientDriver(test: UiTest, window: Window) : BaseDriver(test, window) {

    private val log = LoggerFactory.getLogger(javaClass)

    val list = window.getListBox(ViewNames.Client.List)

    @Suppress("UNCHECKED_CAST")
    val listModel: MyListModel<Client> get() {
        if (list.awtComponent.model !is MyListModel<*>) {
            throw AssertionError("Expected the client list to be of type MyListModel but was: '${list.awtComponent.model.javaClass.name}'!")
        }
        return list.awtComponent.model as MyListModel<Client>
    }

    val createButton = window.getButton(ViewNames.Client.CreateButton)

    val inputFirstName = window.getInputTextBox(ViewNames.Client.InputFirstName)
    val inputLastName = window.getInputTextBox(ViewNames.Client.InputLastName)
    val inputJob= window.getInputTextBox(ViewNames.Client.InputJob)
    val saveButton = window.getButton(ViewNames.Client.SaveButton)
    val cancelButton = window.getButton(ViewNames.Client.CancelButton)

    val inputBirthdate: DatePickerDriver = DatePickerDriver(test, window,
            ViewNames.Client.InputBirthdayButton, ViewNames.Client.InputBirthdayPanel, ViewNames.Client.InputBirthdayText)

//    private val imageContainer = window.getTextBox(ViewNames.Client.ImageContainer)



    // --------------------------------------------------------------------------- list

    //<editor-fold desc="list">

    fun listIndexOf(client: Client): Int {
        val index = listIndexOfBasedOnFullname(client)
        log.trace("listIndexOf(client.fullName={}) => index: {}", client.fullName, index)
        if (index == -1) {
            throw AssertionError("Not found client in list: '$client'!")
        }
        return index
    }

    fun selectList(client: Client) {
        log.trace("selectList(client.fullName={})", client.fullName)
        list.selectIndex(listIndexOf(client))
    }

    fun assertListContains(client: Client) {
        MatcherAssert.assertThat("Expected list to contain client '${client.fullName}'!", listIndexOfBasedOnFullname(client), not(equalTo(-1)))
    }

    fun assertListNotContains(client: Client) {
        MatcherAssert.assertThat(listIndexOfBasedOnFullname(client), equalTo(-1))
    }

    fun assertListSelected(client: Client) {
        MatcherAssert.assertThat(list.awtComponent.selectedIndex, equalTo(listIndexOf(client)))
    }

    fun assertListSelectionEmpty() {
        test.assertThat(list.selectionIsEmpty())
    }

    //</editor-fold>

    // --------------------------------------------------------------------------- misc

    fun saveNewClient(client: Client) {
        createButton.click()

        fillForm(client)

        saveButton.click()
    }

    fun fillForm(client: Client) {
        inputFirstName.text = client.firstName
        inputLastName.text = client.lastName
    }

    fun deleteClient(client: Client) {
        log.debug("deleteClient(client={})", client)

        list.deleteAtRow(listIndexOf(client))
    }

    fun assertPanelVisible() {
        test.assertPanelContainedInMainWindow(ViewNames.Client.MainPanel)
    }

    fun changeImage(fileClassPath: String = PROFILE_PICTURE_CLASSPATH_1) {
        log.debug("changeImage(fileClassPath='{}')", fileClassPath)


        val file = File(javaClass.getResource(fileClassPath).file)
        log.trace("Trying to set classpath image via file reference: '{}'", file.absolutePath)
        postEvent(ImageSelectedEvent(ViewNames.Client.ImagePrefix, file))
    }


//    fun readImage(): ImageIcon? {
//        val label = imageContainer.awtComponent as JLabel
//        return label.icon as ImageIcon?
//    }

    private fun listIndexOfBasedOnFullname(client: Client): Int {
        log.trace("listIndexOfBasedOnFullname(client.fullName='{}')", client.fullName)

        for (i in 0.rangeTo(listModel.size - 1)) {
            log.trace("Checking list model client at index {}: {}", i, listModel.get(i).fullName)
            if (listModel.get(i).fullName.equals(client.fullName)) {
                return i
            }
        }
        return -1
    }

    fun assertNoChangesDetected() {
        test.assertThat(test.not(saveButton.isEnabled))
        test.assertThat(test.not(cancelButton.isEnabled))
    }

    fun assertChangesDetected() {
        test.assertThat("Expected client save button to be enabled!", saveButton.isEnabled)
        test.assertThat(cancelButton.isEnabled)
    }

}
