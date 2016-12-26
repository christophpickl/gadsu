package at.cpickl.gadsu.report

import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CPropTypeCallback
import at.cpickl.gadsu.client.xprops.model.CProps


object CPropsComposer {

    fun compose(cprops: CProps) : String? {
        // MINOR CPropsComposer does not take notes into account yet
        if (cprops.isEmpty()) {
            return null
        }
        return cprops.map { it.onType(object : CPropTypeCallback<String> {
            override fun onEnum(cprop: CPropEnum): String {
                return "${cprop.label}: ${cprop.clientValue.map { it.label }.joinToString("; ")}"
            }

        } ) }.joinToString("\n")
    }

}
