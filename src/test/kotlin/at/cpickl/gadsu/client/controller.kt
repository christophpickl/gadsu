package at.cpickl.gadsu.client

import at.cpickl.gadsu.testinfra.savedValidInstance
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.logic.calculateInsertIndex
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test class ClientViewControllerTest {

    private val prototype = Client.savedValidInstance().copy(lastName = "BBB")
    private var model = MyListModel<Client>()

    @BeforeMethod
    fun resetState() {
        model = MyListModel<Client>()
    }

    fun calculateIndex_emptyModel_returns0() {
        assertThat(calculateIndex(prototype), equalTo(0))
    }

    fun calculateIndex_oneBeingLess_returns1() {
        model.addElement(prototype.copy(lastName = "AAA"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    fun calculateIndex_oneBeingBigger_returns0() {
        model.addElement(prototype.copy(lastName = "CCC"))
        assertThat(calculateIndex(prototype), equalTo(0))
    }

    fun calculateIndex_inBetween_returns1() {
        model.addElement(prototype.copy(lastName = "AAA"))
        model.addElement(prototype.copy(lastName = "CCC"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    fun calculateIndex_twoLessOneBigger_returns2() {
        model.addElement(prototype.copy(lastName = "AAA1"))
        model.addElement(prototype.copy(lastName = "AAA2"))
        model.addElement(prototype.copy(lastName = "CCC"))
        assertThat(calculateIndex(prototype), equalTo(2))
    }

    fun calculateIndex_oneLessTwoBigger_returns1() {
        model.addElement(prototype.copy(lastName = "AAA"))
        model.addElement(prototype.copy(lastName = "CCC1"))
        model.addElement(prototype.copy(lastName = "CCC2"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    private fun calculateIndex(client: Client) = model.calculateInsertIndex(client)

}
