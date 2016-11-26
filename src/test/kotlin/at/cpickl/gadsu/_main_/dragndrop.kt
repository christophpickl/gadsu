package at.cpickl.gadsu._main_

import at.cpickl.gadsu.service.LOG
import at.cpickl.gadsu.view.components.Framed
import at.cpickl.gadsu.view.components.panels.GridPanel
import at.cpickl.gadsu.view.swing.enforceSize
import at.cpickl.gadsu.view.swing.scrolled
import java.awt.Color
import java.awt.Container
import java.awt.Cursor
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.DnDConstants
import java.awt.dnd.DragGestureEvent
import java.awt.dnd.DragGestureListener
import java.awt.dnd.DragGestureRecognizer
import java.awt.dnd.DragSource
import java.awt.dnd.DragSourceDragEvent
import java.awt.dnd.DragSourceDropEvent
import java.awt.dnd.DragSourceEvent
import java.awt.dnd.DragSourceListener
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.io.Serializable
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

// http://stackoverflow.com/a/11443501

// MINOR @DnD - indicate valid drop target (e.g.: change the cursor)

fun main(args: Array<String>) {
    Framed.show(GridPanel().apply {
        c.weightx = 0.5
        c.weighty = 1.0
        c.fill = GridBagConstraints.BOTH
        add(GadsuDropTarget().apply {
            background = Color.WHITE
        }.scrolled(hPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS, vPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER))

        c.gridx++
        add(JPanel().apply {
            background = Color.GRAY
            add(RedDot())
        })
    }, Dimension(400, 200))
}

class GadsuDropTarget : JPanel() {

    var myDropHandler: DropHandler? = null
    var myDropTarget: DropTarget? = null

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
    }

    override fun addNotify() {
        super.addNotify()
        myDropHandler = DropHandler()
        myDropTarget = DropTarget(this, DnDConstants.ACTION_MOVE, myDropHandler, true)
    }
    override fun removeNotify() {
        myDropTarget?.removeDropTargetListener(myDropHandler)
        super.removeNotify()
    }
}

/**
 * The thing to be drag'n'dropped.
 */
class RedDot : JPanel(), Serializable {
    companion object {
        private val serialVersionUid: Long = 123
    }
    private val dndTarget = GadsuDndTarget(this) // delegate instead of super class

    init {
        background = Color.RED
        border = BorderFactory.createLineBorder(Color.BLACK)
        enforceSize(50, 50)
    }

    override fun addNotify() {
        super.addNotify()
        dndTarget.addNotify()
    }

    override fun removeNotify() {
        dndTarget.removeNotify()
        super.removeNotify()
    }
}

class ActiveDot : JPanel() {
    init {
        background = Color.RED.darker()
        border = BorderFactory.createLineBorder(Color.BLACK)
        enforceSize(50, 50)
    }
}

// DRAG
// =====================================================================================================================
/**
 * Flavor.
 */
object JPanelDataFlavor : DataFlavor(JPanel::class.java, null)

/**
 * The Transferable.
 */
class PanelTransferable(private val panel: JPanel) : Transferable {
    private val flavors : Array<DataFlavor> = arrayOf(JPanelDataFlavor)

    override fun getTransferData(flavor: DataFlavor?): Any {
        if (!isDataFlavorSupported(flavor)) {
            throw UnsupportedFlavorException(flavor)
        }
        return panel
    }

    override fun isDataFlavorSupported(flavor: DataFlavor?) = flavor == JPanelDataFlavor
    override fun getTransferDataFlavors() = flavors

}

/**
 * DnD handler.
 */
class DragGestureHandler(private val child: JPanel) : DragGestureListener, DragSourceListener, Serializable {
    companion object {
        private val serialVersionUid: Long = 12321
    }
    private val log = LOG(javaClass)
    private var parent: Container? = null

    override fun dragGestureRecognized(event: DragGestureEvent) {
        log.trace("dragGestureRecognized(event={})", event)
        parent = child.parent
        // parent!!.remove(child) ... NO, this will lead to remove the icon from the drag source (and a NPE!)
        parent!!.invalidate()
        parent!!.repaint()
        event.dragSource.startDrag(
            event, Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR), PanelTransferable(child), this)
    }

    override fun dragDropEnd(event: DragSourceDropEvent) {
        log.trace("dragDropEnd(event.dropSuccess={})", event.dropSuccess)
        // not necessary, as we did not remove the thing when started dragging
//        if (event.dropSuccess) {
//            parent!!.add(child)
//            parent!!.invalidate()
//            parent!!.repaint()
//        }
    }
    override fun dragOver(event: DragSourceDragEvent) { }
    override fun dragExit(event: DragSourceEvent) { }
    override fun dropActionChanged(event: DragSourceDragEvent) { }
    override fun dragEnter(event: DragSourceDragEvent) { }
}

/**
 * Delegate class to add/remove notify.
 */
class GadsuDndTarget(private val target: JPanel) : Serializable {
    companion object {
        private val serialVersionUid: Long = 1234
    }
    private val log = LOG(javaClass)
    private var dgr: DragGestureRecognizer? = null
    private var dragGestureHandler: DragGestureHandler? = null

    fun addNotify() {
        log.trace("addNotify()")
        if (dgr == null) {
            dragGestureHandler = DragGestureHandler(target)
            dgr = DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                    target, DnDConstants.ACTION_MOVE, dragGestureHandler
            )
        }
    }
    fun removeNotify() {
        log.trace("removeNotify()")
        if (dgr != null) {
            dgr!!.removeDragGestureListener(dragGestureHandler)
            dragGestureHandler = null
        }
        dgr = null
    }
}

// DROP
// =====================================================================================================================

class DropHandler : DropTargetListener, Serializable {
    companion object {
        private val serialVersionUid: Long = 12325
    }
    private val log = LOG(javaClass)

    override fun drop(event: DropTargetDropEvent) {
        log.trace("drop(event={})", event)
        var success: Boolean
        if (event.isDataFlavorSupported(JPanelDataFlavor)) {
            val transferable = event.transferable
            try {
                val data = transferable.getTransferData(JPanelDataFlavor)
                if (data is JPanel) {
                    val dtc = event.dropTargetContext
                    val component = dtc.component
                    if (component is JComponent) {
                        log.trace("Accepting drop; repainting.")
                        // extract data from "data" field to create new active dot
                        component.add(ActiveDot())
                        success = true
                        event.acceptDrop(DnDConstants.ACTION_MOVE)
                        // MINOR @DnD - does not affect the scrollbar!
                        component.invalidate()
                        component.revalidate()
                        component.repaint()
                    } else {
                        success = false
                        event.rejectDrop()
                    }
                } else {
                    success = false
                    event.rejectDrop()
                }
            } catch (e: Exception) {
                success = false
                event.rejectDrop()
                log.warn("Drop failed! Event: $event", e)
            }
        } else {
            success = false
            event.rejectDrop()
        }
        event.dropComplete(success)
    }

    override fun dragEnter(event: DropTargetDragEvent) {
        if (event.isDataFlavorSupported(JPanelDataFlavor)) {
            log.trace("dragEnter(event={}) ... accept drag", event)
            event.acceptDrag(DnDConstants.ACTION_MOVE)
        } else {
            log.trace("dragEnter(event={}) ... reject drag", event)
            event.rejectDrag()
        }
    }

    override fun dragExit(event: DropTargetEvent) {
        log.trace("dragExit(event)")
    }
    override fun dragOver(event: DropTargetDragEvent) { }
    override fun dropActionChanged(event: DropTargetDragEvent) { }

}
