package at.cpickl.gadsu._main_

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker

fun main(args: Array<String>) {

    Framed.showWithContext({ context ->
        val modification = ModificationChecker(object : ModificationAware {
            override fun isModified() = false
        })
        val fields = Fields<Client>(modification)
        val renderer = CPropsRenderer(fields, context.bus)
        val form = FormPanel()
        renderer.addXProp(XProps.Sleep, form)
        
        form
    })
}
