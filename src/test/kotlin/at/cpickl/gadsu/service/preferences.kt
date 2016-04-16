package at.cpickl.gadsu.service

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.awt.Dimension
import java.awt.Point
import java.util.prefs.Preferences

@Test class JavaPrefsTest {

    private val testWindowDescriptor = WindowDescriptor(Point(1, 2), Dimension(3, 4))

    private val prefNode = javaClass
    private var testee = JavaPrefs(prefNode)

    @BeforeMethod
    fun initTestee() {
        Preferences.userNodeForPackage(prefNode).clear()
        testee = JavaPrefs(prefNode)
    }

    fun windowDescriptor_nullAtStartup() {
        assertThat(testee.windowDescriptor, nullValue())
        testee.windowDescriptor = testWindowDescriptor
    }

    fun windowDescriptor_getAndSetAgain() {
        testee.windowDescriptor = testWindowDescriptor
        assertThat(testee.windowDescriptor, equalTo(testWindowDescriptor))
    }

}
