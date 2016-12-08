package kotlin_playground

import at.cpickl.gadsu.isShortcutDown
import at.cpickl.gadsu.view.components.Framed
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.HashMap
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.text.AbstractDocument
import javax.swing.text.AttributeSet
import javax.swing.text.Element
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument


private class MySimpleRichTextor : JTextPane() {

    init {
        text = "hallo ein zwei drei"
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                if (e.keyChar == 'b' && e.isShortcutDown) {
                    onToggleBold()
                }
            }
        })
    }

    private fun AttributeSet.dump() {
        println("attributes: $this")
        attributeNames.iterator().forEach {
            val name = it
            val value = getAttribute(name)
            println("    name = [$name]; value = [$value]")
        }
    }

    private fun AttributeSet.toMap(): Map<Any, Any> {
        val map = HashMap<Any, Any>()
        attributeNames.iterator().forEach {
            map.put(it, getAttribute(it))
        }
        return map
    }

    private fun AttributeSet.containsBoldAttribute(): Boolean {
        val map = toMap()
        return map[StyleConstants.Bold]?.equals(true) ?: false
    }

    private fun StyledDocument.forEachChar(action: (Element) -> Boolean) {
        for (i in 0..length - 1) {
            val shouldBreak = action(getCharacterElement(i))
            if (shouldBreak) {
                break
            }
        }
    }


    private fun areAllCharsBold(start: Int, end: Int): Boolean {
//        val adoc = styledDocument as AbstractDocument
//        styledDocument.getCharacterElement(0).attributes.dump()

        for (i in start..end-1) {
            val element = styledDocument.getCharacterElement(i)
            element.attributes.dump()
            val isNotBold = !element.attributes.containsBoldAttribute()
            if (isNotBold) {
                return false
            }
        }

        return true
    }
//        println("adoc.length: ${adoc.length}, selection: $selectionStart/$selectionEnd")
////        println("adoc.rootElements.size: ${adoc.rootElements.size}")
//
//        for (i in selectionStart..(selectionEnd - 1)) {
//            println("  char at: $i")
//            val attrs = adoc.getParagraphElement(i).attributes
//            attrs.attributeNames.iterator().forEach {
//                val name = it
//                val value = attrs.getAttribute(name)
//                println("    name = [$name]; value = [$value]")
//            }
//        }

//        val offset: Int = 0
//        val length: Int = document.length//The value of the first 2 parameters in the setParagraphAttributes() call
//
//        val section = document.defaultRootElement
//        val index0 = section.getElementIndex(offset)
//        val index1 = section.getElementIndex(offset + if (length > 0) length - 1 else 0)
//        for (i in index0..index1) {
//            println("parsing section element index: $i")
//            val paragraph = section.getElement(i)
//            val attributeSet = paragraph.attributes
//            val keys = attributeSet.attributeNames
//            while (keys.hasMoreElements()) {
//                val key = keys.nextElement()
//                val attribute = attributeSet.getAttribute(key)
//                //System.out.println("key = " + key); //For other AttributeSet classes this line is useful because it shows the actual parameter, like "Bold"
//                println("${attribute.javaClass} - '$key': $attribute")
//            }
//        }

    private fun onToggleBold() {
        if (selectedText == null || selectedText.isEmpty()) {
            println("onToggleBold() aborted because is empty")
            return
        }
        val allBold =  areAllCharsBold(selectionStart, selectionEnd)
        println("onToggleBold() selectionStart=$selectionStart, selectionEnd=$selectionEnd; allBold=$allBold; selectedText=[$selectedText]")

        val sc = StyleContext.getDefaultStyleContext()
        var aset: AttributeSet
        if (allBold) {
            println("remove bold")
            aset = sc.removeAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background)
            aset = sc.addAttribute(aset, StyleConstants.Bold, false)
        } else {
            println("add bold")
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Background, Color.RED)
            aset = sc.addAttribute(aset, StyleConstants.Bold, true)
//        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console")
//        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED)

//        caretPosition = selectionStart//document.length
//        setCharacterAttributes(aset, false)
//        replaceSelection("a")


        }

        val adoc = styledDocument as AbstractDocument
        adoc.replace(selectionStart, selectedText.length, selectedText, aset)

        // removeAttributes()

    }
}

fun main(args: Array<String>) {
    Framed.show(
            JPanel(BorderLayout()).apply {

                val textor = MySimpleRichTextor()

                add(textor, BorderLayout.CENTER)

//                val textArea = MyRichTextArea()
//                add(textArea.asJComponent(), BorderLayout.CENTER)
//
//                add(JButton("read from view").apply {
//                    addActionListener {
//                        println("textArea.htmlText: \n" + textArea.htmlText)
//                    }
//                }, BorderLayout.NORTH)
//
//                add(JButton("write to view").apply {
//                    addActionListener {
//                        textArea.htmlText = "fuchur <b>war</b> da"
//                    }
//                }, BorderLayout.SOUTH)
            },
            Dimension(800, 500))
}
