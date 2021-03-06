package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.global.AppEvent
import at.cpickl.gadsu.global.UserEvent


/**
 * User requests to select new tab (via shortcut).
 */
class SelectClientTab(val tab: ClientTabType) : UserEvent() {
    override fun toString(): String{
        return "SelectClientTab(tab=$tab)"
    }
}

/**
 * Tab has changed.
 */
class ClientTabSelected(val tab: ClientTab) : AppEvent()
