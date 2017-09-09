package non_test._main_.view

import at.cpickl.gadsu.global.PanicDialog

fun main(args: Array<String>) {
    val throwable = Exception("exception message")
    PanicDialog(throwable, null, { }).showIt()
}
