package at.cpickl.gadsu.mail.bulkmail

import at.cpickl.gadsu.global.UserEvent

class RequestPrepareBulkMailEvent : UserEvent()

class RequestSendBulkMailEvent : UserEvent()

class BulkMailWindowClosedEvent(val shouldPersistState: Boolean) : UserEvent()
