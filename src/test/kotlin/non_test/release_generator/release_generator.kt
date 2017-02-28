package non_test.release_generator

import at.cpickl.gadsu.KotlinNoArg
import at.cpickl.gadsu.service.LOG
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method

fun List<Any>.prettyPrint() {
    println(map { "- " + it.toString() }.joinToString("\n"))
}

fun main(args: Array<String>) {
    println("release generator START")

    val milestone = ReleaseGenerator.selectMilestone()
    val releaseText = ReleaseGenerator.generateReleaseText(milestone)
    println(releaseText)

    GithubApi.createNewRelease("1.9.0", releaseText)

    // * check if there are no non-closed issues
    // * check for release artifacts
    // * display confirmation message with all prepared data

    // * create new release folder locally and move built artifacts there
    // * create new release
    //   - Select existing GIT tag
    //   - upload artifacts (check for existence)
    // * close milestone


//    println(GithubApi.listOpenMilestones())

//    val issues = GithubApi.listIssues()
//    println("Got ${issues.size} issues back.")
//    println(issues.map(Issue::toString).joinToString("\n"))
}

object ReleaseGenerator {

    val milestones = GithubApi.listOpenMilestones()

    fun selectMilestone(): Milestone {
        milestones.forEachIndexed { i, milestone ->
            println("( ${i + 1} ) ${milestone.version}")
        }
        print(">> [1] ")
        val input = readLine()!!

        val selectedIndex = if (input.isEmpty()) 1 else input.toInt()
        return milestones[selectedIndex - 1]
    }

    fun generateReleaseText(milestone: Milestone): String {
        val issuesText = GithubApi
                .listIssues(milestone)
                .sortedBy { it.number }
                .map { "- ${if (it.state == State.Closed) "" else "!!! State=${it.state} !!! "}#${it.number} ${it.title}" }
                .joinToString("\n")

        return """Windows users please use the EXE, Apple users the DMG and for all other Linuxe/Unixe the platform independent JAR file.

New stuff:
$issuesText
"""
    }
}

/** https://developer.github.com/v3/ */
object GithubApi {

    private val log = LOG(javaClass)
    private val baseGithubUrl = "/repos/christophpickl/gadsu"

    // load creds from ~/.gadsu/github.properties
    private val githubUser = "christoph.pickl@gmail.com"
    private val githubPass = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
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
        val (request, response, result) = FuelManager.instance.request(method = method, path = url, param = parameters).apply {
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


data class Issue(
        val title: String,
        val number: Int,
        val state: State,
        val milestone: Milestone?
)

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

private @JsonData data class CreateRelease(
        val tag_name: String,
        val name: String,
        val body: String,
        val draft: Boolean = true,
        val prerelease: Boolean = false
        /*
          "tag_name": "v1.0.0",
  "target_commitish": "master",
  "name": "v1.0.0",
  "body": "Description of the release",
  "draft": false,
  "prerelease": false
         */
)

private @JsonData data class CreateReleaseResponse(
        val url: String
)

enum class State(
        val jsonValue: String
) {
    Open("open"),
    Closed("closed");

    companion object {
        fun byJsonValue(seek: String) = State.values().first { it.jsonValue == seek }
    }
}

data class Milestone(
        val version: String,
        val number: Int,
        val state: State,
        val url: String,
        val issuesCountOpen: Int,
        val issuesCountClosed: Int
)

private @JsonData data class MilestoneJson(
        val title: String,
        val number: Int,
        val state: String,
        val url: String,
        val open_issues: Int,
        val closed_issues: Int
) {
    fun toMilestone() = Milestone(
            version = title,
            number = number,
            state = State.byJsonValue(state),
            url = url,
            issuesCountOpen = open_issues,
            issuesCountClosed = closed_issues
    )
}

@KotlinNoArg
annotation class JsonData
