package at.cpickl.gadsu.view.components.panels

import at.cpickl.gadsu.global.IS_OS_WIN
import at.cpickl.gadsu.client.xprops.view.GridBagFill
import at.cpickl.gadsu.view.components.inputs.HtmlEditorPane
import at.cpickl.gadsu.view.swing.*
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.JLabel


open class FormPanel(
        private val fillCellsGridy: Boolean = true,
        private val labelAnchor: Int = GridBagConstraints.NORTHEAST,
        private val inputAnchor: Int = GridBagConstraints.SOUTHWEST
) : GridPanel() {
    private val insetsCol1 = Insets(5, 0, 0, 4) // add a bit to the top, and 4 on the right to create a h-gap
    private val insetsCol2 = Insets(0, 0, 0, 0)
    private val insetsCol2leftIncreased = insetsCol2.addLeft(5)

    init {
        c.anchor = GridBagConstraints.NORTHWEST
    }

    open fun addDescriptiveFormInput(
            label: String,
            input: Component,
            description: String,
            fillType: GridBagFill = GridBagFill.Horizontal,
            icon: ImageIcon? = null,
            addTopInset: Int = 0,
            inputWeighty: Double = 0.0
    ) {
        addFormInput(label, input, fillType, icon, addTopInset, inputWeighty)
        val txtDescription = HtmlEditorPane(description)
                .changeLabelFontSize(11.0F)
                .disableFocusable()
        txtDescription.border = BorderFactory.createEmptyBorder(0, 5, 0, 0)
        addFormInput("", txtDescription, GridBagFill.Both)
    }


    open fun addFormInput(
            label: String,
            input: Component,
            fillType: GridBagFill = GridBagFill.Horizontal,
            icon: ImageIcon? = null,
            addTopInset: Int = 0,
            inputWeighty: Double = 0.0
    ) {
        c.gridheight = 1

        c.weightx = 0.0
        c.weighty = inputWeighty
        c.fill = GridBagConstraints.NONE
        c.anchor = labelAnchor
        c.insets = insetsCol1.addPaddingForWindows().addTop(addTopInset)
        add(JLabel(label).bold())

        addIconMaybe(icon)

        c.gridx++
        c.weightx = 1.0
        c.weighty = if (fillCellsGridy) 1.0 else 0.0
        c.fill = fillType.swingId
        c.anchor = inputAnchor
        if (input is JLabel) {
            c.insets = insetsCol2leftIncreased
        } else {
            c.insets = insetsCol2
        }
        c.insets = c.insets.addPaddingForWindows().addTop(addTopInset)
        add(input)

        c.gridy++
        if (icon != null) c.gridy++
        c.gridx = 0
    }

    private fun Insets.addPaddingForWindows(): Insets {
        if (!IS_OS_WIN || c.gridy == 0) {
            return this
        }
        return this.addTop(10)
    }

    protected open fun addIconMaybe(icon: ImageIcon?) {
        if (icon == null) {
            return
        }
        c.gridy++
        c.insets = Insets(5, 0, 0, 4)
        c.weighty = 0.0
        c.weightx = 0.0
        c.anchor = GridBagConstraints.NORTH
        c.fill = GridBagConstraints.NONE
        add(JLabel(icon))

        c.gridheight = 2
        c.gridy--
    }

    open fun addLastColumnsFilled(fillType: GridBagFill = GridBagFill.Both) {
        c.gridwidth = 2
        addLastRowFilled(fillType)
    }
}


/**
 * Like Form, but vertically aligned and vertical space is evenly distributed.
 *
 * Used in treatment view.
 */
class VFillFormPanel : FormPanel() {
    init {
        c.anchor = GridBagConstraints.NORTHWEST
    }

    /**
     * @param addTopInset not used in this subclass :-p
     */
    override fun addFormInput(
            label: String,
            input: Component,
            fillType: GridBagFill,
            icon: ImageIcon?,
            addTopInset: Int,
            inputWeighty: Double // not used
    ) {
        c.weightx = 0.0
        c.weighty = 0.0
        c.fill = GridBagConstraints.NONE
        c.insets = Pad.TOP
        add(JLabel(label).bold())

//        addIconMaybe(icon) ... not yet supported

        c.gridy++
//        c.fill = fillType.swingId
        c.fill = GridBagConstraints.BOTH
        c.weightx = 1.0
        c.weighty = 0.3 // dont know exactly...
        c.insets = Pad.NONE
        add(input)

        c.gridy++
    }

    override fun addLastColumnsFilled(fillType: GridBagFill) {
        addLastRowFilled(fillType)
    }
}
