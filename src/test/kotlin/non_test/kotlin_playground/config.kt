package non_test.kotlin_playground

import non_test.kotlin_playground.ConfigFields.baseUrl
import non_test.kotlin_playground.ConfigFields.threshold
import java.net.URL


interface Config {
    fun <T> get(field: Field<T>): T
}

class InMemoryConfig : Config, InternalConfig {
    private val data = mapOf(
            "presto.port" to "80",
            "presto.host" to "localhost",
            "threshold" to "142"
    )

    override fun <T> get(field: Field<T>) =
            field.valueOf(this)

    override fun get(name: String) = data[name]
            ?: throw IllegalArgumentException(name)
}

interface InternalConfig {
    operator fun get(name: String): String
}
interface Field<out T> {
    fun valueOf(config: InternalConfig): T
    val name: String
}
open class IntField(override val name: String) : Field<Int> {
    override fun valueOf(config: InternalConfig) =
            config[name].toInt()
}
open class StringField(override val name: String) : Field<String> {
    override fun valueOf(config: InternalConfig) =
            config[name]
}
open class UrlField(final override val name: String) : Field<URL> {
    private val host = StringField("$name.host")
    private val port = IntField("$name.port")
    override fun valueOf(config: InternalConfig) =
            URL("https://${host.valueOf(config)}:${port.valueOf(config)}")
}

object ConfigFields {
    object threshold : IntField(name = "threshold")
    object baseUrl : UrlField(name = "presto")
}

fun main(args: Array<String>) {
    val config = InMemoryConfig()

    // and we call get and get directly the proper type
    val thresholdConfig: Int = config.get(threshold)
    val url: URL = config.get(baseUrl)
    println(thresholdConfig)
    println(url)
}


//
//interface Config {
//    fun <T> get(field: Field<T>): T
//}
//class InMemoryConfig : Config {
//    private val data = mapOf("port" to "80", "url" to "http://localhost")
//    override fun <T> get(field: Field<T>) =
//            data[field.name]?.toT(field) ?:
//                    throw IllegalArgumentException(field.name)
//    private fun <T> String.toT(field: Field<T>) = field.valueOf(this)
//}
//
//interface Field<out T> {
//    fun valueOf(rawValue: String): T
//    val name: String
//}
//open class IntField(override val name: String) : Field<Int> {
//    override fun valueOf(rawValue: String) = rawValue.toInt()
//}
//open class StringField(override val name: String) : Field<String> {
//    override fun valueOf(rawValue: String) = rawValue
//}
//open class UrlField(override val name: String) : Field<URL> {
//    override fun valueOf(rawValue: String) = URL()
//}
//
//object ConfigFields {
//    object port : IntField(name = "port")
//    object url : StringField(name = "url")
//}
//
//fun main(args: Array<String>) {
//    val config = InMemoryConfig()
//
//    val jettyPort: Int = config.get(port)
//    val jettyUrl: String = config.get(url)
//
//    println("$jettyUrl:$jettyPort")
//}
//
