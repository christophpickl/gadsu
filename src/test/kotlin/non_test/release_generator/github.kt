package non_test.release_generator

import at.cpickl.gadsu.service.LOG
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method


/** https://developer.github.com/v3/ */
class GithubApi(
        private val baseGithubUrl: String,
        // or could load creds from ~/.gadsu/github.properties
        private val githubUser: String,
        private val githubPass: String
) {

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

    private fun <T> request(method: Method, url: String, returnType: Class<T>, parameters: List<Pair<String, Any?>>? = null, requestEntity: Any? = null): T {
        val (_, _, result) = FuelManager.instance.request(method = method, path = url, param = parameters).apply {
            if (requestEntity != null) {
                body(mapper.writeValueAsString(requestEntity))
            }
        }
                .authenticate(githubUser, githubPass)
                .header("Accept" to "application/vnd.github.v3+json")
                .responseString()

        result.fold({ success: String ->
            return mapper.readValue(success, returnType)
        }, { fail: FuelError ->
            throw RuntimeException("GitHub call failed for URL: $url with parameters: $parameters! (fuel says: $fail)")
        })
    }

}
