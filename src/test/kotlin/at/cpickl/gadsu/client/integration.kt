package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.ClientView
import at.cpickl.gadsu.service.CurrentChangedEvent
import at.cpickl.gadsu.testinfra.GuiceIntegrationTest
import at.cpickl.gadsu.testinfra.TEST_UUID1
import org.mockito.Mockito.*
import org.slf4j.LoggerFactory
import org.testng.annotations.Test
import javax.inject.Inject

@Test(groups = arrayOf("integration", "guice"))
class ClientIntegrationTest : GuiceIntegrationTest() {
    private val log = LoggerFactory.getLogger(javaClass)

    @Inject private var view: ClientView? = null

    fun `Given mock client repository setup, when post a SaveClientEvent, then some other events should be dispatched and repository calls invoked`() {
        view!!.detailView.writeClient(Client.unsavedValidInstance())
        val viewClient = view!!.detailView.readClient()
        val expectedToSaveClient = viewClient.copy(created = clock.now())
        val savedClient = expectedToSaveClient.copy(id = TEST_UUID1)
        val saveEvent = SaveClientEvent()

        log.trace("expectedToSaveClient: $expectedToSaveClient")
        `when`(mockClientRepository.insert(expectedToSaveClient)).thenReturn(savedClient)

        bus.post(saveEvent)

        busListener.assertContains(
                saveEvent, // we did it :)
                ClientCreatedEvent(savedClient),
                ClientSelectedEvent(savedClient, null),
                CurrentChangedEvent("client", Client.INSERT_PROTOTYPE, savedClient)
        )
        verify(mockClientRepository).insert(expectedToSaveClient)
        verify(mockTreatmentRepository).findAllFor(savedClient)
        verifyNoMoreInteractions(mockClientRepository, mockTreatmentRepository)
    }

}

