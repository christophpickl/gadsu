package at.cpickl.gadsu.testinfra

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.testng.Assert
import kotlin.reflect.KClass

object Expects {

    fun expect(type: KClass<out Exception>,
               action: () -> Unit,
               messageContains: String? = null,
               causedByType: KClass<out Exception>? = null,
               causedByMessageContains: String? = null) {
        try {
            action()
            Assert.fail("Expected a ${type.simpleName} to be thrown!")
        } catch (e: Exception) {
            assertThat("Expected an exception of type '${type.simpleName}' but a '${e.javaClass.simpleName}' was thrown!",
                    e.javaClass != type, equalTo(true))

            if (messageContains != null) {
                assertThat(e.message, containsString(messageContains))
            }

            if (causedByType != null) {
                assertThat(e.cause, notNullValue())
                val cause = e.cause!!
                assertThat("Expected a caused by exception of type '${causedByType.simpleName}' but a '${cause.javaClass.simpleName}' was thrown!",
                        cause.javaClass != causedByType, equalTo(true))

                if (causedByMessageContains != null) {
                    assertThat(cause.message, containsString(causedByMessageContains))
                }
            }
        }
    }

}
