package at.cpickl.gadsu.service

import com.google.inject.AbstractModule
import com.google.inject.matcher.AbstractMatcher
import com.google.inject.matcher.Matchers
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.slf4j.LoggerFactory
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
        val log = LoggerFactory.getLogger(cleanClassName(invocation.`this`.javaClass))
        if (log.isDebugEnabled) {
            log.debug("{}(event={})", invocation.method.name, invocation.arguments[0])
        }
        return invocation.proceed()
    }

    private fun cleanClassName(clazz: Class<Any>): String {
        val name = clazz.name
        if (!name.contains("$$")) {
            return name
        }
        return name.substring(0, name.indexOf("$$"))
    }

}
