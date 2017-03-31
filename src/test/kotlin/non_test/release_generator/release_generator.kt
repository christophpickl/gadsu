package non_test.release_generator

fun main(args: Array<String>) {
    println("release generator START")

    val milestone = ReleaseGenerator.selectMilestone()
    val releaseText = ReleaseGenerator.generateReleaseText(milestone)
    println(releaseText)

//    GithubApi.createNewRelease("1.9.0", releaseText)

    // * check if there are no non-closed issues
    // * check for release artifacts
    // * display confirmation message with all prepared data

    // * create new release folder locally and move built artifacts there
    // * create new release
    //   - Select existing GIT tag
    //   - upload artifacts (check for existence)
    // * close milestone


    println(GithubApi.listOpenMilestones())

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
