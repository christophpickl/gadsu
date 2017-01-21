package at.cpickl.gadsu.service

import java.util.UUID


interface HasId {
    val id: String?
}

interface IdGenerator {
    fun generate(): String
}

class UuidGenerator : IdGenerator {
    override fun generate(): String = UUID.randomUUID().toString()
}

