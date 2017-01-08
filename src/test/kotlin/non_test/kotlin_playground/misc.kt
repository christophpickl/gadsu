package non_test.kotlin_playground



//fun `func is fun`() {
//    val btn1: JButton = JButton().apply {
//        text = "hihi"
//        true
//    }
//    val btn2: JButton = with(JButton()) {
//        text = "hihi"
//        this
//    }
//    val panel = JPanel()
//    panel.add(JButton().let { btn ->
//        btn.text = "hihi"
//        btn
//    })
//}


fun main(args: Array<String>) {
    myRecursion(10)
}

fun myRecursion(max: Int) {
    _myRecursion(1, max)
}

fun _myRecursion(current: Int, max: Int) {
    when {
        current <= max -> { println(current); _myRecursion(current + 1, max) }
        else -> return
    }
}
