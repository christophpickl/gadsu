package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.service.verifyNoIntersection
import at.cpickl.gadsu.tcm.model.IsEnumOption
import java.util.*

class CPropsBuilder {
    private val cprops = LinkedList<CProp>()

    fun add(xprop: XPropEnum, vararg selectedOpts: IsEnumOption): CPropsBuilder {
        cprops.add(CPropEnum(xprop, selectedOpts.map { it.opt }))
        return this
    }

    fun build(): CProps {
        // turn List<CProp> to Map<XProp, CProp> whereas the xprop is part of the cprop
        return CProps(cprops.associate { Pair(it.xprop, it) })
    }
}

data class CProps(private val props: Map<XProp, CProp>) {
    companion object {
        val empty: CProps get() = CProps(emptyMap())

        // eg: CProps.build(XProps.Sleep, XProps.SleepOpts.ProblemsFallAsleep)
        fun builder() = CPropsBuilder()

    }

    fun findOrNull(xprop: XProp): CProp? {
        return props[xprop]
    }

    /**
     * Useful when having xprop fields split among different views.
     */
    fun combine(that: CProps): CProps {
        this.props.verifyNoIntersection(that.props)
        val map = HashMap<XProp, CProp>()
        map.putAll(this.props)
        map.putAll(that.props)
        return CProps(map)
    }

    fun forEach(func: (CProp) -> Unit) {
        props.values.forEach(func)
    }
    fun <R> map(func: (CProp) -> R): List<R> {
        return props.values.map(func)
    }

    override fun toString(): String {
        val keysFlat = props.map {
            "${it.key.key}=${it.value.clientValue}"
        }.joinToString(", ")
        return "CProps(props(${props.size})=$keysFlat)"
    }

    fun isEmpty() = props.isEmpty()
}

interface CProp : XProp {
    val xprop: XProp
    val clientValue: Any
    val isClientValueSet: Boolean

    fun <R> onType(callback: CPropTypeCallback<R>): R

}

data class CPropEnum(
        override val xprop: XPropEnum,
        override val clientValue: List<XPropEnumOpt>
) : CProp, XProp by xprop {

    override val isClientValueSet = clientValue.isNotEmpty()

    override fun <R> onType(callback: CPropTypeCallback<R>) = callback.onEnum(this)

    override fun toString(): String {
        return "CPropEnum(clientValue=${clientValue.map { it.key }.joinToString(", ")})"
    }
}
