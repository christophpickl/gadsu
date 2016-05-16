package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.service.formatDateTimeLong
import at.cpickl.gadsu.treatment.DeleteTreatmentEvent
import at.cpickl.gadsu.treatment.OpenTreatmentEvent
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.CellView
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyList
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.MyListModel
import at.cpickl.gadsu.view.swing.Pad
import at.cpickl.gadsu.view.swing.bold
import at.cpickl.gadsu.view.swing.fatComponent
import com.google.common.eventbus.EventBus
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel


class TreatmentList @Inject constructor(
        private val bus: EventBus
) : MyList<Treatment>(
        ViewNames.Treatment.ListInClientView,
        MyListModel<Treatment>(),
        bus,
        object : MyListCellRenderer<Treatment>() {
            override fun newCell(value: Treatment) = TreatmentCell(value)
        }
) {
    init {
        initSinglePopup("L\u00f6schen", { DeleteTreatmentEvent(it) })
        initDoubleClicked { OpenTreatmentEvent(it) }
    }

}

class TreatmentCell(val treatment: Treatment): DefaultCellView<Treatment>(treatment), CellView {
    private val lblNumber = JLabel("${treatment.number}.")
    private val lblDate = JLabel("${treatment.date.formatDateTimeLong()}")

    override val applicableForegrounds: Array<JComponent> = arrayOf(lblNumber, lblDate)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
        add(lblNumber.bold())

        c.gridx++
        c.insets = Pad.LEFT
        add(lblDate)

        c.gridx++
        c.fatComponent()
        add(JLabel(""))
    }

}

