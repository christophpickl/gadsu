package at.cpickl.gadsu.client

import at.cpickl.gadsu.view.MainWindow
import com.google.inject.Inject
import javax.swing.JLabel
import javax.swing.JPanel

class ClientViewController @Inject constructor(
        private val mainWindow: MainWindow
) {
    private val view: ClientView = ClientView()
    init {
        // MINOR maybe this should go to the startup logic instead
        mainWindow.changeContent(view)
    }
}

class ClientView : JPanel() {
    init {
        add(JLabel("client"))
    }
}
