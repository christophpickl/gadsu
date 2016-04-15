package at.cpickl.gadsu.service

import at.cpickl.gadsu.Development
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.status.InfoStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseLogConfigurator {

    protected val defaultPattern = "%-43(%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread]) [%-5level] %logger{42} - %msg%n"
    protected val context: LoggerContext
    init {
        context = LoggerFactory.getILoggerFactory() as LoggerContext
    }

    protected fun consoleAppender(name: String,
                                  pattern: String = defaultPattern,
                                  withAppender: ((ConsoleAppender<ILoggingEvent>) -> Unit)? = null): Appender<ILoggingEvent> {
        val appender = ConsoleAppender<ILoggingEvent>()
        appender.context = context
        appender.name = name
        appender.encoder = patternLayout(pattern)
        if (withAppender != null) {
            withAppender(appender)
        }
        appender.start()
        return appender
    }

    protected fun fileAppender(name: String, filename: String, filenamePattern: String): Appender<ILoggingEvent> {
        val appender = RollingFileAppender<ILoggingEvent>()
        appender.file = filename

        // http://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy
        // http://www.programcreek.com/java-api-examples/index.php?api=ch.qos.logback.core.rolling.TimeBasedRollingPolicy
        val policy = TimeBasedRollingPolicy<ILoggingEvent>()
        policy.context = context
        policy.setParent(appender)
        policy.fileNamePattern = filenamePattern
        policy.maxHistory = 14 // two weeks
        policy.start()

        appender.rollingPolicy = policy
        //        appender.setAppend(true)
        //        appender.setTriggeringPolicy()
        appender.context = context
        appender.name = name
        appender.encoder = patternLayout()
        appender.start()
        return appender
    }

    // or: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    protected fun patternLayout(pattern: String = defaultPattern): PatternLayoutEncoder {
        val layout = PatternLayoutEncoder()
        layout.context = context
        layout.pattern = pattern
        layout.start()
        return layout
    }

    fun configureLog() {
        val status = context.statusManager
        status.add(InfoStatus("Setting up log configuration.", context))

        val logger = context.getLogger(Logger.ROOT_LOGGER_NAME)
        logger.detachAndStopAllAppenders()

        configureInternal(logger)
    }

    abstract protected fun configureInternal(logger: ch.qos.logback.classic.Logger)

    protected fun changeLevel(packageName: String, level: ch.qos.logback.classic.Level) {
        context.getLogger(packageName).level = level
    }
}

class LogConfigurator(private val debugEnabled: Boolean) : BaseLogConfigurator() {

    override fun configureInternal(logger: ch.qos.logback.classic.Logger) {
        logger.level = if (debugEnabled || Development.ENABLED) Level.ALL else Level.DEBUG

        changeLevel("org.apache", Level.WARN)
        changeLevel("org.springframework", Level.WARN)

        if (Development.ENABLED) {
            logger.addAppender(consoleAppender("Gadsu-Dev-ConsoleAppender", "%-27(%d{HH:mm:ss} [%thread]) [%-5level] %logger{30} - %msg%n"))
        } else {
            if (debugEnabled) {
                logger.addAppender(consoleAppender("Gadsu-ConsoleAppender"))
            } else {
                logger.addAppender(consoleAppender("Gadsu-ConsoleAppender", withAppender = { appender ->
                    appender.addFilter(ThresholdFilter(Level.WARN))
                }))

            }
            logger.addAppender(fileAppender("Gadsu-FileAppender", "gadsu.log", "gadsu-%d{yyyy_MM_dd}.log"))
        }
    }

}

private class ThresholdFilter(private val level: Level) : Filter<ILoggingEvent>() {
    init {
        start()
    }
    override fun decide(event: ILoggingEvent): FilterReply {
        if (event.level.isGreaterOrEqual(level)) {
            return FilterReply.ACCEPT
        }
        return FilterReply.DENY
    }

}
