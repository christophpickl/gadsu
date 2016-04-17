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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClientCreatedEvent) return false
        if (client != other.client) return false
        return true
    }
    override fun hashCode(): Int {
        return client.hashCode()
    }
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("client", client)
                .toString()
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
class ClientSelectedEvent(val client: Client, val previousSelected: Client?) : UserEvent() {

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other !is ClientSelectedEvent) return false
        if (client != other.client) return false
        if (previousSelected != other.previousSelected) return false
        return true
    }
    override fun hashCode(): Int{
        var result = client.hashCode()
        result += 31 * result + (previousSelected?.hashCode() ?: 0)
        return result
    }
    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("client", client)
                .add("previousSelected", previousSelected)
                .toString()
    }

}

/**
 * Happens when selected client has been deleted.
 */
class ClientUnselectedEvent(val client: Client) : UserEvent()


/**
 * Back button in treatments view.
 */
class ShowClientViewEvent() : AppEvent()
