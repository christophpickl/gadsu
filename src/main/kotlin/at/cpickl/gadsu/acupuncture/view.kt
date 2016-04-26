package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.GridPanel
import at.cpickl.gadsu.view.components.HtmlEditorPane
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.Pad
import at.cpickl.gadsu.view.components.SearchTextField
import at.cpickl.gadsu.view.components.bold
import at.cpickl.gadsu.view.components.enforceWidth
import at.cpickl.gadsu.view.components.scrolled
import at.cpickl.gadsu.view.toHexString
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JLabel


class AcupunctureFrame @Inject constructor(
        private val bus: EventBus,
        val list: AcupunctureList
) : MyFrame("Akupunktur Datenbank") {

    private val log = LoggerFactory.getLogger(javaClass)
    val inpSearch = SearchTextField()

    private val bigText = HtmlEditorPane()

    init {
        val panel = GridPanel()
        inpSearch.enforceWidth(200)
        list.enforceWidth(200)

        // SEARCH
        panel.c.weightx = 0.0
        panel.c.weighty = 0.0
        panel.c.fill = GridBagConstraints.HORIZONTAL
        panel.add(inpSearch)

        // DETAIL
        panel.c.gridx++
        panel.c.insets = Pad.LEFT
        panel.c.gridheight = 2
        panel.c.weightx = 1.0
        panel.c.weighty = 1.0
        panel.c.fill = GridBagConstraints.BOTH
        panel.add(bigText)

        // MASTER LIST
        panel.c.insets = Pad.NONE
        panel.c.gridx = 0
        panel.c.gridy++
        panel.c.gridheight = 1
        panel.c.weightx = 0.0
        panel.c.weighty = 1.0
        panel.c.fill = GridBagConstraints.BOTH
        panel.add(list.scrolled())


        panel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

        size = Dimension(650, 500)
    }

    fun changeAcupunct(punct: Acupunct?) {
        log.debug("changeAcupunct(newAcupunct={})", punct)
        if (punct == null) {
            bigText.text = "Bitte einen Akupunkturpunkt ausw\u00e4hlen."
        } else {
            val meridian = punct.meridian
            val element = meridian.element
            bigText.text = """
            <h1>${punct.titleLong}</h1>
            <b>Element</b>: <span color="#${element.color.toHexString()}">${element.label}</span><br/>
            <b>Extremit${"\u00e4"}t</b>: ${meridian.extremity.label}<br/>
            <b>Indikationen</b>: ${punct.indications.joinToString()}<br/>
            <br/>
            ${punct.note}
            """
        }
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

class AcupunctCell(val punct: Acupunct): DefaultCellView<Acupunct>(punct) {

    private val txtTitle = JLabel(punct.titleShort).bold()
    private val txtIndications = JLabel(punct.indications.joinToString())

    override val applicableForegrounds: Array<JComponent> = arrayOf(txtTitle, txtIndications)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(txtTitle)

        c.gridy++
        add(txtIndications)
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
        bus,
        AcupunctListCellRenderer()
), SearchableList<Acupunct> {
    init {
        //        initSinglePopup("L\u00f6schen", { DeleteTreatmentEvent(it) })
        //        initDoubleClicked { OpenTreatmentEvent(it) }
    }

}
