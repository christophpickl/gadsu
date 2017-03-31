package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import com.github.christophpickl.kpotpourri.common.toPrettyString

fun main(args: Array<String>) {
    println("release generator START")

    val generator = ReleaseGenerator(
            // repositoryName = "gadsu"
            repositoryName = "gadsu_release_playground"
    )

    if (false) {
        generator.foobarStuff()
        return
    }

    val milestone = generator.selectMilestone()
    val issues = generator.issuesFor(milestone)
    val releaseText = generator.generateReleaseText(issues)
    println(releaseText)

    // * check for released artifacts (DMG/JAR/EXE created before)
    // * print summary of what is going to be done (do the validation first, then print summary, then execute)
    //   == display confirmation message with all prepared data
    generator.executeRelease(milestone, issues, releaseText)

    // * create new release folder locally and move built artifacts there


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
        github.listTags()
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

    /**
     * Also does the "all issues closed"-validation.
     */
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

    /**
     * Finally the method which has some permanent effects.
     */
    fun executeRelease(milestone: Milestone, issues: List<Issue>, releaseText: String) {
        validateTagExists(milestone)

        println("Confirming release...")
        github.close(milestone)

        // TODO * create new release
        //   - Select existing GIT tag: milestone.tagLike
        //   - upload artifacts (check for existence before)
    }

    private fun validateTagExists(milestone: Milestone) {
        // milestone version is like "1.5"
        // tags are like "v1.5.0"
        val tags = github.listTags()
        tags.find { it.name == milestone.tagLike } ?:
                throw ReleaseException("The required tag '${milestone.tagLike}' does not exist! Available tags: ${tags.map { it.name }.joinToString(", ")}")
    }

    private val Milestone.tagLike: String get() = "v$version.0"
}

class ReleaseException(message: String, cause: Throwable? = null) : GadsuException(message, cause)
