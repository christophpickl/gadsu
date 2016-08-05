package at.cpickl.gadsu.service

import at.cpickl.gadsu.GADSU_DIRECTORY
import at.cpickl.gadsu.development.Development
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.rolling.TriggeringPolicyBase
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.status.InfoStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val GADSU_LOG_FILE = File(GADSU_DIRECTORY, "gadsu.log")

abstract class BaseLogConfigurator {

    protected val defaultPattern = "%-43(%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread]) [%-5level] %logger{42} - %msg%n"
    protected val context: LoggerContext
    private var yetConfigured = false

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
        val rollingPolicy = TimeBasedRollingPolicy<ILoggingEvent>()
        rollingPolicy.context = context
        rollingPolicy.setParent(appender)
        rollingPolicy.fileNamePattern = filenamePattern
        rollingPolicy.maxHistory = 14 // two weeks
        rollingPolicy.start()
        appender.rollingPolicy = rollingPolicy

        val triggeringPolicy = RollOncePerSessionTriggeringPolicy<ILoggingEvent>()
        triggeringPolicy.start()
        appender.triggeringPolicy = triggeringPolicy
        appender.isAppend = true
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
        if (yetConfigured) {
            println("Log configurator '${javaClass.simpleName}' has been already activated. " +
                    "(Usually happens because of manually enabling log in tests and by testng suites)")
            return
        }
        yetConfigured = true

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

        arrayOf(
                "org.apache",
                "org.springframework",
                "org.flywaydb",
                "net.sf.jasperreports"
        ).forEach { changeLevel(it, Level.WARN) }

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
            logger.addAppender(fileAppender("Gadsu-FileAppender",
                    GADSU_LOG_FILE.absolutePath,
                    File(GADSU_DIRECTORY, "gadsu-%d{yyyy_MM_dd}.log.zip").absolutePath
            ))
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

// http://stackoverflow.com/questions/2492022/how-to-roll-the-log-file-on-startup-in-logback
private class RollOncePerSessionTriggeringPolicy<E> : TriggeringPolicyBase<E>() {
    private var doRolling: Boolean = true
    override fun isTriggeringEvent(activeFile: File, event: E): Boolean {
        // roll the first time when the event gets called
        if (doRolling) {
            doRolling = false
            return true
        }
        return false
    }
}
