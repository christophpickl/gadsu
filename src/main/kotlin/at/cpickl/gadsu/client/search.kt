package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.view.ClientViewController
import at.cpickl.gadsu.client.view.ExtendedClient
import at.cpickl.gadsu.service.Logged
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

data class ClientSearchEvent(val term: String)

@Logged
class ClientSearchController @Inject constructor(
        private val viewController: ClientViewController
) {

    private val filterAll: (ExtendedClient) -> Boolean = { true }

    @Subscribe
    fun onClientSearchEvent(event: ClientSearchEvent) {
        viewController.searchFilter = if (event.term.isEmpty()) filterAll else buildFilter(event.term)
        viewController.researchClients()
    }

    private fun buildFilter(originalTerm: String): (ExtendedClient) -> Boolean {
        val terms = originalTerm.toLowerCase().split(" ").filter { it.isNotEmpty() }.map { it.trim() }
        return { client ->
            terms.all { term ->
                client.firstName.toLowerCase().contains(term) ||
                        client.lastName.toLowerCase().contains(term) ||
                        client.nickNameInt.toLowerCase().contains(term) ||
                        client.nickNameExt.toLowerCase().contains(term)
            }
        }
    }
}
