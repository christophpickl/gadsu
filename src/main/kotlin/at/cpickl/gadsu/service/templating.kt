package at.cpickl.gadsu.service

import at.cpickl.gadsu.GadsuException
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.StringReader
import java.io.StringWriter

interface TemplatingEngine {

    /**
     * @throws FreemarkerInvalidReferenceException if templateText contained variable not contained in data
     */
    fun process(templateText: String, data: Map<String, Any>): String

}

class FreemarkerTemplatingEngine : TemplatingEngine {
    private val log = LOG(javaClass)

    companion object {
        // http://freemarker.org/docs/pgui_quickstart_createconfiguration.html
        val configuration: Configuration by lazy {
            Configuration(Configuration.VERSION_2_3_25).apply {
                defaultEncoding = "UTF-8"
            logTemplateExceptions = false
            templateExceptionHandler = freemarker.template.TemplateExceptionHandler.RETHROW_HANDLER
//                setSharedVariable("someGadsuVar", "foobar")
            }
        }
    }

    override fun process(templateText: String, data: Map<String, Any>): String {
        log.debug("process(..)")
        val reader = StringReader(templateText)
        val template = Template("myTemplate", reader, configuration)
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
