package at.cpickl.gadsu.mail.bulkmail

import at.cpickl.gadsu.UserEvent

class RequestPrepareBulkMailEvent : UserEvent()

class RequestSendBulkMailEvent : UserEvent()

class BulkMailWindowClosedEvent(val shouldPersistState: Boolean) : UserEvent()
