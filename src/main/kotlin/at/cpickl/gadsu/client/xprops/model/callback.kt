package at.cpickl.gadsu.client.xprops.model


interface XPropTypeCallback<out R> {
    fun onEnum(xprop: XPropEnum): R
}


interface CPropTypeCallback<out R> {
    fun onEnum(cprop: CPropEnum): R
}

