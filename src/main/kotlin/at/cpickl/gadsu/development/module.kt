package at.cpickl.gadsu.development

class DevelopmentModule : com.google.inject.AbstractModule() {
    override fun configure() {

        bind(DevWindow::class.java).asEagerSingleton()
        bind(DevelopmentController::class.java).asEagerSingleton()


    }
}
