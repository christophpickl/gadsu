package at.cpickl.gadsu.appointment.gcal

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientState
import net.ricecode.similarity.JaroWinklerStrategy
import net.ricecode.similarity.StringSimilarityServiceImpl
import javax.inject.Inject

interface MatchClients {
    fun findMatchingClients(name: String): List<Client>
}

class MatchClientsInDb @Inject constructor(private val clientRepo: ClientRepository) : MatchClients {

    companion object {
        private val SCORE_THRESHOLD = 0.8
    }

    private val similarityService = StringSimilarityServiceImpl(JaroWinklerStrategy())

    override fun findMatchingClients(name: String): List<Client> {
        val clients = clientRepo.findAll(ClientState.ACTIVE)
        return clients.filter {
            similarityService.score(name, it.firstName) > SCORE_THRESHOLD ||
                    similarityService.score(name, it.nickName) > SCORE_THRESHOLD ||
                    similarityService.score(name, it.lastName) > SCORE_THRESHOLD
        }
    }

}
