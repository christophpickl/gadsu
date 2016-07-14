package at.cpickl.gadsu.service

import at.cpickl.gadsu.AppStartupEvent
import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientRepository
import at.cpickl.gadsu.view.ViewNames
import com.google.common.eventbus.Subscribe
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

interface Suggester {
    fun suggest(viewName: String, currentText: String): String?
}

class SuggesterImpl @Inject constructor(
        private val clientRepo: ClientRepository
) : Suggester {

    private val log = LOG(javaClass)
    private val cachedValues: HashMap<String, List<String>> = HashMap()

    override fun suggest(viewName: String, currentText: String): String? {
        if (currentText.isEmpty()) {
            return null
        }
        val values: List<String>
        val maybeValues = cachedValues[viewName]
        if (maybeValues == null) {
            values = fetchDistinctValues(viewName)
            cachedValues[viewName] = values
        } else {
            values = maybeValues
        }

        return values.firstOrNull { it.startsWith(currentText) && it != currentText }
    }

    private fun fetchDistinctValues(viewName: String): List<String> {
        log.trace("fetchDistinctValues(viewName='{}')", viewName)
        val mapper: (Client) -> String = when (viewName) {
            ViewNames.Client.InputJob -> { { it.job } }
            ViewNames.Client.InputCountryOfOrigin -> { { it.countryOfOrigin } }
            ViewNames.Client.InputOrigin -> { { it.origin } }
            ViewNames.Client.InputChildren -> { { it.children } }
            ViewNames.Client.InputZipCode -> { { it.contact.zipCode } }
            ViewNames.Client.InputCity -> { { it.contact.city } }
            else -> throw IllegalArgumentException("Unsupported view name for auto suggestion: '$viewName'!")
        }
        return clientRepo.findAll().map(mapper).filter { it.isNotEmpty() }.distinct()
    }
}

interface SuggesterController {
    fun enableSuggestionsFor(vararg textFields: JTextField)
}

@Logged
open class SuggesterControllerImpl @Inject constructor(
        private val suggester: Suggester

) : SuggesterController {

    private val log = LOG(javaClass)

    // has to be delayed, because during startup (when the UI is constructed) the DB connection is not yet established
    private val delayedRegistry = ArrayList<JTextField>()

    override fun enableSuggestionsFor(vararg textFields: JTextField) {
        log.debug("enableSuggestionsFor(..)")
        delayedRegistry.addAll(textFields)
    }

    @Subscribe open fun onAppStartupEvent(event: AppStartupEvent) {
        delayedRegistry.forEach { reallyEnableSuggestionsFor(it) }
    }

    private fun reallyEnableSuggestionsFor(textField: JTextField) {

        var duringCompletion = false
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                if (duringCompletion) {
                    return
                }
                val currentText = textField.text
                log.trace("on text change => '{}'", currentText)
                val suggestValue = suggester.suggest(textField.name, currentText)
                if (suggestValue != null) {
                    SwingUtilities.invokeLater {
                        log.trace("Changing suggestion to: '{}'", suggestValue)
                        duringCompletion = true
                        // run later, as can not change text while being notified text has changed
                        textField.text = suggestValue
                        textField.select(currentText.length, suggestValue.length)
                        duringCompletion = false
                    }
                }
            }
            override fun changedUpdate(e: DocumentEvent?) { }
            override fun removeUpdate(e: DocumentEvent?) { }
        })
    }

}
