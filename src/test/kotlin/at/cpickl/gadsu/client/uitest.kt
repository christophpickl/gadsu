package at.cpickl.gadsu.client

import at.cpickl.gadsu.testinfra.UiTest


class ClientDriver(private val test: UiTest) {
    // val btnNewClient: Button get() = window.getButton(ViewNames.Client.BUTTON_NEW)

    val list = test.mainWindow.getListBox(at.cpickl.gadsu.view.ViewNames.Client.List)

}

class ClientUiTest : UiTest() {

    fun testFoo() {
        println("clientDriver().list.size=" + clientDriver().list.size)

    }
}
