package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.UserEvent


class SelectClientTab(val tab: ClientTabType) : UserEvent() {
    override fun toString(): String{
        return "SelectClientTab(tab=$tab)"
    }
}