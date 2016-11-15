package at.cpickl.gadsu.treatment.dyn.treats

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class MeridianAndPositionFactoryTest {

    fun `bySqlCodes matches all possible values`() {
        MeridianAndPosition.values().forEach {
            assertThat(MeridianAndPositionFactory.bySqlCodes(it.meridian.sqlCode, it.position.sqlCode),
                    equalTo(it))
        }
    }

}
