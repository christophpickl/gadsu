package non_test.release_generator

import at.cpickl.gadsu.service.LOG
import com.github.christophpickl.kpotpourri.common.toPrettyString
import non_test.release_generator.ValidationResult.ValidationError
import non_test.release_generator.ValidationResult.ValidationSuccess
import java.util.LinkedList

//private interface ValidationStep {
//    fun execute()
//}
sealed class ValidationResult {
    object ValidationSuccess : ValidationResult()
    class ValidationError(
            val exceptions: List<ReleaseValidationException>
    ) : ValidationResult()
}

private class ValidationCollector {

    private val validationSteps: MutableList<() -> Unit> = LinkedList()

    fun add(validationStep: () -> Unit): ValidationCollector {
        validationSteps.add(validationStep)
        return this
    }

    fun execute(): ValidationResult {
        val exceptions = LinkedList<ReleaseValidationException>()
        for (step in validationSteps) {
            try {
                step.invoke()
            } catch (e: ReleaseValidationException) {
                exceptions.add(e)
            }
        }
        if (exceptions.isEmpty()) {
            return ValidationSuccess
        }
        return ValidationError(exceptions)
    }
}

class ReleaseValidator(
        private val githubApi: GithubApi,
        private val milestone: Milestone,
        private val issues: List<Issue>
) {
    private val log = LOG(javaClass)

    /**
     * @throws ReleaseValidationException
     */
    fun validate() {
        val validationResult = ValidationCollector().add({
            tagExists()
        }).add({
            allIssuesClosed()
        }).execute()

        when (validationResult) {
            is ValidationSuccess -> log.debug("Release validation OK.")
            is ValidationError ->
                throw ReleaseValidationException(
                        "Release validation failed! Following error(s) occured:\n" +
                                validationResult.exceptions.map { "- ${it.message}" }.joinToString("\n")
                )

        }
    }

    // TODO verify local folders exist, release artifacts (jar/dmg/exe) exists

    private fun milestoneOpen() {
        if (milestone.state != State.Open) {
            fail("The milestone is to be expected in state open! But was: ${milestone.state}")
        }
    }

    private fun allIssuesClosed() {
        val nonClosedIssues = issues.filter { it.state != State.Closed }
        if (nonClosedIssues.isNotEmpty()) {
            fail("There have been non closed issues in the given milestone!\n${nonClosedIssues.toPrettyString()}")
        }
    }

    private fun tagExists() {
        val tags = githubApi.listTags()
        if (tags.find { it.name == milestone.version } == null) {
            fail("The required tag '${milestone.version}' does not exist! Available tags: ${tags.map { it.name }.joinToString(", ")}")
        }
    }

    private fun fail(message: String) {
        throw InternalReleaseValidationException(message)
    }

}

class ReleaseValidationException(message: String) : ReleaseException(message)

private class InternalReleaseValidationException(message: String) : ReleaseException(message)
