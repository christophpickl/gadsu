package at.cpickl.gadsu.testinfra

import non_test.Framed
import org.testng.Assert
import java.awt.Component

object TestViewStarter {

    var componentToShow: Component? = null

    @JvmStatic
    fun main(args: Array<String>) {
        if (componentToShow == null) {
            Assert.fail("TestViewStarter.componentToShow (static var) must be set first before using it in MainClassAdapter!")
        }
        Framed.show(componentToShow!!, null)
    }
}
