package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.ClientView
import at.cpickl.gadsu.client.view.ClientViewController
import at.cpickl.gadsu.service.Clock
import com.google.common.eventbus.EventBus
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import javax.swing.DefaultListModel

@Test class ClientViewControllerTest {

    private val prototype = Client.savedValidInstance().withLastName("BBB")
    private var model = DefaultListModel<Client>()

    @BeforeMethod
    fun resetState() {
        model = DefaultListModel<Client>()
    }

    fun calculateIndex_emptyModel_returns0() {
        assertThat(calculateIndex(prototype), equalTo(0))
    }

    fun calculateIndex_oneBeingLess_returns1() {
        model.addElement(prototype.withLastName("AAA"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    fun calculateIndex_oneBeingBigger_returns0() {
        model.addElement(prototype.withLastName("CCC"))
        assertThat(calculateIndex(prototype), equalTo(0))
    }

    fun calculateIndex_inBetween_returns1() {
        model.addElement(prototype.withLastName("AAA"))
        model.addElement(prototype.withLastName("CCC"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    fun calculateIndex_twoLessOneBigger_returns2() {
        model.addElement(prototype.withLastName("AAA1"))
        model.addElement(prototype.withLastName("AAA2"))
        model.addElement(prototype.withLastName("CCC"))
        assertThat(calculateIndex(prototype), equalTo(2))
    }

    fun calculateIndex_oneLessTwoBigger_returns1() {
        model.addElement(prototype.withLastName("AAA"))
        model.addElement(prototype.withLastName("CCC1"))
        model.addElement(prototype.withLastName("CCC2"))
        assertThat(calculateIndex(prototype), equalTo(1))
    }

    private fun calculateIndex(client: Client) = ClientViewController(EventBus(), mock(Clock::class.java),
            mock(ClientView::class.java), mock(ClientRepository::class.java)).calculateIndex(model, client)

}