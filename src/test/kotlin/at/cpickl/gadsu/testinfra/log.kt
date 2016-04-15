package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.BaseLogConfigurator
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestNGListener
import org.testng.ITestResult
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Listeners
import org.testng.annotations.Test


@Test
@Listeners(LogTestListener::class)
class TestLogger : BaseLogConfigurator() {

    // MINOR springramework still logs (using JDK logging most likely)... reroute it!
    override fun configureInternal(logger: ch.qos.logback.classic.Logger) {
        logger.level = Level.ALL

        arrayOf(
                "org.apache",
                "org.springframework",
                "org.flywaydb"
        ).forEach { changeLevel(it, Level.WARN) }
        logger.addAppender(consoleAppender("Gadsu-ConsoleAppender", "%d{HH:mm:ss} [%-5level] %logger{42} - %msg%n"))
    }

    @BeforeSuite
    fun initLogging() {
        configureLog()
    }

}

class LogTestListener :  ITestNGListener, ITestListener {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onTestStart(result: ITestResult) {
        logTest("START", result)
    }

    override fun onTestSuccess(result: ITestResult) {
        logTest("SUCCESS", result)
    }

    override fun onTestSkipped(result: ITestResult) {
        // copy and paste ;)
        log.warn("======> {} - {}#{}", "SKIP", result.testClass.realClass.simpleName, result.method.methodName)
    }

    override fun onTestFailure(result: ITestResult) {
        logTest("FAIL", result)
    }


    override fun onStart(context: ITestContext) {
        log.info("Test Suite STARTED")
    }

    override fun onFinish(context: ITestContext) {
        log.info("Test Suite FINISHED")
    }

    override fun onTestFailedButWithinSuccessPercentage(result: ITestResult) { }

    private fun logTest(label: String, result: ITestResult) {
        log.info("======> {} - {}#{}", label, result.testClass.realClass.simpleName, result.method.methodName)
    }

}
