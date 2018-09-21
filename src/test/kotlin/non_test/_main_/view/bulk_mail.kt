package non_test._main_.view

import at.cpickl.gadsu.mail.bulkmail.BulkMailSwingView
import at.cpickl.gadsu.view.MainFrame
import com.nhaarman.mockito_kotlin.mock
import non_test.Framed

fun main(args: Array<String>) {
    Framed.showFrameWithContext({ context ->
        val mainFrame = mock<MainFrame>()
        BulkMailSwingView(mainFrame, context.bus)
    })
}
