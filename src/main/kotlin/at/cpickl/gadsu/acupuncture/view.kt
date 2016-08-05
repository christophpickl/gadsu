package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.service.SearchableList
import at.cpickl.gadsu.tcm.model.Acupunct
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyFrame
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.components.inputs.SearchTextField
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.enforceWidth
import at.cpickl.gadsu.view.swing.scrolled
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
        val list: AcupunctureList
) : MyFrame("Akupunktur Datenbank") {

    val inpSearch = SearchTextField()

    private val log = LoggerFactory.getLogger(javaClass)
    private val bigText = HtmlEditorPane()

    init {
        val panel = GridPanel()
        inpSearch.enforceWidth(200)
        // NOOOOO! list.enforceWidth(200)

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
//        val scroll = JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)
        panel.add(list.scrolled())


        panel.border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        contentPane.layout = BorderLayout()
        contentPane.add(panel, BorderLayout.CENTER)

        size = Dimension(650, 500)
    }

    fun clearAcupunct() {
        bigText.text = "Bitte einen Akupunkturpunkt ausw\u00e4hlen."
    }
    fun changeAcupunct(punct: Acupunct) {
        log.debug("changeAcupunct(newAcupunct={})", punct)
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

    // MINOR @REFACTOR UI - make reusable in MyFrame for others (controller should take over some more functionality)
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

class AcupunctCell(punct: Acupunct): DefaultCellView<Acupunct>(punct) {

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

class AcupunctureList @Inject constructor(
        bus: EventBus
) : MyList<Acupunct>(
        ViewNames.Acupunct.List,
        MyListModel<Acupunct>(),
        bus,
        object : MyListCellRenderer<Acupunct>() {
            override fun newCell(value: Acupunct) = AcupunctCell(value)
        }
), SearchableList<Acupunct> {
    init {
        //        initSinglePopup("L\u00f6schen", { DeleteTreatmentEvent(it) })
        //        initDoubleClicked { OpenTreatmentEvent(it) }
    }

}
