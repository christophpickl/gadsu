package at.cpickl.gadsu.view

import at.cpickl.gadsu.global.GadsuException
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.Test
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedList
import kotlin.reflect.full.memberProperties

@Test class ViewNamesTest {

    private val log = LoggerFactory.getLogger(javaClass)

    fun `no duplicate view names registered`() {
        val duplicates = extractProperties().filterValues { it.size > 1 } // more than one containerId registeredViewName
        if (duplicates.isNotEmpty()) {
            Assert.fail("Duplicate view names registered!\n" +
                    duplicates.map { "View name '${it.key}' registered by: ${it.value.joinToString()}" }
                            .joinToString(separator = "\n - "))
        }
    }

    private fun extractProperties(): HashMap<String, MutableList<String>> {
        // containerId eg: "Client.CancelButton"
        val viewNamesByContainerId = LinkedHashMap<String, MutableList<String>>()
        ViewNames::class.memberProperties.forEach {
            val containerRef = it
            log.debug("Checking container (${containerRef.name}): {}", containerRef)
            if (containerRef.name == "Components") {
                log.debug("Skip Components... dynamic view names.")
            } else {
                val containerObj = containerRef.get(ViewNames)!!
                containerObj.javaClass.kotlin.memberProperties.forEach {
                    val viewNameRef = it

                    val containerId = "${containerRef.name}.${viewNameRef.name}"
                    val viewNameObj = viewNameRef.get(containerObj)
                    if (viewNameObj !is String) {
                        throw GadsuException("Expected to be a String: " + viewNameObj)
                    }
                    if (!viewNamesByContainerId.containsKey(viewNameObj)) {
                        viewNamesByContainerId.put(viewNameObj, LinkedList())
                    }
                    log.debug("Found view name '{}' => '{}'", containerId, viewNameObj)
                    viewNamesByContainerId.get(viewNameObj)!!.add(containerId)
                }
            }
        }
        return viewNamesByContainerId
    }

}
