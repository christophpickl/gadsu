package at.cpickl.gadsu.treatment.dyn.treats

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.testng.annotations.Test

@Test class AllTonguePropertableTest {

    fun `check for duplicate SQL codes`() {
        val colors = TongueProperty.Color.values().map { it.sqlCode }
        val shapes = TongueProperty.Shape.values().map { it.sqlCode }
        val coats = TongueProperty.Coat.values().map { it.sqlCode }
        val specials = TongueProperty.Special.values().map { it.sqlCode }

        val all = mutableSetOf<String>()
        val duplicates = mutableSetOf<String>()

        all.addAll(colors)

        duplicates.addAll(shapes.filter { all.contains(it) })
        all.addAll(shapes)

        duplicates.addAll(coats.filter { all.contains(it) })
        all.addAll(coats)

        duplicates.addAll(specials.filter { all.contains(it) })

        assertThat(duplicates, empty())
    }

}
