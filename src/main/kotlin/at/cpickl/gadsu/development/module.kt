package at.cpickl.gadsu.development

class DevelopmentModule : com.google.inject.AbstractModule() {
    override fun configure() {

        bind(DevelopmentController::class.java).asEagerSingleton()

    }
}
