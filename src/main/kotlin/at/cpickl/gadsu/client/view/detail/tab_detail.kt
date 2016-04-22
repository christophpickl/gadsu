package at.cpickl.gadsu.client.view.detail

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.view.Labels
import javax.swing.JLabel


class ClientTabDetail : DefaultClientTab(Labels.Tabs.ClientDetail) {

    init {
        add(JLabel("Hier kommen noch gesundheits/TCM Daten rein ..."))
    }

    override fun isModified(client: Client): Boolean {
        return false
    }

    override fun updateFields(client: Client) {

    }
}
