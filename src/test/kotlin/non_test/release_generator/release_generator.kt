package non_test.release_generator

import at.cpickl.gadsu.global.GadsuException
import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.kpotpourri.common.file.humanReadableSize
import com.github.christophpickl.kpotpourri.common.file.move
import com.github.christophpickl.kpotpourri.common.io.Keyboard
import com.github.christophpickl.kpotpourri.github.AssetUpload
import com.github.christophpickl.kpotpourri.github.CreateReleaseRequest
import com.github.christophpickl.kpotpourri.github.GithubApi
import com.github.christophpickl.kpotpourri.github.Issue
import com.github.christophpickl.kpotpourri.github.Milestone
import com.github.christophpickl.kpotpourri.github.RepositoryConfig
import com.github.christophpickl.kpotpourri.github.buildGithub4k
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import java.io.File

private val REPO_CONFIG = RepositoryConfig(
        repositoryOwner = "christophpickl",
        repositoryName = "gadsu",
        username = "christoph.pickl@gmail.com", // or could load creds from ~/.gadsu/github.properties
        // MINOR use kpotpourri github4k version 1.7 new detectGithubPass()
        password = System.getProperty("github.pass", null) ?: throw RuntimeException("Expected to have set -Dgithub.pass")
)
private val TEST_REPO_CONFIG = REPO_CONFIG.copy(repositoryName = "gadsu_release_playground")

private val RELEASE_CONFIG = ReleaseConfig(
        sourceReleaseBuildFolder = File("/Users/wu/Dev/shiatsu/gadsu_release_build/release_artifacts"),
        targetReleaseArtifactsFolder = File("/Users/wu/Kampfkunst/Shiatsu/___GADSU/Releases") // add "vX.X.X" folder
)

fun main(args: Array<String>) {
    configureLogging()
    println("Gadsu Release Generator - START")
    println()

//    if (true) {
//        buildGithub4k(REPO_CONFIG).listReleases().prettyPrint()
//        return
//    }

    ReleaseGenerator(buildGithub4k(REPO_CONFIG), RELEASE_CONFIG).executeRelease()

    println()
    println("Gadsu Release Generator - END")
}

private fun configureLogging() {
    Logback4k.reconfigure {
        rootLevel = Level.ALL
        packageLevel(Level.WARN,
                "org.apache",
                "com.github.christophpickl.kpotpourri"
        )
        addConsoleAppender()
    }
}

private class ReleaseGenerator(
        private val githubApi: GithubApi,
        private val config: ReleaseConfig
) {

    private val milestones by lazy { githubApi.listOpenMilestones().filterNot { it.version == "ongoing" } }

    fun executeRelease() {
        val milestone = selectMilestone()
        val issues = githubApi.listIssues(milestone)
        ReleaseValidator(githubApi, milestone, issues, config).validate()
        val releaseText = generateReleaseText(issues)

        printSummary(milestone, issues, releaseText)

        println("**********> Confirm Release <**********")
        if (!Keyboard.readConfirmation("Really go on?", defaultConfirm = false)) {
            println("Release process aborted by user.")
            return
        }

        // now do the actual release!
        // ------------------------

        println("Creating new GitHub release.")
        val release = githubApi.createNewRelease(CreateReleaseRequest(
                tag_name = milestone.version,
                name = "Release ${milestone.version}",
                body = releaseText
        ))
        println()

        println("Going to upload the release artifacts, this can take some time ...")
        config.sourceArtifactFiles(milestone).forEach { (type, file) ->
            println("  ... uploading: ${file.name} (${file.humanReadableSize})")
            githubApi.uploadReleaseAsset(AssetUpload(
                    releaseId = release.id,
                    bytes = file.readBytes(),
                    fileName = file.name,
                    contentType = type.contentType
            ))
        }
        println()

        println("Closing milestone ${milestone.version}.")
        githubApi.close(milestone)
        println()

        val targetMilestoneFolder = config.targetMilestoneFolder(milestone)
        println("Create local folder: ${targetMilestoneFolder.absolutePath}")
        targetMilestoneFolder.mkdir()
        config.sourceArtifactFiles(milestone).forEach { (_, file) ->
            val target = File(targetMilestoneFolder, file.name)
            println("Moving file from: ${file.absolutePath}\n  to: ${target.absolutePath}")
            file.move(target)
        }
    }

    private fun selectMilestone(): Milestone {
        println("Select a milestone to be released:")
        milestones.forEachIndexed { i, (version) ->
            println("( ${i + 1} ) $version")
        }
        print(">> [1] ")
        val input = readLine()!!

        val selectedIndex = if (input.isEmpty()) 1 else input.toInt()
        return milestones[selectedIndex - 1]
    }

    private fun generateReleaseText(issues: List<Issue>): String {
        val issuesText = issues
                .map { "- #${it.number} ${it.title}" }
                .joinToString("\n")

        return """Windows users please use the EXE, Apple users the DMG and for all other Linuxe/Unixe the platform independent JAR file.

New stuff:
$issuesText"""
    }

    private fun printSummary(milestone: Milestone, issues: List<Issue>, releaseText: String) {
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
    }

}

open class ReleaseException(message: String, cause: Throwable? = null) : GadsuException(message, cause)

data class ReleaseConfig(
        val sourceReleaseBuildFolder: File,
        val targetReleaseArtifactsFolder: File
) {
    fun targetMilestoneFolder(milestone: Milestone) = File(targetReleaseArtifactsFolder, "v${milestone.version}")
    fun sourceArtifactFiles(milestone: Milestone) = ArtifactTypes.values().map { Pair(it, File(sourceReleaseBuildFolder, it.toFilename(milestone))) }
}

enum class ArtifactTypes(val contentType: String) {
    Jar("application/java-archive") {
        override fun toFilename(milestone: Milestone) = "Gadsu-${milestone.version}.jar"
    },
    Exe("application/x-msdownload") {
        override fun toFilename(milestone: Milestone) = "Gadsu.exe"
    },
    Dmg("application/x-apple-diskimage") {
        override fun toFilename(milestone: Milestone) = "Gadsu-${milestone.version}.dmg"
    };

    abstract fun toFilename(milestone: Milestone): String

}
