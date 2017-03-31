package non_test.release_generator


data class Issue(
        val title: String,
        val number: Int,
        val state: State,
        val milestone: Milestone?
)

data class Milestone(
        val version: String,
        val number: Int,
        val state: State,
        val url: String
)


@JsonData data class CreateReleaseRequest(
        val tag_name: String,
        val name: String,
        val body: String,
        val draft: Boolean = true,
        val prerelease: Boolean = false
)

@JsonData data class TagResponse(
        val name: String
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

