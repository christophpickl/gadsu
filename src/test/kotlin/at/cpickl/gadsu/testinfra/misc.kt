package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.toMyImage
import org.testng.SkipException

// https://docs.travis-ci.com/user/environment-variables/#Default-Environment-Variables
val IS_TRAVIS: Boolean = System.getProperty("user.name", "").equals("travis")
// does NOT work!!! System.getProperty("TRAVIS", "").equals("true")


val PROFILE_PICTURE_CLASSPATH_1 = "/gadsu_test/profile_pic-valid_man1.jpg"
val PROFILE_PICTURE_CLASSPATH_2 = "/gadsu_test/profile_pic-valid_man2.jpg"


val MyImage.Companion.TEST_CLIENT_PIC1: MyImage get() = PROFILE_PICTURE_CLASSPATH_1.toMyImage()
val MyImage.Companion.TEST_CLIENT_PIC2: MyImage get() = PROFILE_PICTURE_CLASSPATH_2.toMyImage()


fun skip(reason: String) {
    throw SkipException(reason)
}
