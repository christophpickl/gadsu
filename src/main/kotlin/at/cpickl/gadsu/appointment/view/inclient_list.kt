package at.cpickl.gadsu.appointment.view

import at.cpickl.gadsu.appointment.Appointment
import at.cpickl.gadsu.appointment.DeleteAppointmentEvent
import at.cpickl.gadsu.appointment.OpenAppointmentEvent
import at.cpickl.gadsu.service.formatDateTimeLong
import at.cpickl.gadsu.view.ViewNames
import at.cpickl.gadsu.view.components.*
import at.cpickl.gadsu.view.swing.transparent
import com.google.common.eventbus.EventBus
import java.awt.GridBagConstraints
import javax.inject.Inject
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class AppointmentList @Inject constructor(
        private val bus: EventBus
) : MyList<Appointment>(
        ViewNames.Appointment.ListInClientView,
        MyListModel<Appointment>(),
        bus,
        object : MyListCellRenderer<Appointment>() {
            override fun newCell(value: Appointment) = AppointmentCell(value)
        }
) {
    init {
        initSinglePopup("L\u00f6schen", { DeleteAppointmentEvent(it) })
        initDoubleClicked { OpenAppointmentEvent(it) }
    }

}

class AppointmentCell(val appointment: Appointment): DefaultCellView<Appointment>(appointment), CellView {
    private val lblDate = JLabel("${appointment.start.formatDateTimeLong()}")

    override val applicableForegrounds: Array<JComponent> = arrayOf(lblDate)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
        add(lblDate)

        // fill UI hack ;)
        c.gridx++
        c.weightx = 1.0
        c.fill = GridBagConstraints.HORIZONTAL
        add(JPanel().transparent())
    }
}