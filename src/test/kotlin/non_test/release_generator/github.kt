package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.KotlinNoArg
import at.cpickl.gadsu.service.LOG
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method


data class GithubConfig(
        val repositoryOwner: String,
        val repositoryName: String,
        val username: String,
        val password: String
)


/**
 * https://developer.github.com/v3/
 */
class GithubApi (
        private val config: GithubConfig
) {

    companion object {
        private val GITHUB_MIMETYPE = "application/vnd.github.v3+json"
    }

    private val log = LOG(javaClass)
    private val mapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    private val baseGithubUrl = "/repos/${config.repositoryOwner}/${config.repositoryName}"

    init {
        FuelManager.instance.basePath = "https://api.github.com"
    }

    /**
     * POST /repos/:owner/:repo/releases
     *
     * https://developer.github.com/v3/repos/releases/#create-a-release
     */
    fun createNewRelease(createRequest: CreateReleaseRequest) {
        val response = request(
                method = Method.POST,
                url = "$baseGithubUrl/releases",
                requestEntity = createRequest,
                returnType = CreateReleaseResponse::class.java
        )
        println("createNewRelease: $response") // TODO finish
    }

    /**
     * GET /repos/:owner/:repo/tags
     *
     * https://developer.github.com/v3/repos/#list-tags
     */
    fun listTags() = request(
                method = Method.GET,
                url = "$baseGithubUrl/tags",
                returnType = Array<TagResponse>::class.java
        ).toList().sortedBy { it.name }

    private fun uploadReleaseAsset() {
        // POST https://<upload_url>/repos/:owner/:repo/releases/:id/assets?name=foo.zip

    }

    /**
     * PATCH /repos/:owner/:repo/milestones/:number
     *
     * https://developer.github.com/v3/issues/milestones/#update-a-milestone
     */
    fun close(milestone: Milestone) {
        if (milestone.state == State.Closed) {
            throw GadsuException("Milestone already closed: $milestone")
        }
        val response = request(
                method = Method.POST,
                url = "$baseGithubUrl/milestones/${milestone.number}",
                requestEntity = UpdateMilestone(
                        state = State.Closed.jsonValue
                ),
                headers = listOf("X-HTTP-Method-Override" to "PATCH"), // HttpURLConnection hack which does not support PATCH method
                returnType = UpdateMilestoneResponseJson::class.java
        )
        assert(response.state == State.Closed.jsonValue)
    }

    fun listOpenMilestones(): List<Milestone> {
        log.debug("listOpenMilestones()")

        return request(
                method = Method.GET,
                url = "$baseGithubUrl/milestones",
                // state defaults to open
                returnType = Array<MilestoneJson>::class.java)
                .map { it.toMilestone() }
                .sortedBy { it.version }
    }

    fun listIssues(milestone: Milestone): List<Issue> {
        log.debug("listIssues(milestone={})", milestone)

        return request(
                method = Method.GET,
                url = "$baseGithubUrl/issues",
                parameters = listOf(
                        "state" to "all",
                        "milestone" to milestone.number
                ),
                returnType = Array<IssueJson>::class.java)
                .map { it.toIssue() }
                .sortedBy { it.number }
    }

    /**
     * @param query parameters
     */
    private fun <T> request(
            method: Method,
            url: String,
            returnType: Class<T>,
            parameters: List<Pair<String, Any?>>? = null,
            headers: List<Pair<String, String>>? = null,
            requestEntity: Any? = null
    ): T {
        val (_, response, result) = FuelManager.instance.request(method = method, path = url, param = parameters).apply {
            if (requestEntity != null) {
                body(mapper.writeValueAsString(requestEntity))
            }
        }
                .authenticate(config.username, config.password)
                .header("Accept" to GITHUB_MIMETYPE)
                .apply { if (headers !=null ) { httpHeaders.putAll(headers)} }
                .responseString()

        log.trace("Status code: {}", response.httpStatusCode)

        result.fold({ success: String ->
            return mapper.readValue(success, returnType)
        }, { fail: FuelError ->
            throw GadsuException("GitHub call failed for URL: $url with parameters: $parameters! (fuel says: $fail)", fail.exception)
        })
    }

}

@KotlinNoArg
annotation class JsonData

private @JsonData data class MilestoneJson(
        val title: String,
        val number: Int,
        val state: String,
        val url: String
) {
    fun toMilestone() = Milestone(
            version = title,
            number = number,
            state = State.byJsonValue(state),
            url = url
    )
}

private @JsonData data class IssueJson(
        val title: String,
        val number: Int,
        val state: String, // "open", "closed"
        val milestone: MilestoneJson? = null
) {
    fun toIssue() = Issue(
            title = title,
            number = number,
            state = State.byJsonValue(state),
            milestone = milestone?.toMilestone()
    )
}


private @JsonData data class UpdateMilestone(
        val state: String
)

private @JsonData data class UpdateMilestoneResponseJson(
        val state: String
)

private @JsonData data class CreateReleaseResponse(
        val url: String
)
