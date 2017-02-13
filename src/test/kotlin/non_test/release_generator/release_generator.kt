package non_test.release_generator

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.google.common.base.MoreObjects

fun main(args: Array<String>) {
    println("release generator START")

    val issues = GithubApi.listIssues()
    println("Got ${issues.size} issues back.")
    println(issues.map(Issue::toString).joinToString("\n"))
}

object GithubApi {

    // load creds from ~/.gadsu/github.properties
    private val githubUser = "christoph.pickl@gmail.com"
    private val githubPass = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
    private val mapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    init {
        FuelManager.instance.basePath = "https://api.github.com"
    }

    fun createNewRelease(releaseName: String) {
        TODO()
    }

    // get all issues for current version
    fun listIssues(): List<Issue> {
        val (request, response, result) =
                Fuel.get("/repos/christophpickl/gadsu/issues", listOf("state" to "all"))
                        .authenticate(githubUser, githubPass)
                        .header("Accept" to "application/vnd.github.v3+json")
                        .responseString()

        result.fold({ success: String ->
            return mapper.readValue(success, Array<IssueJson>::class.java).map { it.toIssue() }
        }, { fail: FuelError ->
            throw RuntimeException("GitHub call failed! $fail")
        })
    }

    private fun IssueJson.toIssue() = Issue(
            title = title,
            state = State.byJsonValue(state),
            milestone = milestone?.let { Milestone(it.title) }
    )
}

data class Issue(
        val title: String,
        val state: State,
        val milestone: Milestone?
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
        val version: String
)

private class MilestoneJson {
    lateinit var title: String
}

private class IssueJson {
    lateinit var title: String
    lateinit var state: String // "open", "closed"
    var milestone: MilestoneJson? = null

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("title", title)
            .add("state", state)
            .toString()
}
