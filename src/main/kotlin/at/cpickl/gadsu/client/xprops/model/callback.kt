package at.cpickl.gadsu.client.xprops.model


interface XPropTypeCallback<R> {
    fun onEnum(xprop: XPropEnum): R
}


interface CPropTypeCallback<R> {
    fun onEnum(cprop: CPropEnum): R
}

