package at.cpickl.gadsu

abstract class Event

/**
 * Dispatched internally by the application.
 */
abstract class AppEvent : Event()

/**
 * Always triggered by the user (running on the UI dispatch thread).
 */
abstract class UserEvent : Event()

class AppStartupEvent : AppEvent()

/**
 * Check for unsaved changes.
 */
class QuitAskEvent : UserEvent()

class QuitEvent : UserEvent()
