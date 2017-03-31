package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import com.github.christophpickl.kpotpourri.common.toPrettyString

fun main(args: Array<String>) {
    println("release generator START")

    val generator = ReleaseGenerator(
            //            repositoryName = "gadsu"
            repositoryName = "gadsu_release_playground"
    )
    val milestone = generator.selectMilestone()
    val issues = generator.issuesFor(milestone)
    val releaseText = generator.generateReleaseText(issues)
    println(releaseText)


    // * check for release artifacts
    // * display confirmation message with all prepared data

    // * create new release folder locally and move built artifacts there
    // * create new release
    //   - Select existing GIT tag
    //   - upload artifacts (check for existence)
    // * close milestone


}

class ReleaseGenerator(
        repositoryName: String
) {

    private val github = GithubApi(
            baseGithubUrl = "/repos/christophpickl/$repositoryName",
            githubUser = "christoph.pickl@gmail.com",
            githubPass = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
    )

    private val milestones by lazy { github.listOpenMilestones().filterNot { it.version == "ongoing" } }

    fun foobarStuff() {
//    GithubApi.createNewRelease("1.9.0", releaseText)
//    val issues = GithubApi.listIssues()
//    println("Got ${issues.size} issues back.")
//    println(issues.map(Issue::toString).joinToString("\n"))
    }

    fun selectMilestone(): Milestone {
        milestones.forEachIndexed { i, (version) ->
            println("( ${i + 1} ) $version")
        }
        print(">> [1] ")
        val input = readLine()!!

        val selectedIndex = if (input.isEmpty()) 1 else input.toInt()
        return milestones[selectedIndex - 1]
    }

    fun issuesFor(milestone: Milestone): List<Issue> {
        val issues = github
                .listIssues(milestone)
                .sortedBy { it.number }
        val nonClosedIssues = issues.filter { it.state != State.Closed }
        if (nonClosedIssues.isNotEmpty()) {
            throw ReleaseException("There have been non closed issues in the given milestone!\n${nonClosedIssues.toPrettyString()}")
        }
        return issues
    }


    fun generateReleaseText(issues: List<Issue>): String {
        val issuesText = issues
                .map { "- ${if (it.state == State.Closed) "" else "!!! State=${it.state} !!! "}#${it.number} ${it.title}" }
                .joinToString("\n")

        return """Windows users please use the EXE, Apple users the DMG and for all other Linuxe/Unixe the platform independent JAR file.

New stuff:
$issuesText
"""
    }
}

class ReleaseException(message: String, cause: Throwable? = null) : GadsuException(message, cause)
