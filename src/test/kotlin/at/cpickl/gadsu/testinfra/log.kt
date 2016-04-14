package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.service.BaseLogConfigurator
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestNGListener
import org.testng.ITestResult
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test


@Test
class TestLogger : BaseLogConfigurator() {

    // MINOR springramework still logs (using JDK logging most likely)... reroute it!
    override fun configureInternal(logger: ch.qos.logback.classic.Logger) {
        logger.level = Level.ALL
        changeLevel("org.springframework", Level.WARN)
        logger.addAppender(consoleAppender("Gadsu-ConsoleAppender"))
    }

    @BeforeSuite
    fun initLogging() {
        configureLog()
    }

}

// MINOR improve solution: http://testng.org/doc/documentation-main.html#testng-listeners
//@Test @Listeners(LogTestListener::class)

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
