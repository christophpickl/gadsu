package at.cpickl.gadsu.mail

import at.cpickl.gadsu.development.Development
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.slf4j.LoggerFactory

class MailModule : AbstractModule() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun configure() {
        log.debug("configure()")

        bind(GMailApi::class.java).to(GMailApiImpl::class.java).`in`(Scopes.SINGLETON)

        if (Development.MOCKMAIL_ENABLED) {
            log.warn("MockMail enabled. Not sending mails.")
            bind(MailSender::class.java).to(MockMailSender::class.java).`in`(Scopes.SINGLETON)
        } else {
            bind(MailSender::class.java).to(MailSenderImpl::class.java).`in`(Scopes.SINGLETON)
        }

        bind(AppointmentConfirmationer::class.java).to(AppointmentConfirmationerImpl::class.java).`in`(Scopes.SINGLETON)

        bind(MailView::class.java).to(MailSwingView::class.java).`in`(Scopes.SINGLETON)
        bind(MailController::class.java).asEagerSingleton()

    }

}
