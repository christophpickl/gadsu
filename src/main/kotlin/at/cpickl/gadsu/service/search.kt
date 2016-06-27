package at.cpickl.gadsu.service

import at.cpickl.gadsu.view.components.inputs.SearchTextField
import org.slf4j.LoggerFactory


interface SearchableService<T> {
    fun all(): List<T>
    fun find(searchTerm: String): List<T>
}

interface SearchableList<T> {
    fun resetData(elements: List<T>)
}

class Search<T> private constructor(
        private val textField: SearchTextField,
        private val list: SearchableList<T>,
        private val service: SearchableService<T>
) {
    companion object {
        fun <T> setup(textField: SearchTextField, list: SearchableList<T>, service: SearchableService<T>) {
            Search(textField, list, service).registerYourself()
        }

    }

    private val log = LoggerFactory.getLogger(javaClass)
    private val allData: List<T> = service.all()

    init {
        restoreAllData()
    }

    private fun registerYourself() {
        textField.addSearchListener { searchTerm ->
            log.debug("searched for: '{}'", searchTerm)
            if (searchTerm.isEmpty()) {
                restoreAllData()
            } else {
                list.resetData(service.find(searchTerm))
            }
        }
    }

    private fun restoreAllData() {
        log.trace("restoreAllData()")
        list.resetData(allData)
    }

}
