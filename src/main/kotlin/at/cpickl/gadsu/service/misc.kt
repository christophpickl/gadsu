package at.cpickl.gadsu.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.UUID

fun LOG(javaClass: Class<Any>): Logger {
    return LoggerFactory.getLogger(javaClass.nameAopCleaned)
}

private val Class<Any>.nameAopCleaned: String get() = if (name.contains("$$")) name.substring(0, name.indexOf("$$")) else name


interface HasId {
    val id: String?
}

interface IdGenerator {
    fun generate(): String
}

class UuidGenerator : IdGenerator {
    override fun generate(): String = UUID.randomUUID().toString()
}

val LOG_Closeable = LoggerFactory.getLogger(Closeable::class.java)
fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        LOG_Closeable.warn("Could not close '${this}'!", e)
    }
}

