package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import com.github.christophpickl.kpotpourri.common.toPrettyString

private val GITHUB_CONFIG = GithubConfig(
        repositoryOwner = "christophpickl",
        // repositoryName = "gadsu",
        repositoryName = "gadsu_release_playground",
        username = "christoph.pickl@gmail.com", // or could load creds from ~/.gadsu/github.properties
        password = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
)
// release folder: /Users/wu/Dev/shiatsu/gadsu_release_build/release_artifacts
// target folder: /Users/wu/Kampfkunst/Shiatsu/___GADSU/Releases/vX.X.X

fun main(args: Array<String>) {
    println("Gadsu Release Generator - START")
    println()

    val githubApi = GithubApi(GITHUB_CONFIG)
    val generator = ReleaseGenerator(githubApi)

    val milestone = generator.selectMilestone()
    generator.executeRelease(milestone)

    println()
    println("Gadsu Release Generator - END")
}

class ReleaseGenerator(
        private val githubApi: GithubApi
) {


    private val milestones by lazy { githubApi.listOpenMilestones().filterNot { it.version == "ongoing" } }

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
     * Finally the method which has some permanent effects.
     */
    fun executeRelease(milestone: Milestone) {
        val issues = githubApi.listIssues(milestone)

        ReleaseValidator(githubApi, milestone, issues).validate()
        val releaseText = generateReleaseText(issues)

        // TODO display confirmation message with all prepared data
        println("""Release Summary
================
Version: ${milestone.version}
Issues: ${issues.size}

Release Text:
----------------
$releaseText
----------------

Milestone: $milestone
Issues:
${issues.toPrettyString()}
""")

        println("**********> Confirm Release <**********")
        println("Confirm [y/n]:")
        val input = readLine()
        if (input != "y") {
            println("Release process aborted by user.")
            return
        }

        // do the actual release!
        // ------------------------

        // TODO create new release folder locally and move built artifacts there
        println("Preparing filesystem.")

        println("Closing milestone.")
        githubApi.close(milestone)

        println("Creating new GitHub release.")
        githubApi.createNewRelease(CreateReleaseRequest(
                tag_name = milestone.version,
                name = "Release ${milestone.version}",
                body = releaseText
        ))

        // TODO upload artifacts to github
        println("Uploading release artifacts.")
    }

    private fun generateReleaseText(issues: List<Issue>): String {
        val issuesText = issues
                .map { "- #${it.number} ${it.title}" }
                .joinToString("\n")

        return """Windows users please use the EXE, Apple users the DMG and for all other Linuxe/Unixe the platform independent JAR file.

New stuff:
$issuesText"""
    }

}

open class ReleaseException(message: String, cause: Throwable? = null) : GadsuException(message, cause)

