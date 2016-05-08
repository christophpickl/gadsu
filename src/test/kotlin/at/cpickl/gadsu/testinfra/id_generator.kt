package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.IdGenerator
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers


class SimpleTestableIdGenerator(_id: String? = null) : IdGenerator {
    var id = _id ?: TEST_UUID1
    override fun generate() = id
}

class ListTestableIdGenerator(private val ids: Array<String>, private val cycleThrough: Boolean = false) : IdGenerator {
    private var currentIndex = 0

    override fun generate(): String {
        val nextId = ids[currentIndex++]
        if (cycleThrough && currentIndex == ids.size) {
            currentIndex = 0 // reset generated ID
        }
        return nextId
    }

    fun assertAllConsumed() {
        MatcherAssert.assertThat(currentIndex, Matchers.equalTo(ids.size))
    }
}

class SequencedTestableIdGenerator() : IdGenerator {
    private var sequence = 1
    override fun generate() = sequence++.toString()
}
