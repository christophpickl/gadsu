package non_test._main_.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.view.CPropsRenderer
import at.cpickl.gadsu.tcm.model.XProps
import at.cpickl.gadsu.testinfra.unsavedValidInstance
import at.cpickl.gadsu.view.Fields
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.panels.FormPanel
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import java.awt.Dimension

fun main(args: Array<String>) {

    Framed.showWithContext({ context ->
        val modification = ModificationChecker(object : ModificationAware {
            override fun isModified() = false
        })
        val fields = Fields<Client>(modification)
        val renderer = CPropsRenderer(fields, context.bus)
        val form = FormPanel()
        renderer.addXProp(XProps.Sleep, form)
        renderer.updateFields(Client.unsavedValidInstance().copy(
                cprops = CProps.builder()
                        .add(XProps.Sleep, XProps.SleepOpts.NeedMuch, XProps.SleepOpts.ProblemsFallAsleep)
                        .build()
        ))

        form
    }, size = Dimension(500, 400))
}
