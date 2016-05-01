package at.cpickl.gadsu.client.xprops.model

import com.google.common.collect.ComparisonChain


interface XProp {
    val key: String
    val label: String
    // value: T
    fun <R> onType(callback: XPropTypeCallback<R>): R
}


data class XPropEnum(
        override val key: String,
        override val label: String,
        val options: List<XPropEnumOpt>
) : XProp {
    override fun <R> onType(callback: XPropTypeCallback<R>) = callback.onEnum(this)
}

// TODO rewrite, define order by order in code
data class XPropEnumOpt(
        val order: Int,
        val key: String,
        val label: String
        //val label: String = key
) : Comparable<XPropEnumOpt> {
    override fun compareTo(other: XPropEnumOpt): Int {
        return ComparisonChain.start()
                .compare(this.order, other.order)
                .result()
    }

}

