package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.ImageSelectedEvent
import at.cpickl.gadsu.testinfra.PROFILE_PICTURE_CLASSPATH_1
import at.cpickl.gadsu.testinfra.ui.BaseDriver
import at.cpickl.gadsu.testinfra.ui.DateSpecPicker
import at.cpickl.gadsu.testinfra.ui.UiTest
import at.cpickl.gadsu.testinfra.ui.deleteAtRow
import at.cpickl.gadsu.view.ViewNames
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
    val inputJob = window.getInputTextBox(ViewNames.Client.InputJob)

    val inputMail = window.getInputTextBox(ViewNames.Client.InputMail)
    val inputPhone = window.getInputTextBox(ViewNames.Client.InputPhone)
    val inputStreet = window.getInputTextBox(ViewNames.Client.InputStreet)
    val inputZipCode = window.getInputTextBox(ViewNames.Client.InputZipCode)
    val inputCity = window.getInputTextBox(ViewNames.Client.InputCity)
    val inputChildren = window.getInputTextBox(ViewNames.Client.InputChildren)
    val inputCountryOfOrigin = window.getInputTextBox(ViewNames.Client.InputCountryOfOrigin)
    val inputNote = window.getInputTextBox(ViewNames.Client.InputNote)
    val inputRelationship = window.getComboBox(ViewNames.Client.InputRelationship)
    val inputGender = window.getComboBox(ViewNames.Client.InputGender)

    val saveButton = window.getButton(ViewNames.Client.SaveButton)
    val cancelButton = window.getButton(ViewNames.Client.CancelButton)

    val inputBirthdate: DateSpecPicker get() = DateSpecPicker(test, window, ViewNames.Client.InputBirthdayPrefix)

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

    fun saveBasicClient(client: Client) {
        createButton.click()
        fillNames(client)
        saveButton.click()
    }

    fun saveFullClient(client: Client) {
        createButton.click()
        fillFull(client)
        saveButton.click()
    }

    fun fillNames(client: Client) {
        inputFirstName.text = client.firstName
        inputLastName.text = client.lastName
    }

    fun fillFull(client: Client) {
        fillNames(client)
        inputJob.text = client.job
        inputMail.text = client.contact.mail
        inputPhone.text = client.contact.phone
        inputStreet.text = client.contact.street
        inputZipCode.text = client.contact.zipCode
        inputCity.text = client.contact.city
        inputChildren.text = client.children
        inputCountryOfOrigin.text = client.countryOfOrigin
        inputNote.text = client.note
        inputRelationship.select(client.relationship.label)
        inputGender.select(client.gender.label)
        inputBirthdate.changeDate(client.birthday)
        // picture is not setable in client view (yet)
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


    fun assertViewContains(client: Client) {
        test.assertThat(inputFirstName.textEquals(client.firstName))
        test.assertThat(inputLastName.textEquals(client.lastName))
        test.assertThat(inputJob.textEquals(client.job))
        test.assertThat(inputMail.textEquals(client.contact.mail))
        test.assertThat(inputPhone.textEquals(client.contact.phone))
        test.assertThat(inputStreet.textEquals(client.contact.street))
        test.assertThat(inputZipCode.textEquals(client.contact.zipCode))
        test.assertThat(inputCity.textEquals(client.contact.city))
        test.assertThat(inputChildren.textEquals(client.children))
        test.assertThat(inputCountryOfOrigin.textEquals(client.countryOfOrigin))
        test.assertThat(inputNote.textEquals(client.note))
        test.assertThat(inputRelationship.selectionEquals(client.relationship.label))
        test.assertThat(inputGender.selectionEquals(client.gender.label))
        inputBirthdate.assertMaybeSelected(client.birthday)
    }

}
