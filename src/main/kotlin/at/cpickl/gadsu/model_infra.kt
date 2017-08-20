package at.cpickl.gadsu

import java.util.Collections


interface Labeled {
    val label: String
}

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

abstract class EnumBase<out T>(
        private val values: Array<T>
) where T : Ordered, T : SqlEnum {
    val orderedValues: List<T> = orderedValuesOf(values)
    fun parseSqlCode(search: String) = parseSqlCodeFor(values, search)
}
