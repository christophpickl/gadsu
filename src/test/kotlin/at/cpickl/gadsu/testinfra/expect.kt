package at.cpickl.gadsu.testinfra

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.testng.Assert
import kotlin.reflect.KClass

object Expects {

    fun expect(type: KClass<out Exception>, messageContains: String, action: () -> Unit) {
        try {
            action()
            Assert.fail("Expected a ${type.simpleName} to be thrown!")
        } catch (e: Exception) {
            assertThat("Expected an exception of type '${type.simpleName}' but a '${e.javaClass.simpleName}' was thrown!",
                    e.javaClass != type, equalTo(true))
            assertThat(e.message, containsString(messageContains))
        }
    }

}
