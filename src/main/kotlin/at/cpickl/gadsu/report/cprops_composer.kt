package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CPropTypeCallback


object CPropsComposer {
    fun compose(client: Client) : String? {
        if (client.cprops.isEmpty()) {
            return null
        }
        return client.cprops.map { it.onType(object : CPropTypeCallback<String> {
            override fun onEnum(cprop: CPropEnum): String {
                return "${cprop.label}: ${cprop.clientValue.map { it.label }.joinToString("; ")}"
            }

        } ) }.joinToString("\n")
    }
}
