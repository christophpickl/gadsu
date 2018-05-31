package non_test._main_.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.view.detail.ClientTab5e
import at.cpickl.gadsu.view.logic.ModificationAware
import at.cpickl.gadsu.view.logic.ModificationChecker
import non_test.Framed


fun main(args: Array<String>) {
    Framed.showWithContextDefaultSize { context ->
        ClientTab5e(
                Client.INSERT_PROTOTYPE,
                ModificationChecker(object : ModificationAware {
                    override fun isModified() = true
                }),
                context.bus
        ).asComponent()
    }
}
