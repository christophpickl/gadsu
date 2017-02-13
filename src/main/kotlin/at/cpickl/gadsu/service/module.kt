package at.cpickl.gadsu.service

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.slf4j.LoggerFactory

class ServiceModule() : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.trace("configure()")

        bind(Clock::class.java).to(RealClock::class.java).`in`(Scopes.SINGLETON)
        bind(GoogleConnector::class.java).to(GoogleConnectorImpl::class.java).`in`(Scopes.SINGLETON)
        bind(IdGenerator::class.java).to(UuidGenerator::class.java).`in`(Scopes.SINGLETON)

        bind(MetaInf::class.java).toProvider(MetaInfLoader::class.java).`in`(Scopes.SINGLETON)

        bind(WebPageOpener::class.java).to(SwingWebPageOpener::class.java).asEagerSingleton() // talks only via event bus

        bind(FileSystem::class.java).to(FileSystemImpl::class.java).`in`(Scopes.SINGLETON)

        bind(TemplatingEngine::class.java).to(FreemarkerTemplatingEngine::class.java).`in`(Scopes.SINGLETON)

        install(CurrentModule())
        install(InternetConnectionModule())
    }
}
