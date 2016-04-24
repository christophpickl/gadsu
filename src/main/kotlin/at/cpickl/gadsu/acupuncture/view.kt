package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.SearchTextField
import at.cpickl.gadsu.view.components.bold
import com.google.common.eventbus.EventBus
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel


class AcupunctureFrame @Inject constructor(
        private val bus: EventBus,
        val list: AcupunctureList
) : MyFrame("Akkpunktur Datenbank") {

    val inpSearch = SearchTextField()

    init {
        val panel = GridPanel()

        panel.c.weightx = 1.0
        panel.c.fill = GridBagConstraints.HORIZONTAL
        panel.add(inpSearch)

        panel.c.gridy++
        panel.c.fill = GridBagConstraints.BOTH
        panel.c.weighty = 1.0
        panel.add(list)


        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

        size = Dimension(300, 700)
    }

    // MINOR make reusable in MyFrame for others (controller should take over some more functionality)
    fun start() {
        isVisible = true
    }

    fun close() {
        isVisible = false
    }

    fun destroy() {
        close()
        dispose()
    }

}


class AcupunctCell(val treatment: Acupunct): DefaultCellView<Acupunct>(treatment) {

    private val txtTitle = JLabel("${treatment.meridian.name} ${treatment.number}").bold()
    private val txtIndications = JLabel(treatment.indications.joinToString())

    override val applicableForegrounds: Array<JComponent> = arrayOf(txtTitle, txtIndications)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtTitle)

        c.gridy++
        add(txtIndications)
        //        c.fatComponent()
        //        add(JLabel(""))
    }

}

private class AcupunctListCellRenderer : MyListCellRenderer<Acupunct>() {
    override fun newCell(value: Acupunct) = AcupunctCell(value)
}


class AcupunctureList @Inject constructor(
        private val bus: EventBus
) : MyList<Acupunct>(
        ViewNames.Acupunct.List,
        MyListModel<Acupunct>(),
        AcupunctListCellRenderer(),
        bus
), SearchableList<Acupunct> {
    init {
        //        initSinglePopup("L\u00f6schen", { DeleteTreatmentEvent(it) })
        //        initDoubleClicked { OpenTreatmentEvent(it) }
    }

}
