package non_test.release_generator

import at.cpickl.gadsu.KotlinNoArg


data class Issue(
        val title: String,
        val number: Int,
        val state: State,
        val milestone: Milestone?
)

@JsonData data class IssueJson(
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

@JsonData data class CreateRelease(
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

@JsonData data class CreateReleaseResponse(
        val url: String
)

data class Milestone(
        val version: String,
        val number: Int,
        val state: State,
        val url: String,
        val issuesCountOpen: Int,
        val issuesCountClosed: Int
)

@JsonData data class MilestoneJson(
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

enum class State(
        val jsonValue: String
) {
    Open("open"),
    Closed("closed");

    companion object {
        fun byJsonValue(seek: String) = State.values().first { it.jsonValue == seek }
    }
}

