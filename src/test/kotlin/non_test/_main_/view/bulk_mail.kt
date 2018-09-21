package non_test._main_.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.testInstance1
import at.cpickl.gadsu.client.testInstance2
import at.cpickl.gadsu.mail.bulkmail.BulkMailSwingView
import at.cpickl.gadsu.view.MainFrame
import com.nhaarman.mockito_kotlin.mock
import non_test.Framed

fun main(args: Array<String>) {
    Framed.showFrameWithContext({ context ->
        val mainFrame = mock<MainFrame>()
        BulkMailSwingView(mainFrame, context.bus).apply {
            initClients(listOf(
                    Client.testInstance1,
                    Client.testInstance2,
                    Client.testInstance1.copy(nickNameInt = "Foo", lastName = "Baristabumbad"),
                    Client.testInstance1.copy(nickNameInt = "Max", lastName = "Muster"),
                    Client.testInstance1.copy(nickNameInt = "Anna", lastName = "Nym"),
                    Client.testInstance1.copy(nickNameInt = "Hans", lastName = "Goisern"),
                    Client.testInstance1.copy(nickNameInt = "Peter", lastName = "Hubert"),
                    Client.testInstance1.copy(nickNameInt = "Otto", lastName = "Walker")
            ))
        }
    })
}
