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
//        println("findMatchingClients(name='$name')")
        val clients = clientRepo.findAll(ClientState.ACTIVE)
//        clients.forEach {
//            println("client name: '${it.firstName}' / '${it.nickName}' / '${it.lastName}'")
//            println("  Score first name: ${similarityService.score(name, it.firstName)}")
//            println("  Score nick name: ${similarityService.score(name, it.nickName)}")
//            println("  Score last name: ${similarityService.score(name, it.lastName)}")
//        }
        return clients.filter {
            checkScore(name, it.firstName) || checkScore(name, it.nickName) || checkScore(name, it.lastName)
        }
    }

    private fun checkScore(searchName: String, someNamePart: String): Boolean {
        return someNamePart.trim().split(" ").filter { it.trim().isNotEmpty() }
                .map { similarityService.score(searchName, it.trim()) }.any { it > SCORE_THRESHOLD }
    }

}
