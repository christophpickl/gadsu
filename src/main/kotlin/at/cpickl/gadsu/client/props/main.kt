package at.cpickl.gadsu.client.props


interface Prop {

}

data class StringProp(val value: String) : Prop
data class MultiEnumProp(val entries: List<String>) : Prop {
    //    val activeEntries = entries.filterValues { it == true }.keys.toList()
}

data class ClientProps(val properties: Map<String, Prop>) {
    companion object {
        val empty: ClientProps get() = ClientProps(emptyMap())
    }
}
