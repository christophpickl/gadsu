package at.cpickl.gadsu.service

//class Serializer(private val bytes: ByteArray) {
//    fun asString() = Base64.getEncoder().encodeToString(bytes)
//    fun asFile(target: File) {
//        asString().saveToFile(target)
//    }
//}
//
//class Deserializer {
//    fun <T> byString(deserialized: String) = deserialized.deserialize() as T
//}
//
//fun String.deserialize(): Any  {
//    val data = Base64.getDecoder().decode(this)
//    val ois = ObjectInputStream(ByteArrayInputStream(data))
//    val o = ois.readObject()
//    ois.close()
//    return o
//}
//
//fun Any.serialize(): Serializer {
//    val baos = ByteArrayOutputStream()
//    val oos = ObjectOutputStream(baos)
//    oos.writeObject(this)
//    oos.close()
//    return Serializer(baos.toByteArray())
//}
