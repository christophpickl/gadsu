package at.cpickl.gadsu.client

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.UserEvent
import com.google.common.base.MoreObjects

/**
 * Request the form to reset in order to enter client data.
 */
class CreateNewClientEvent : UserEvent()

/**
 * Save button clicked means either insert or update.
 */
class SaveClientEvent : UserEvent()

class ClientCreatedEvent(val client: Client) : AppEvent() {
    init {
        if (!client.yetPersisted) {
            throw GadsuException("Can not dispatch created client event with a client which was not yet persisted: $client")
        }
    }
}

class ClientUpdatedEvent(val client: Client) : AppEvent()

class DeleteClientEvent(val client: Client) : UserEvent() {
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("client", client)
                .toString()
    }
}

class ClientDeletedEvent(val client: Client) : AppEvent()

/**
 * Master list selected.
 */
class ClientSelectedEvent(val client: Client) : UserEvent() {
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("client", client)
                .toString()
    }
}
