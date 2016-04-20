package at.cpickl.gadsu.treatment.inclient

import at.cpickl.gadsu.service.formatDateTimeLong
import at.cpickl.gadsu.treatment.Treatment
import at.cpickl.gadsu.view.components.CellView
import at.cpickl.gadsu.view.components.DefaultCellView
import at.cpickl.gadsu.view.components.MyListCellRenderer
import at.cpickl.gadsu.view.components.Pad
import at.cpickl.gadsu.view.components.bold
import at.cpickl.gadsu.view.components.fatComponent
import java.awt.GridBagConstraints
import javax.swing.JComponent
import javax.swing.JLabel

class TreatmentCell(val treatment: Treatment): DefaultCellView<Treatment>(treatment), CellView {

    private val lblNumber = JLabel("${treatment.number}. Behandlung")
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

class TreatmentListCellRenderer : MyListCellRenderer<Treatment>() {
    override fun newCell(value: Treatment) = TreatmentCell(value)
}
