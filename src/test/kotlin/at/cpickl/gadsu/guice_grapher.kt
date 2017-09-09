package at.cpickl.gadsu

import at.cpickl.gadsu.global.QuitEvent
import at.cpickl.gadsu.start.Args
import at.cpickl.gadsu.start.GadsuModule
import com.google.common.eventbus.EventBus
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.grapher.graphviz.GraphvizGrapher
import com.google.inject.grapher.graphviz.GraphvizModule
import java.io.File
import java.io.PrintWriter

fun main(args: Array<String>) {
    graph("gadsu_guice_graph.dot", Guice.createInjector(GadsuModule(Args.EMPTY)))
}

private fun graph(filename: String, appInjector: Injector) {
    // https://github.com/google/guice/wiki/Grapher
    val target = File(filename)
    val out = PrintWriter(target, "UTF-8");

    val injector = Guice.createInjector(GraphvizModule())
    val grapher = injector.getInstance(GraphvizGrapher::class.java)
    grapher.setOut(out)
    grapher.setRankdir("TB")
    grapher.graph(appInjector)

    appInjector.getInstance(EventBus::class.java).post(QuitEvent())
    println("Successfully saved graph to file: ${target.absolutePath}")
}
