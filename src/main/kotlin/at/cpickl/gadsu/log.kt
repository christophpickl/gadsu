package at.cpickl.gadsu

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.InfoStatus
import org.slf4j.LoggerFactory


object LogConfigurator {

    fun configure() {
        val context = LoggerFactory.getILoggerFactory() as LoggerContext
        val status = context.getStatusManager()
        status.add(InfoStatus("Setting up MyShiatsu PROD configuration.", context))

        val logger = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        logger.detachAndStopAllAppenders()
        logger.setLevel(Level.ALL)
        context.getLogger("org.apache").setLevel(Level.WARN)
        context.getLogger("org.hibernate").setLevel(Level.WARN)
        context.getLogger("org.jboss").setLevel(Level.WARN)

        // TODO introduce some development switch to change log config
//        if (Develop.ENABLED) {
            println("Develop LOG enabled.")
            logger.addAppender(consoleAppender(context))
//        } else {
            // in future:
            // * also add console appender, but with level WARN for all
//            logger.addAppender(fileAppender(context))
//        }
    }

    private fun consoleAppender(context: LoggerContext): Appender<ILoggingEvent> {
        val appender = ConsoleAppender<ILoggingEvent>()
        appender.setContext(context)
        appender.setName("MyShiatsu-ConsoleAppender")
        appender.setEncoder(defaultPatternLayout(context))
        appender.start()
        return appender
    }

    private fun fileAppender(context: LoggerContext): Appender<ILoggingEvent> {
        val appender = RollingFileAppender<ILoggingEvent>()
        appender.setFile("myshiatsu.log")

        // http://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy
        // http://www.programcreek.com/java-api-examples/index.php?api=ch.qos.logback.core.rolling.TimeBasedRollingPolicy
        val policy = TimeBasedRollingPolicy<ILoggingEvent>()
        policy.setContext(context)
        policy.setParent(appender)
        policy.setFileNamePattern("myshiatsu-%d{yyyy_MM_dd}.log")
        policy.setMaxHistory(14) // two weeks
        policy.start()

        appender.setRollingPolicy(policy)
        //        appender.setAppend(true)
        //        appender.setTriggeringPolicy()
        appender.setContext(context)
        appender.setName("MyShiatsu-FileAppender")
        appender.setEncoder(defaultPatternLayout(context))
        appender.start()
        return appender
    }

    private fun defaultPatternLayout(context: LoggerContext): PatternLayoutEncoder {
        val layout = PatternLayoutEncoder()
        layout.setContext(context)
        // layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n")
        layout.setPattern("%-32(%d{HH:mm:ss.SSS} [%thread]) [%-5level] %logger{42} - %msg%n")
        layout.start()
        return layout
    }

}
