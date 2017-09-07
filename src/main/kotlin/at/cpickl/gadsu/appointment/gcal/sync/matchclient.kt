package at.cpickl.gadsu.appointment.gcal.sync

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.service.LOG
import net.ricecode.similarity.JaroWinklerStrategy
import net.ricecode.similarity.StringSimilarityServiceImpl
import javax.inject.Inject

interface MatchClients {

    fun findMatchingClients(
            name: String,
            yetClients: List<Client>? = null
    ): List<Client>

}

class MatchClientsInDb @Inject constructor(
        private val clientRepo: ClientRepository
) : MatchClients {

    companion object {
        private val SCORE_THRESHOLD = 0.8
    }

    private val log = LOG(javaClass)

    private val similarityService = StringSimilarityServiceImpl(JaroWinklerStrategy())

    override fun findMatchingClients(name: String, yetClients: List<Client>?): List<Client> {
        log.debug("findMatchingClients(name='{}', yetClients)", name)
        val clients = yetClients ?: clientRepo.findAll(ClientState.ACTIVE)

        return clients.map {
            SimilarClient(it,
                    checkScore(name, it.firstName),
                    checkScore(name, it.nickNameExt),
                    checkScore(name, it.nickNameInt),
                    checkScore(name, it.lastName)
            )
        }.filter {
            it.scoresFirst.anyReachesThreshold() ||
                    it.scoresNickExt.anyReachesThreshold() ||
                    it.scoresNickInt.anyReachesThreshold() ||
                    it.scoresLast.anyReachesThreshold()
        }.sortedByDescending {
            log.trace("Possible candidate: $it")
            listOf(
                    it.scoresFirst.max() ?: 0.0,
                    it.scoresNickExt.max() ?: 0.0,
                    it.scoresNickInt.max() ?: 0.0,
                    it.scoresLast.max() ?: 0.0
            ).max()!!
        }.map { it.client }
    }

    private fun List<Double>.anyReachesThreshold() = any { it > SCORE_THRESHOLD }

    private fun checkScore(searchName: String, someNamePart: String): List<Double> {
        return someNamePart.trim().split(" ").filter { it.trim().isNotEmpty() }
                .map { similarityService.score(searchName, it.trim()) }
    }

}

private data class SimilarClient(
        val client: Client,
        val scoresFirst: List<Double>,
        val scoresNickExt: List<Double>,
        val scoresNickInt: List<Double>,
        val scoresLast: List<Double>
)
