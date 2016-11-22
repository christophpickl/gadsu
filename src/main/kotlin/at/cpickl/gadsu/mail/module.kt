package at.cpickl.gadsu.mail

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.slf4j.LoggerFactory

class MailModule : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.debug("configure()")

        bind(GMailServerConector::class.java).to(GMailServerConectorImpl::class.java).`in`(Scopes.SINGLETON)
        bind(MailSender::class.java).to(GMailSender::class.java).`in`(Scopes.SINGLETON)

        bind(MailView::class.java).to(MailSwingView::class.java).`in`(Scopes.SINGLETON)
        bind(MailController::class.java).asEagerSingleton()

    }

}
