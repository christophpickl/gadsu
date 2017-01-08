package non_test.kotlin_playground


fun main(args: Array<String>) {
    val service = newService("myTestId")

    println(">> main: addListener.")
    service.addListener { println("event caught: $it") }
    println(">> main: service execute.")
    service.execute()

    // NOT VISIBLE to the outside world :) service.idNotVisibleToOutsideWorld
    println(">> main: printId.")
    service.printId()
}




fun newService(myId: String): Service {
    // idNotVisibleToOutsideWorld is routed two times, yes :-/
    return MyService(myId, DefaultObservable(), object : DefaultIdPrintable() {
        override val idNotVisibleToOutsideWorld = myId
    })
}

data class Event(val message: String)

// two interfaces implemented by delegation feature
interface Service : Observable, IdPrintable {
    fun execute()
}

interface Observable {
    fun addListener(listener: (Event) -> Unit)
}

interface IdPrintable {
    fun printId()
}



private class MyService(override val idNotVisibleToOutsideWorld: String,
                        private val observable: DefaultObservable,
                        private val idPrintable: IdPrintable

) : Service, HasId,
        // this does the trick! =)
        Observable by observable, IdPrintable by idPrintable {

    override fun execute() {
        println("MyService.execute()")
        observable._dispatch(Event("executed"))
    }
}

private open class DefaultObservable : Observable {
    private val listeners = mutableListOf<(Event) -> Unit>()

    override fun addListener(listener: (Event) -> Unit) {
        println("DefaultObservable.addListener()")
        listeners.add(listener)
    }
    fun _dispatch(event: Event) {
        println("DefaultObservable._dispatch()")
        listeners.forEach { it.invoke(event) }
    }
}

// internal interface
private interface HasId {
    val idNotVisibleToOutsideWorld: String
}
private abstract class DefaultIdPrintable : IdPrintable, HasId {
    override fun printId() {
        println("DefaultIdPrintable.printId() ... ID = '${this.idNotVisibleToOutsideWorld}'")
    }

}
