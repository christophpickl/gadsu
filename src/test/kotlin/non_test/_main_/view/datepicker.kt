package non_test._main_.view

import at.cpickl.gadsu.view.datepicker.view.MyDatePicker
import non_test.Framed
import java.awt.Component
import javax.swing.JButton

//fun main(args: Array<String>) {
//    Framed.show(JDatePicker())
//}

// https://github.com/JDatePicker/JDatePicker
// http://www.codejava.net/java-se/swing/how-to-use-jdatepicker-to-display-calendar-component
fun main(args: Array<String>) {
    val datePicker = MyDatePicker.build(null, "my")
//    datePicker.disableClear()
    val btn = JButton("print selected date")
    btn.addActionListener {
        println("selectedDate: " + datePicker.selectedDate())
    }

    Framed.show(arrayOf(datePicker as Component, btn))
}
