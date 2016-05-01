package at.cpickl.gadsu.client.xprops.model

import at.cpickl.gadsu.service.verifyNoIntersection
import java.util.HashMap

data class CProps(private val props: Map<XProp, CProp>) {
    companion object {
        val empty: CProps get() = CProps(emptyMap())
    }

    fun findOrNull(xprop: XProp): CProp? {
        return props[xprop]
    }

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
}

interface CProp : XProp {
    val delegate: XProp
    val clientValue: Any
    fun <R> onType(callback: CPropTypeCallback<R>): R

}

data class CPropEnum(
        override val delegate: XPropEnum,
        override val clientValue: List<XPropEnumOpt>
) : CProp, XProp by delegate {
    override fun <R> onType(callback: CPropTypeCallback<R>) = callback.onEnum(this)
}
