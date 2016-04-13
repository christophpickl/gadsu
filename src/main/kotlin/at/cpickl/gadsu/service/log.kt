package at.cpickl.gadsu.service

import at.cpickl.gadsu.Development
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.InfoStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object LogConfigurator {

    fun configure() {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val status = context.statusManager
        status.add(InfoStatus("Setting up Gadsu log configuration.", context))

        val logger = context.getLogger(Logger.ROOT_LOGGER_NAME)
        logger.detachAndStopAllAppenders()
        logger.level = Level.ALL
        context.getLogger("org.apache").level = Level.WARN
        context.getLogger("org.hibernate").level = Level.WARN
        context.getLogger("org.jboss").level = Level.WARN

        // TODO introduce some development switch to change log config
        if (Development.ENABLED) {
            println("Develop LOG enabled.")
            logger.addAppender(consoleAppender(context))
        } else {
            // TODO also add console appender, but with level WARN for all
            logger.addAppender(fileAppender(context))
        }
    }

    private fun consoleAppender(context: LoggerContext): Appender<ILoggingEvent> {
        val appender = ConsoleAppender<ILoggingEvent>()
        appender.context = context
        appender.name = "MyShiatsu-ConsoleAppender"
        appender.encoder = defaultPatternLayout(context)
        appender.start()
        return appender
    }

    private fun fileAppender(context: LoggerContext): Appender<ILoggingEvent> {
        val appender = RollingFileAppender<ILoggingEvent>()
        appender.file = "myshiatsu.log"

        // http://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy
        // http://www.programcreek.com/java-api-examples/index.php?api=ch.qos.logback.core.rolling.TimeBasedRollingPolicy
        val policy = TimeBasedRollingPolicy<ILoggingEvent>()
        policy.context = context
        policy.setParent(appender)
        policy.fileNamePattern = "myshiatsu-%d{yyyy_MM_dd}.log"
        policy.maxHistory = 14 // two weeks
        policy.start()

        appender.rollingPolicy = policy
        //        appender.setAppend(true)
        //        appender.setTriggeringPolicy()
        appender.context = context
        appender.name = "MyShiatsu-FileAppender"
        appender.encoder = defaultPatternLayout(context)
        appender.start()
        return appender
    }

    private fun defaultPatternLayout(context: LoggerContext): PatternLayoutEncoder {
        val layout = PatternLayoutEncoder()
        layout.context = context
        // layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n")
        layout.pattern = "%-32(%d{HH:mm:ss.SSS} [%thread]) [%-5level] %logger{42} - %msg%n"
        layout.start()
        return layout
    }

}
