package at.cpickl.gadsu.view

object TestMacHandler : MacHandler {
    override fun isEnabled() = false
    override fun registerAbout(onAbout: () -> Unit) { }
    override fun registerPreferences(onPreferences: () -> Unit) { }
    override fun registerQuit(onQuit: () -> Unit) { }
}
