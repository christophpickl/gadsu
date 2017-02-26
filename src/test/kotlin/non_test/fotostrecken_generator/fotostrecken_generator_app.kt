package non_test.fotostrecken_generator

import java.io.File
import java.io.FileWriter

// kotlinc -script fotostrecken_generator.kts
val KeepBuildDir = true // turn on for table of contents

val RootDir = File("/Users/wu/Kampfkunst/Shiatsu/_fotostrecken")
val ReleaseDir = File(RootDir, "Releases")
val BuildDir = File(RootDir, "tmp_build")

fun main(args: Array<String>) {
    execute(IokaiProject)
}

fun execute(project: Project) {
    println("Building project: ${project.id} ...")

    prepareFolders()

    val latexSource = generateLatex(project)

    val texFilename = "${project.id}.tex"
    val texFile = File(BuildDir, texFilename)
    println("Writing LaTeX contetnt to: ${texFile.absolutePath}")
    FileWriter(texFile).use {
        it.write(latexSource)
    }

    executePdflatex(texFilename)
//    nativeExecute(BuildDir, "pdflatex", texFilename)

    val pdfFilename = "${project.id}.pdf"
    val pdfFile = File(BuildDir, pdfFilename)

    pdfFile.renameTo(File(ReleaseDir, pdfFilename))

    println("Opening file ...")
    nativeExecute(ReleaseDir, "open", pdfFilename)


    if (!KeepBuildDir) {
        BuildDir.deleteRecursively()
    }

    println("Done: ${pdfFile.absolutePath}")
}

fun nativeExecute(cwd: File, vararg args: String) {
    val result = Runtime.getRuntime().exec(args, emptyArray(), cwd).waitFor()
    if (result != 0) {
        throw RuntimeException("invalid return code $result for: ${args.joinToString(", ")}")
    }
}

fun prepareFolders() {
    if (!ReleaseDir.exists()) {
        ReleaseDir.mkdir()
    }

    if (!KeepBuildDir && BuildDir.exists()) {
        BuildDir.deleteRecursively()
    }
    BuildDir.mkdir()
}

fun executePdflatex(texFilename: String) {

    // pdflatex demo.tex
    val pdflatexProcess = ProcessBuilder("pdflatex", texFilename).apply {
        directory(BuildDir)
    }.start()
    println("Executing pdflatex ...")
    val pdflatexResult = pdflatexProcess.waitFor() // this hangs if there was a latex error :-/
    println("pdflatex returned.")
    if (pdflatexResult != 0) {
        throw RuntimeException("pdflatexResult: $pdflatexResult")
    }
}
