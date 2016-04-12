package at.cpickl.gadsu

abstract class Event {}

abstract class UserEvent : Event() {}

class QuitUserEvent : UserEvent() {}

