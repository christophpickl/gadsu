package non_test.fotostrecken_generator



fun generateLatex(project: Project): String {
    return with(project) {
        """
\documentclass[a4paper,12pt]{article}
\usepackage[utf8]{inputenc}
\usepackage{graphicx}
\usepackage{tocloft}

\title{$title}
\author{Christoph Pickl}

\begin{document}

\begin{center}

	~
	\vspace{8cm}

	\Huge{\textbf{$title}} \\
	\vspace{0.4cm}
	\Large{$date}

    \vfill

    \renewcommand\contentsname{}
    \renewcommand{\cftsecleader}{\cftdotfill{\cftdotsep}}
    \tableofcontents

\end{center}

\newpage

${generateSectionsTex(project)}

\end{document}
"""
    }
}

fun generateSectionsTex(project: Project): String {
    val string = StringBuilder()

    project.sections.forEach { section ->
        string.append("""
\section{${section.title}}

 \begin{table}[h!]
   \begin{center}
     \begin{tabular}{ c c }

	${generateImagesTex(project, section.images)}

      \end{tabular}
    \end{center}
\end{table}
""")

    }
    return string.toString()
}

fun generateImagesTex(project: Project, images: List<Image>): String {
    val evenImages = if (images.size.isOdd) images.plus(EMPTY_IMAGE) else images

    val string = StringBuilder()
    for (i in 1.rangeTo(evenImages.size / 2)) {
        val zeroI = i - 1
        val image1 = evenImages[zeroI * 2]
        val image2 = evenImages[zeroI * 2 + 1]

        val isEmpty2 = image2 === EMPTY_IMAGE
        val imageTex2 = if (isEmpty2) "" else "\\includegraphics{../images/${project.id}/${image2.file}}"
        val captionTex2 = if (isEmpty2) "" else image2.caption

        string.append("\t\t\\includegraphics{../images/${project.id}/${image1.file}} & $imageTex2 \\\\\n")
        string.append("\t\t${image1.caption} & $captionTex2 \\\\\n")
    }
    return string.toString()
}

private val Int.isOdd: Boolean
    get()  = this % 2 == 1

