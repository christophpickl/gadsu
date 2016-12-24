package at.cpickl.gadsu.tcm

import com.google.inject.AbstractModule

class TcmModule : AbstractModule() {
    override fun configure() {
        bind(ElementsTableController::class.java).asEagerSingleton()
    }
}
