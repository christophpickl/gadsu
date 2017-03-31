package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.LOG
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method


/**
 * https://developer.github.com/v3/
 */
class GithubApi(
        // /repos/REPO_OWNER/REPO_NAME
        private val baseGithubUrl: String,
        // or could load creds from ~/.gadsu/github.properties
        private val githubUser: String,
        private val githubPass: String
) {

    companion object {
        private val GITHUB_MIMETYPE = "application/vnd.github.v3+json"
    }

    private val log = LOG(javaClass)

    private val mapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    init {
        FuelManager.instance.basePath = "https://api.github.com"
    }

    fun createNewRelease(versionString: String, releaseText: String) {
        val foo = request(
                method = Method.POST,
                url = "$baseGithubUrl/releases",
                requestEntity = CreateRelease(
                        tag_name = "v$versionString",
                        name = "Release $versionString",
                        body = releaseText
                ),
                returnType = CreateReleaseResponse::class.java
        )
        println("foo: $foo")
    }

    private fun uploadReleaseAsset() {
        // POST https://<upload_url>/repos/:owner/:repo/releases/:id/assets?name=foo.zip

    }

    /**
     * PATCH /repos/:owner/:repo/milestones/:number
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
                .authenticate(githubUser, githubPass)
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
