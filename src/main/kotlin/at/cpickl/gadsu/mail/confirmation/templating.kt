package at.cpickl.gadsu.mail.confirmation

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.service.LOG
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.StringReader
import java.io.StringWriter


fun main(args: Array<String>) {
    val templateText = "hallo \${name} \${not}!"

    val root = mapOf("name" to "kotlin")

//    val template = Freemarker.configuration.getTemplate("test.ftlh")
//    val reader = StringReader(templateText)
//    val template = Template("myTemplate", reader, Freemarker.configuration)
//    val out = OutputStreamWriter(System.out)
//    template.process(root, out)
    println(Freemarker.process(templateText, root))
}

object Freemarker {
    private val log = LOG(javaClass)

    // http://freemarker.org/docs/pgui_quickstart_createconfiguration.html
    private val configuration: Configuration by lazy {
        Configuration(Configuration.VERSION_2_3_25).apply {
//            setDirectoryForTemplateLoading(File("/where/you/store/templates"))
            defaultEncoding = "UTF-8"
            logTemplateExceptions = false
            templateExceptionHandler = freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER
        }
    }

    fun process(templateText: String, data: Map<String, Any>): String {
        log.debug("process(..)")
//    val template = configuration.getTemplate("test.ftlh")
        val reader = StringReader(templateText)
        val template = Template("dynamic-emplate", reader, configuration)
        val writer = StringWriter()
        try {
            template.process(data, writer)
            return writer.toString()
        } catch(e: Exception) {
            throw FreemarkerInvalidReferenceException(
                    "The template text contained a reference which was not found in the parameter map.\n" +
                            "See the exception cause for details.\n" +
                            "template text: <<<$template>>>\n" +
                            "data: $data"
                    , e)
        }
    }

}

class FreemarkerInvalidReferenceException(message: String, cause: Exception) : GadsuException(message, cause)
