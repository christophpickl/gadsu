package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.ClientView
import at.cpickl.gadsu.testinfra.GuiceIntegrationTest
import at.cpickl.gadsu.testinfra.TEST_UUID1
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import org.mockito.Mockito.*
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import javax.inject.Inject

@Test(groups = arrayOf("integration", "guice"))
class ClientIntegrationTest : GuiceIntegrationTest() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Inject private var currentClient: CurrentClient? = null
    @Inject private var view: ClientView? = null

    fun `Given mock client repository setup, when post a SaveClientEvent, then some other events should be dispatched and repository calls invoked`() {

        currentClient!!.data = Client.unsavedValidInstance() // trigger a CurrentChangedEvent => view will read this data

        val viewClient = view!!.detailView.readClient()
        val expectedToSaveClient = viewClient.copy(created = clock.now())
        val savedClient = expectedToSaveClient.copy(id = TEST_UUID1)
        val saveEvent = SaveClientEvent()

        log.trace("expectedToSaveClient: $expectedToSaveClient")
        `when`(mockClientRepository.insertWithoutPicture(expectedToSaveClient)).thenReturn(savedClient)

        bus.post(saveEvent)

        // watch out: checking for contains, instead of hasItems, will lead to very (too!) strict assertion
        busListener.assertHasItems(
                saveEvent, // we did it :)
                ClientCreatedEvent(savedClient),
                ClientSelectedEvent(savedClient, null)
        )

        verify(mockClientRepository).insertWithoutPicture(expectedToSaveClient)
        verify(mockTreatmentRepository).findAllFor(savedClient)
        verify(mockXPropsRepository).delete(savedClient)
        verify(mockXPropsRepository).insert(savedClient, emptyList())
        verifyNoMoreInteractions(mockClientRepository, mockTreatmentRepository, mockXPropsRepository)
    }

}
