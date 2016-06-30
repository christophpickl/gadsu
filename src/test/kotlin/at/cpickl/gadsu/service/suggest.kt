package at.cpickl.gadsu.service

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.view.ViewNames
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test


@Test
class SuggesterImplTest {

    private lateinit var clientRepo: ClientRepository

    @BeforeMethod
    fun initMocks() {
        clientRepo = mock(ClientRepository::class.java)
    }

    fun `suggest general stuff`() {
        returnClients(client(job = "Developer"))

        assertSuggest(ViewNames.Client.InputJob, "", null)
        assertSuggest(ViewNames.Client.InputJob, "d", null)
        assertSuggest(ViewNames.Client.InputJob, "D", "Developer")
        assertSuggest(ViewNames.Client.InputJob, "De", "Developer")
        assertSuggest(ViewNames.Client.InputJob, "Da", null)
        assertSuggest(ViewNames.Client.InputJob, "Developer", null)
    }

    fun `suggest job, autocomplete`() {
        returnClients(client(job = "Developer"))
        assertSuggest(ViewNames.Client.InputJob, "D", "Developer")
    }

    fun `suggest country of origin, autocomplete`() {
        returnClients(client(countryOfOrigin = "austria"))
        assertSuggest(ViewNames.Client.InputCountryOfOrigin, "a", "austria")
    }

    fun `suggest children, autocomplete`() {
        returnClients(client(children = "keine"))
        assertSuggest(ViewNames.Client.InputChildren, "k", "keine")
    }

    fun `suggest zipCode, autocomplete`() {
        returnClients(client(zipCode = "1010"))
        assertSuggest(ViewNames.Client.InputZipCode, "1", "1010")
    }

    fun `suggest city, autocomplete`() {
        returnClients(client(city = "Wien"))
        assertSuggest(ViewNames.Client.InputCity, "W", "Wien")
    }

    private fun assertSuggest(viewName: String, entered: String, expected: String?) {
        val actual = SuggesterImpl(clientRepo).suggest(viewName, entered)
        assertThat(actual, equalTo(expected))
    }

    private fun returnClients(vararg clients: Client) {
        `when`(clientRepo.findAll()).thenReturn(clients.toList())
    }

    private fun client(job: String = "", countryOfOrigin: String = "", children: String = "", zipCode: String = "", city: String = "") =
        Client.INSERT_PROTOTYPE.copy(
            job = job,
            countryOfOrigin = countryOfOrigin,
            children = children,
            contact = Contact.INSERT_PROTOTYPE.copy(
                zipCode = zipCode,
                city = city
            )
        )

}