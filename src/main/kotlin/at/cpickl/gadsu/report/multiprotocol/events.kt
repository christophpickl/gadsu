package at.cpickl.gadsu.report.multiprotocol

import at.cpickl.gadsu.AppEvent
import at.cpickl.gadsu.UserEvent


class RequestCreateMultiProtocolEvent() : UserEvent()

/**
 * Creates the PDF and stores entries in DB (mark treatments as yet protcolized).
 */
class ReallyCreateMultiProtocolEvent(val description: String) : UserEvent()

/**
 * Just to have a quick look at the generated PDF.
 */
class TestCreateMultiProtocolEvent() : UserEvent()

class MultiProtocolGeneratedEvent: AppEvent()
