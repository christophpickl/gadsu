package at.cpickl.gadsu.mail

import at.cpickl.gadsu.UserEvent

class RequestPrepareMailEvent : UserEvent()

class RequestSendMailEvent : UserEvent()

class MailWindowClosedEvent(val shouldPersistState: Boolean) : UserEvent()
