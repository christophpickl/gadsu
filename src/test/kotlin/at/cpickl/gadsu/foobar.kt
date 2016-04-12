package at.cpickl.gadsu

import at.cpickl.gadsu.service.RealClock
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.Test

@Test class DummyTest {

    fun dummyTestMethod() {
        MatcherAssert.assertThat(2 + 2, Matchers.equalTo(4))
    }

    fun dummyClockTest() {
        RealClock().now()
    }

}
