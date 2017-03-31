package non_test.release_generator

import at.cpickl.gadsu.GadsuException
import com.github.christophpickl.kpotpourri.common.toPrettyString
import java.io.File
import java.nio.file.Files

private val GITHUB_CONFIG = GithubConfig(
        repositoryOwner = "christophpickl",
        // repositoryName = "gadsu",
        repositoryName = "gadsu_release_playground",
        username = "christoph.pickl@gmail.com", // or could load creds from ~/.gadsu/github.properties
        password = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
)
private val RELEASE_CONFIG = ReleaseConfig(
        sourceReleaseBuildFolder = File("/Users/wu/Dev/shiatsu/gadsu_release_build/release_artifacts"),
        targetReleaseArtifactsFolder = File("/Users/wu/Kampfkunst/Shiatsu/___GADSU/Releases") // add "vX.X.X" folder
)

fun main(args: Array<String>) {
    println("Gadsu Release Generator - START")
    println()

    val githubApi = GithubApi(GITHUB_CONFIG)
    val generator = ReleaseGenerator(githubApi, RELEASE_CONFIG)

    val milestone = generator.selectMilestone()
    generator.executeRelease(milestone)

    println()
    println("Gadsu Release Generator - END")
}

private class ReleaseGenerator(
        private val githubApi: GithubApi,
        private val config: ReleaseConfig
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

        ReleaseValidator(githubApi, milestone, issues, config).validate()
        val releaseText = generateReleaseText(issues)

        // TODO display confirmation message with all prepared data
        println("""Release Summary
================
Version: ${milestone.version}
Issues: ${issues.size}

LOCAL Artifacts source folder: ${config.sourceReleaseBuildFolder.absolutePath}
LOCAL Release target folder: ${config.targetReleaseArtifactsFolder}

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

        val targetMilestoneFolder = config.targetMilestoneFolder(milestone)
        println("Create local folder: ${targetMilestoneFolder.absolutePath}")
        targetMilestoneFolder.mkdir()
        config.sourceArtifactFiles(milestone).forEach { file ->
            val target = File(targetMilestoneFolder, file.name)
            println("Moving file from: ${file.absolutePath}\n  to: ${target.absolutePath}")
            file.move(target)
        }
        println()

        println("Closing milestone ${milestone.version}.")
        githubApi.close(milestone)

        println("Creating new GitHub release.")
        githubApi.createNewRelease(CreateReleaseRequest(
                tag_name = milestone.version,
                name = "Release ${milestone.version}",
                body = releaseText
        ))

        println("Uploading release artifacts.")
        // TODO upload artifacts to github
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

data class ReleaseConfig(
        val sourceReleaseBuildFolder: File,
        val targetReleaseArtifactsFolder: File
) {
    fun targetMilestoneFolder(milestone: Milestone) = File(targetReleaseArtifactsFolder, "v${milestone.version}")
    fun sourceArtifactFiles(milestone: Milestone) = ArtifactTypes.values().map { File(sourceReleaseBuildFolder, it.toFilename(milestone)) }
}

private enum class ArtifactTypes {
    Jar {
        override fun toFilename(milestone: Milestone) = "Gadsu-${milestone.version}.jar"
    },
    Exe {
        override fun toFilename(milestone: Milestone) = "Gadsu.exe"
    },
    Dmg {
        override fun toFilename(milestone: Milestone) = "Gadsu-${milestone.version}.dmg"
    };

    abstract fun toFilename(milestone: Milestone): String
}

@Deprecated("use kpotpourri's implementation instead", ReplaceWith("file.move(target)", "com.github.christophpickl.kpotpourri.common.move"))
fun File.move(target: File) {
    Files.move(this.toPath(), target.toPath())
}
