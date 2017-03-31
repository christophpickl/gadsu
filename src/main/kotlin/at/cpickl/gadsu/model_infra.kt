package at.cpickl.gadsu

import java.util.Collections


interface Ordered {
    val order: Int
}
fun <T : Ordered> orderedValuesOf(unorderedArray: Array<T>): List<T> {
    val unordered = unorderedArray.toList()
    Collections.sort(unordered) { f1, f2 -> f1.order.compareTo(f2.order) }
    return unordered
}


interface SqlEnum {
    val sqlCode: String
}
fun <E : SqlEnum> parseSqlCodeFor(values: Array<E>, search: String): E {
    return values.firstOrNull { it.sqlCode == search } ?:
            throw GadsuException("Unhandled SQL code: '$search'!")
}
