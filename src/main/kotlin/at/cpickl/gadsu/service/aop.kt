package at.cpickl.gadsu.service

import com.google.inject.AbstractModule
import com.google.inject.matcher.AbstractMatcher
import com.google.inject.matcher.Matchers
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import java.lang.reflect.Method

class AopModule : AbstractModule() {
    override fun configure() {
        bindInterceptor(Matchers.annotatedWith(Logged::class.java), OnSubscribeMethodMatcher(), LoggedAspect())
    }
}

/**
 * ATTENTION: Classes (and their methods) must be declared to be "open" (non-final) in order to work with guice AOP.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Logged
// FIXME do same for custom transactional annotation

class OnSubscribeMethodMatcher : AbstractMatcher<Method>() {
    override fun matches(method: Method): Boolean {
        val methodName = method.name
        return methodName.startsWith("on")
    }

}

class LoggedAspect : MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        val log = LOG(invocation.`this`.javaClass)
        if (log.isDebugEnabled) {
            // will be logged by the AllMightyEventCatcher anyway, and leads to event.toStrings from multiple subscribers!
//            log.debug("{}(event={})", invocation.method.name, invocation.arguments[0])
            log.debug("{}(event)", invocation.method.name, invocation.arguments)
        }
        return invocation.proceed()
    }


}
