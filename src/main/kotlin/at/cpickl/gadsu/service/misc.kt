package at.cpickl.gadsu.service

import java.util.*

interface IdGenerator {
    fun generate(): String
}

class UuidGenerator : IdGenerator {
    override fun generate(): String = UUID.randomUUID().toString()
}
