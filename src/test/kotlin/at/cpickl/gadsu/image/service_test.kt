package at.cpickl.gadsu.image

import at.cpickl.gadsu.testinfra.PROFILE_PICTURE_CLASSPATH_1
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.testng.annotations.Test

@Test class ImageServiceImplTest {

    fun `scale image sunshine`() {
        val icon1 = PROFILE_PICTURE_CLASSPATH_1.readImageIconFromClasspath()

        val targetSize = ImageSize.LITTLE
        val actual = icon1.scale(targetSize)

        assertThat(actual.size(), equalTo(targetSize.toDimension()))
    }


}