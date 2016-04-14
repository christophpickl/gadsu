package at.cpickl.gadsu.client

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent
import com.google.common.base.MoreObjects


class CreateNewClientEvent : UserEvent()

class ClientCreatedEvent(val client: Client) : AppEvent()

class SaveClientEvent : UserEvent()

class ClientSelectedEvent(val client: Client) : UserEvent() {
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("client", client)
                .toString()
    }
}
