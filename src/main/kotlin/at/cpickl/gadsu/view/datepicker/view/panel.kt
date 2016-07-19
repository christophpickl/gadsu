package at.cpickl.gadsu.view.datepicker.view

import at.cpickl.gadsu.view.datepicker.*
import org.jdatepicker.DateModel
import java.awt.Component
import java.awt.GridLayout
import java.awt.event.*
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.JTableHeader
import javax.swing.table.TableModel


open class JDatePanel
/**
 * Create a JDatePanel with a custom date model.

 * @param model a custom date model
 */
@JvmOverloads constructor(model: DateModel<*> = JDatePanel.createModel()) : JComponent(), DatePanel {

    private val actionListeners: MutableSet<ActionListener>
    private val dateConstraints: MutableSet<DateSelectionConstraint>

    private var showYearButtons: Boolean = false
    /* (non-Javadoc)
     * @see org.jdatepicker.JDatePanel#isDoubleClickAction()
     */
    /* (non-Javadoc)
     * @see org.jdatepicker.JDatePanel#setDoubleClickAction(boolean)
     */
    override var isDoubleClickAction: Boolean = false
    private val firstDayOfWeek: Int

    private val internalModel: InternalCalendarModel
    private val internalController: InternalController
    private val internalView: InternalView

    /**
     * Create a JDatePanel with an initial value, with a UtilCalendarModel.

     * @param value the initial value
     */
    constructor(value: Calendar) : this(createModelFromValue(value)) {
    }

    /**
     * Create a JDatePanel with an initial value, with a UtilDateModel.

     * @param value the initial value
     */
    constructor(value: java.util.Date) : this(createModelFromValue(value)) {
    }

    /**
     * Create a JDatePanel with an initial value, with a SqlDateModel.

     * @param value the initial value
     */
    constructor(value: java.sql.Date) : this(createModelFromValue(value)) {
    }

    init {
        actionListeners = HashSet<ActionListener>()
        dateConstraints = HashSet<DateSelectionConstraint>()

        showYearButtons = false
        isDoubleClickAction = false
        firstDayOfWeek = Calendar.getInstance().firstDayOfWeek

        internalModel = InternalCalendarModel(model)
        internalController = InternalController()
        internalView = InternalView()

        layout = GridLayout(1, 1)
        add(internalView)
    }

    override fun addActionListener(actionListener: ActionListener) {
        actionListeners.add(actionListener)
    }

    override fun removeActionListener(actionListener: ActionListener) {
        actionListeners.remove(actionListener)
    }

    /**
     * Called internally when actionListeners should be notified.
     */
    private fun fireActionPerformed() {
        for (actionListener in actionListeners) {
            actionListener.actionPerformed(ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Date selected"))
        }
    }

    override var isShowYearButtons: Boolean
        get() = this.showYearButtons
        set(showYearButtons) {
            this.showYearButtons = showYearButtons
            internalView.updateShowYearButtons()
        }

    override val model: DateModel<*>
        get() = internalModel.model

    override fun addDateSelectionConstraint(constraint: DateSelectionConstraint) {
        dateConstraints.add(constraint)
    }

    override fun removeDateSelectionConstraint(constraint: DateSelectionConstraint) {
        dateConstraints.remove(constraint)
    }

    override fun removeAllDateSelectionConstraints() {
        dateConstraints.clear()
    }

    override val dateSelectionConstraints: Set<DateSelectionConstraint>
        get() = Collections.unmodifiableSet(dateConstraints)

    fun checkConstraints(model: DateModel<*>): Boolean {
        for (constraint in dateConstraints) {
            if (!constraint.isValidSelection(model)) {
                return false
            }
        }
        return true
    }

    override fun setVisible(aFlag: Boolean) {
        super.setVisible(aFlag)

        if (aFlag) {
            internalView.updateTodayLabel()
        }
    }

    override fun setEnabled(enabled: Boolean) {
        internalView.isEnabled = enabled

        super.setEnabled(enabled)
    }

    /**
     * Logically grouping the view controls under this internal class.

     * @author Juan Heyns
     */
    private inner class InternalView : JPanel() {

        private var centerPanel: JPanel? = null
        private var northCenterPanel: JPanel? = null
        private var northPanel: JPanel? = null
        private var southPanel: JPanel? = null
        private var previousButtonPanel: JPanel? = null
        private var nextButtonPanel: JPanel? = null
        private var dayTable: JTable? = null
        private var dayTableHeader: JTableHeader? = null
        private var dayTableCellRenderer: InternalTableCellRenderer? = null
        private var monthLabel: JLabel? = null
        private var todayLabel: JLabel? = null
        private var noneLabel: JLabel? = null
        private var monthPopupMenu: JPopupMenu? = null
        private var monthPopupMenuItems: Array<JMenuItem>? = null
        private var nextMonthButton: JButton? = null
        private var previousMonthButton: JButton? = null
        private var previousYearButton: JButton? = null
        private var nextYearButton: JButton? = null
        private var yearSpinner: JSpinner? = null

        /**
         * Update the scroll buttons UI.
         */
        fun updateShowYearButtons() {
            if (showYearButtons) {
                getNextButtonPanel().add(getNextYearButton())
                getPreviousButtonPanel().removeAll()
                getPreviousButtonPanel().add(getPreviousYearButton())
                getPreviousButtonPanel().add(getPreviousMonthButton())
            } else {
                getNextButtonPanel().remove(getNextYearButton())
                getPreviousButtonPanel().remove(getPreviousYearButton())
            }
        }

        /**
         * Update the UI of the monthLabel
         */
        fun updateMonthLabel() {
            monthLabel!!.text = texts.getText(ComponentTextDefaults.Key.getMonthKey(internalModel.model.month))
        }

        init {
            layout = java.awt.BorderLayout()
            setSize(200, 180)
            preferredSize = java.awt.Dimension(200, 180)
            isOpaque = false
            add(getNorthPanel(), java.awt.BorderLayout.NORTH)
            add(getSouthPanel(), java.awt.BorderLayout.SOUTH)
            add(getCenterPanel(), java.awt.BorderLayout.CENTER)
        }

        /**
         * This method initializes northPanel
         */
        private fun getNorthPanel(): JPanel {
            if (northPanel == null) {
                northPanel = javax.swing.JPanel()
                northPanel!!.layout = java.awt.BorderLayout()
                northPanel!!.name = ""
                northPanel!!.border = javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3)
                northPanel!!.background = colors.getColor(ComponentColorDefaults.Key.BG_MONTH_SELECTOR)
                northPanel!!.add(getPreviousButtonPanel(), java.awt.BorderLayout.WEST)
                northPanel!!.add(getNextButtonPanel(), java.awt.BorderLayout.EAST)
                northPanel!!.add(getNorthCenterPanel(), java.awt.BorderLayout.CENTER)
            }
            return northPanel!!
        }

        /**
         * This method initializes northCenterPanel
         */
        private fun getNorthCenterPanel(): JPanel {
            if (northCenterPanel == null) {
                northCenterPanel = javax.swing.JPanel()
                northCenterPanel!!.layout = java.awt.BorderLayout()
                northCenterPanel!!.border = javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5)
                northCenterPanel!!.isOpaque = false
                northCenterPanel!!.add(getMonthLabel(), java.awt.BorderLayout.CENTER)
                northCenterPanel!!.add(getYearSpinner(), java.awt.BorderLayout.EAST)
            }
            return northCenterPanel!!
        }

        /**
         * This method initializes monthLabel
         */
        fun getMonthLabel(): JLabel {
            if (monthLabel == null) {
                monthLabel = javax.swing.JLabel()
                monthLabel!!.foreground = colors.getColor(ComponentColorDefaults.Key.FG_MONTH_SELECTOR)
                monthLabel!!.horizontalAlignment = javax.swing.SwingConstants.CENTER
                monthLabel!!.addMouseListener(internalController)
                updateMonthLabel()
            }
            return monthLabel!!
        }

        /**
         * This method initializes yearSpinner
         */
        private fun getYearSpinner(): JSpinner {
            if (yearSpinner == null) {
                yearSpinner = javax.swing.JSpinner()
                yearSpinner!!.model = internalModel
            }
            return yearSpinner!!
        }

        /**
         * This method initializes southPanel
         */
        private fun getSouthPanel(): JPanel {
            if (southPanel == null) {
                southPanel = javax.swing.JPanel()
                southPanel!!.layout = java.awt.BorderLayout()
                southPanel!!.background = colors.getColor(ComponentColorDefaults.Key.BG_TODAY_SELECTOR)
                southPanel!!.border = javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3)
                southPanel!!.add(getTodayLabel(), java.awt.BorderLayout.WEST)
                southPanel!!.add(getNoneLabel(), java.awt.BorderLayout.EAST)
            }
            return southPanel!!
        }

        /**
         * This method initializes todayLabel
         */
        fun getNoneLabel(): JLabel {
            if (noneLabel == null) {
                noneLabel = javax.swing.JLabel()
                noneLabel!!.foreground = colors.getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED)
                noneLabel!!.horizontalAlignment = javax.swing.SwingConstants.CENTER
                noneLabel!!.addMouseListener(internalController)
                //TODO get the translations for each language before adding this in
                //noneLabel.setToolTipText(getText(ComponentTextDefaults.CLEAR));
                noneLabel!!.icon = icons.clearIcon
            }
            return noneLabel!!
        }

        fun updateTodayLabel() {
            val now = Calendar.getInstance()
            val df = formats.getFormat(ComponentFormatDefaults.Key.TODAY_SELECTOR)
            todayLabel!!.text = texts.getText(ComponentTextDefaults.Key.TODAY) + ": " + df.format(now.time)
        }

        /**
         * This method initializes todayLabel
         */
        fun getTodayLabel(): JLabel {
            if (todayLabel == null) {
                todayLabel = javax.swing.JLabel()
                todayLabel!!.foreground = colors.getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED)
                todayLabel!!.horizontalAlignment = javax.swing.SwingConstants.CENTER
                todayLabel!!.addMouseListener(internalController)
                updateTodayLabel()
            }
            return todayLabel!!
        }

        /**
         * This method initializes centerPanel
         */
        private fun getCenterPanel(): JPanel {
            if (centerPanel == null) {
                centerPanel = javax.swing.JPanel()
                centerPanel!!.layout = java.awt.BorderLayout()
                centerPanel!!.isOpaque = false
                centerPanel!!.add(getDayTableHeader(), java.awt.BorderLayout.NORTH)
                centerPanel!!.add(getDayTable(), java.awt.BorderLayout.CENTER)
            }
            return centerPanel!!
        }

        /**
         * This method initializes dayTable
         */
        fun getDayTable(): JTable {
            if (dayTable == null) {
                dayTable = javax.swing.JTable()
                dayTable!!.autoResizeMode = javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS
                dayTable!!.model = internalModel
                dayTable!!.setShowGrid(true)
                dayTable!!.gridColor = colors.getColor(ComponentColorDefaults.Key.BG_GRID)
                dayTable!!.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
                dayTable!!.cellSelectionEnabled = true
                dayTable!!.rowSelectionAllowed = true
                dayTable!!.isFocusable = false
                dayTable!!.addMouseListener(internalController)
                for (i in 0..6) {
                    val column = dayTable!!.columnModel.getColumn(i)
                    column.cellRenderer = getDayTableCellRenderer()
                }
                dayTable!!.addComponentListener(object : ComponentListener {

                    override fun componentResized(e: ComponentEvent) {
                        // The new size of the table
                        val w = e.component.size.getWidth()
                        val h = e.component.size.getHeight()

                        // Set the size of the font as a fraction of the width or the height, whichever is smallest
                        val sw = Math.floor(w / 16).toFloat()
                        val sh = Math.floor(h / 8).toFloat()
                        dayTable!!.font = dayTable!!.font.deriveFont(Math.min(sw, sh))

                        // Set the row height as a fraction of the height
                        val r = Math.floor(h / 6).toInt()
                        dayTable!!.rowHeight = r
                    }

                    override fun componentMoved(e: ComponentEvent) {
                        // Do nothing
                    }

                    override fun componentShown(e: ComponentEvent) {
                        // Do nothing
                    }

                    override fun componentHidden(e: ComponentEvent) {
                        // Do nothing
                    }

                })
            }
            return dayTable!!
        }

        private fun getDayTableCellRenderer(): InternalTableCellRenderer {
            if (dayTableCellRenderer == null) {
                dayTableCellRenderer = InternalTableCellRenderer()
            }
            return dayTableCellRenderer!!
        }

        private fun getDayTableHeader(): JTableHeader {
            if (dayTableHeader == null) {
                dayTableHeader = getDayTable().tableHeader
                dayTableHeader!!.resizingAllowed = false
                dayTableHeader!!.reorderingAllowed = false
                dayTableHeader!!.defaultRenderer = getDayTableCellRenderer()
            }
            return dayTableHeader!!
        }

        /**
         * This method initializes previousButtonPanel
         */
        private fun getPreviousButtonPanel(): JPanel {
            if (previousButtonPanel == null) {
                previousButtonPanel = javax.swing.JPanel()
                val layout = java.awt.GridLayout(1, 2)
                layout.hgap = 3
                previousButtonPanel!!.layout = layout
                previousButtonPanel!!.name = ""
                previousButtonPanel!!.isOpaque = false
                if (isShowYearButtons) {
                    previousButtonPanel!!.add(getPreviousYearButton())
                }
                previousButtonPanel!!.add(getPreviousMonthButton())
            }
            return previousButtonPanel!!
        }

        /**
         * This method initializes nextButtonPanel
         */
        private fun getNextButtonPanel(): JPanel {
            if (nextButtonPanel == null) {
                nextButtonPanel = javax.swing.JPanel()
                val layout = java.awt.GridLayout(1, 2)
                layout.hgap = 3
                nextButtonPanel!!.layout = layout
                nextButtonPanel!!.name = ""
                nextButtonPanel!!.isOpaque = false
                nextButtonPanel!!.add(getNextMonthButton())
                if (isShowYearButtons) {
                    nextButtonPanel!!.add(getNextYearButton())
                }
            }
            return nextButtonPanel!!
        }

        /**
         * This method initializes nextMonthButton
         */
        fun getNextMonthButton(): JButton {
            if (nextMonthButton == null) {
                nextMonthButton = JButton()
                nextMonthButton!!.icon = icons.nextMonthIconEnabled
                nextMonthButton!!.disabledIcon = icons.nextMonthIconDisabled
                nextMonthButton!!.text = ""
                nextMonthButton!!.preferredSize = java.awt.Dimension(20, 15)
                nextMonthButton!!.border = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)
                nextMonthButton!!.isFocusable = false
                nextMonthButton!!.isOpaque = true
                nextMonthButton!!.addActionListener(internalController)
                nextMonthButton!!.toolTipText = texts.getText(ComponentTextDefaults.Key.MONTH)
            }
            return nextMonthButton!!
        }

        /**
         * This method initializes nextYearButton
         */
        fun getNextYearButton(): JButton {
            if (nextYearButton == null) {
                nextYearButton = JButton()
                nextYearButton!!.icon = icons.nextYearIconEnabled
                nextYearButton!!.disabledIcon = icons.nextMonthIconDisabled
                nextYearButton!!.text = ""
                nextYearButton!!.preferredSize = java.awt.Dimension(20, 15)
                nextYearButton!!.border = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)
                nextYearButton!!.isFocusable = false
                nextYearButton!!.isOpaque = true
                nextYearButton!!.addActionListener(internalController)
                nextYearButton!!.toolTipText = texts.getText(ComponentTextDefaults.Key.YEAR)
            }
            return nextYearButton!!
        }

        /**
         * This method initializes previousMonthButton
         */
        fun getPreviousMonthButton(): JButton {
            if (previousMonthButton == null) {
                previousMonthButton = JButton()
                previousMonthButton!!.icon = icons.previousMonthIconEnabled
                previousMonthButton!!.disabledIcon = icons.previousMonthIconDisabled
                previousMonthButton!!.text = ""
                previousMonthButton!!.preferredSize = java.awt.Dimension(20, 15)
                previousMonthButton!!.border = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)
                previousMonthButton!!.isFocusable = false
                previousMonthButton!!.isOpaque = true
                previousMonthButton!!.addActionListener(internalController)
                previousMonthButton!!.toolTipText = texts.getText(ComponentTextDefaults.Key.MONTH)
            }
            return previousMonthButton!!
        }

        /**
         * This method initializes previousMonthButton
         */
        fun getPreviousYearButton(): JButton {
            if (previousYearButton == null) {
                previousYearButton = JButton()
                previousYearButton!!.icon = icons.previousYearIconEnabled
                previousYearButton!!.disabledIcon = icons.previousYearIconDisabled
                previousYearButton!!.text = ""
                previousYearButton!!.preferredSize = java.awt.Dimension(20, 15)
                previousYearButton!!.border = javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)
                previousYearButton!!.isFocusable = false
                previousYearButton!!.isOpaque = true
                previousYearButton!!.addActionListener(internalController)
                previousYearButton!!.toolTipText = texts.getText(ComponentTextDefaults.Key.YEAR)
            }
            return previousYearButton!!
        }

        /**
         * This method initializes monthPopupMenu
         */
        fun getMonthPopupMenu(): JPopupMenu {
            if (monthPopupMenu == null) {
                monthPopupMenu = javax.swing.JPopupMenu()
                val menuItems = getMonthPopupMenuItems()
                for (i in menuItems.indices) {
                    monthPopupMenu!!.add(menuItems[i])
                }
            }
            return monthPopupMenu!!
        }

        fun getMonthPopupMenuItems(): Array<JMenuItem> {
            if (monthPopupMenuItems == null) {
                val list = ArrayList<JMenuItem>(12)
                for (i in 0..11) {
                    list.add(JMenuItem(texts.getText(ComponentTextDefaults.Key.getMonthKey(i))).apply { addActionListener(internalController) })
                }
                monthPopupMenuItems = list.toTypedArray()
            }
            return monthPopupMenuItems!!
        }

        override fun setEnabled(enabled: Boolean) {
            dayTable!!.isEnabled = enabled
            dayTableCellRenderer!!.isEnabled = enabled
            nextMonthButton!!.isEnabled = enabled
            if (nextYearButton != null) {
                nextYearButton!!.isEnabled = enabled
            }
            previousMonthButton!!.isEnabled = enabled
            if (previousYearButton != null) {
                previousYearButton!!.isEnabled = enabled
            }
            yearSpinner!!.isEnabled = enabled
            if (enabled) {
                todayLabel!!.foreground = colors.getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED)
            } else {
                todayLabel!!.foreground = colors.getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_DISABLED)
            }

            super.setEnabled(enabled)
        }

    }

    /**
     * This inner class renders the table of the days, setting colors based on
     * whether it is in the month, if it is today, if it is selected etc.
     */
    private inner class InternalTableCellRenderer : DefaultTableCellRenderer() {

        override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
            // Exit this method if the value is null, encountered from JTable#AccessibleJTable
            if (value == null) {
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            }

            val label = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column) as JLabel
            label.horizontalAlignment = JLabel.CENTER

            if (row == -1) {
                label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_HEADER)
                label.background = colors.getColor(ComponentColorDefaults.Key.BG_GRID_HEADER)
                label.horizontalAlignment = JLabel.CENTER
                return label
            }

            val todayCal = Calendar.getInstance()
            val selectedCal = Calendar.getInstance()
            selectedCal.set(internalModel.model.year, internalModel.model.month, internalModel.model.day)

            val cellDayValue = (value as Int?)!!
            val lastDayOfMonth = selectedCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Other month
            if (cellDayValue < 1 || cellDayValue > lastDayOfMonth) {
                label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_OTHER_MONTH)

                val calForDay = Calendar.getInstance()
                calForDay.set(internalModel.model.year, internalModel.model.month, cellDayValue)
                val modelForDay = UtilCalendarModel(calForDay)
                label.background = if (checkConstraints(modelForDay))
                    colors.getColor(ComponentColorDefaults.Key.BG_GRID)
                else
                    colors.getColor(ComponentColorDefaults.Key.BG_GRID_NOT_SELECTABLE)

                //Past end of month
                if (cellDayValue > lastDayOfMonth) {
                    label.text = Integer.toString(cellDayValue - lastDayOfMonth)
                } else {
                    val lastMonth = GregorianCalendar()
                    lastMonth.set(selectedCal.get(Calendar.YEAR), selectedCal.get(Calendar.MONTH) - 1, 1)
                    val lastDayLastMonth = lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                    label.text = Integer.toString(lastDayLastMonth + cellDayValue)
                }//Before start of month
            } else {
                label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_THIS_MONTH)

                val calForDay = Calendar.getInstance()
                calForDay.set(internalModel.model.year, internalModel.model.month, cellDayValue)
                val modelForDay = UtilCalendarModel(calForDay)
                label.background = if (checkConstraints(modelForDay))
                    colors.getColor(ComponentColorDefaults.Key.BG_GRID)
                else
                    colors.getColor(ComponentColorDefaults.Key.BG_GRID_NOT_SELECTABLE)

                //Today
                if (todayCal.get(Calendar.DATE) == cellDayValue
                        && todayCal.get(Calendar.MONTH) == internalModel.model.month
                        && todayCal.get(Calendar.YEAR) == internalModel.model.year) {
                    label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_TODAY)
                    //Selected
                    if (internalModel.model.isSelected && selectedCal.get(Calendar.DATE) == cellDayValue) {
                        label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_TODAY_SELECTED)
                        label.background = colors.getColor(ComponentColorDefaults.Key.BG_GRID_TODAY_SELECTED)
                    }
                } else {
                    //Selected
                    if (internalModel.model.isSelected && selectedCal.get(Calendar.DATE) == cellDayValue) {
                        label.foreground = colors.getColor(ComponentColorDefaults.Key.FG_GRID_SELECTED)
                        label.background = colors.getColor(ComponentColorDefaults.Key.BG_GRID_SELECTED)
                    }
                }//Other day
            }//This month

            return label
        }

    }

    /**
     * This inner class hides the public view event handling methods from the
     * outside. This class acts as an internal controller for this component. It
     * receives events from the view components and updates the model.
     */
    private inner class InternalController : ActionListener, MouseListener {

        /**
         * Next, Previous and Month buttons clicked, causes the model to be updated.
         */
        override fun actionPerformed(arg0: ActionEvent) {
            if (!this@JDatePanel.isEnabled) {
                return
            }

            if (arg0.source === internalView.getNextMonthButton()) {
                internalModel.model.addMonth(1)
            } else if (arg0.source === internalView.getPreviousMonthButton()) {
                internalModel.model.addMonth(-1)
            } else if (arg0.source === internalView.getNextYearButton()) {
                internalModel.model.addYear(1)
            } else if (arg0.source === internalView.getPreviousYearButton()) {
                internalModel.model.addYear(-1)
            } else {
                for (month in 0..internalView.getMonthPopupMenuItems().size - 1) {
                    if (arg0.source === internalView.getMonthPopupMenuItems()[month]) {
                        internalModel.model.month = month
                    }
                }
            }
        }

        /**
         * Mouse down on monthLabel pops up a table. Mouse down on todayLabel
         * sets the value of the internal model to today. Mouse down on day
         * table will set the day to the value. Mouse down on none label will
         * clear the date.
         */
        override fun mousePressed(arg0: MouseEvent) {
            if (!this@JDatePanel.isEnabled) {
                return
            }

            if (arg0.source === internalView.getMonthLabel()) {
                internalView.getMonthPopupMenu().isLightWeightPopupEnabled = false
                internalView.getMonthPopupMenu().show(arg0.source as Component, arg0.x, arg0.y)
            } else if (arg0.source === internalView.getTodayLabel()) {
                val today = Calendar.getInstance()
                internalModel.model.setDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE))
            } else if (arg0.source === internalView.getDayTable()) {
                val row = internalView.getDayTable().selectedRow
                val col = internalView.getDayTable().selectedColumn
                if (row >= 0 && row <= 5) {
                    val date = internalModel.getValueAt(row, col) as Int

                    // check constraints
                    val oldDay = internalModel.model.day
                    internalModel.model.day = date
                    if (!checkConstraints(internalModel.model)) {
                        // rollback
                        internalModel.model.day = oldDay
                        return
                    }

                    internalModel.model.isSelected = true

                    if (isDoubleClickAction && arg0.clickCount == 2) {
                        fireActionPerformed()
                    }
                    if (!isDoubleClickAction) {
                        fireActionPerformed()
                    }
                }
            } else if (arg0.source === internalView.getNoneLabel()) {
                internalModel.model.isSelected = false

                if (isDoubleClickAction && arg0.clickCount == 2) {
                    fireActionPerformed()
                }
                if (!isDoubleClickAction) {
                    fireActionPerformed()
                }
            }
        }

        override fun mouseClicked(arg0: MouseEvent) {
        }

        override fun mouseEntered(arg0: MouseEvent) {
        }

        override fun mouseExited(arg0: MouseEvent) {
        }

        override fun mouseReleased(arg0: MouseEvent) {
        }

    }

    /**
     * This model represents the selected date. The model implements the
     * TableModel interface for displaying days, and it implements the
     * SpinnerModel for the year.
     */
    protected inner class InternalCalendarModel(val model: DateModel<*>) : TableModel, SpinnerModel, ChangeListener {
        private val spinnerChangeListeners: MutableSet<ChangeListener>
        private val tableModelListeners: MutableSet<TableModelListener>

        init {
            this.spinnerChangeListeners = HashSet<ChangeListener>()
            this.tableModelListeners = HashSet<TableModelListener>()
            model.addChangeListener(this)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun addChangeListener(arg0: ChangeListener) {
            spinnerChangeListeners.add(arg0)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun removeChangeListener(arg0: ChangeListener) {
            spinnerChangeListeners.remove(arg0)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun getNextValue(): Any {
            return Integer.toString(model.year + 1)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun getPreviousValue(): Any {
            return Integer.toString(model.year - 1)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun setValue(text: Any) {
            val year = text as String
            model.year = Integer.parseInt(year)
        }

        /**
         * Part of SpinnerModel, year
         */
        override fun getValue(): Any {
            return Integer.toString(model.year)
        }

        /**
         * Part of TableModel, day
         */
        override fun addTableModelListener(arg0: TableModelListener) {
            tableModelListeners.add(arg0)
        }

        /**
         * Part of TableModel, day
         */
        override fun removeTableModelListener(arg0: TableModelListener) {
            tableModelListeners.remove(arg0)
        }

        /**
         * Part of TableModel, day
         */
        override fun getColumnCount(): Int {
            return 7
        }

        /**
         * Part of TableModel, day
         */
        override fun getRowCount(): Int {
            return 6
        }

        /**
         * Part of TableModel, day
         */
        override fun getColumnName(columnIndex: Int): String {
            val key = ComponentTextDefaults.Key.getDowKey((firstDayOfWeek - 1 + columnIndex) % 7)
            return texts.getText(key)
        }

        private var lookup: IntArray? = null

        /**
         * Results in a mapping which calculates the number of days before the first day of month

         * DAY OF WEEK
         * M T W T F S S
         * 1 2 3 4 5 6 0

         * or

         * S M T W T F S
         * 0 1 2 3 4 5 6

         * DAYS BEFORE
         * 0 1 2 3 4 5 6

         * @return
         */
        private fun lookup(): IntArray {
            if (lookup == null) {
                lookup = IntArray(8)
                lookup!![(firstDayOfWeek - 1) % 7] = 0
                lookup!![(firstDayOfWeek + 0) % 7] = 1
                lookup!![(firstDayOfWeek + 1) % 7] = 2
                lookup!![(firstDayOfWeek + 2) % 7] = 3
                lookup!![(firstDayOfWeek + 3) % 7] = 4
                lookup!![(firstDayOfWeek + 4) % 7] = 5
                lookup!![(firstDayOfWeek + 5) % 7] = 6
            }
            return lookup!!
        }

        /**
         * Part of TableModel, day

         * previous month (... -1, 0) ->
         * current month (1...DAYS_IN_MONTH) ->
         * next month (DAYS_IN_MONTH + 1, DAYS_IN_MONTH + 2, ...)
         */
        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            val series = columnIndex + rowIndex * 7 + 1

            val firstOfMonth = Calendar.getInstance()
            firstOfMonth.set(model.year, model.month, 1)
            val dowForFirst = firstOfMonth.get(Calendar.DAY_OF_WEEK)
            val daysBefore = lookup()[dowForFirst - 1]

            return series - daysBefore
        }

        /**
         * Part of TableModel, day
         */
        @SuppressWarnings("unchecked", "rawtypes")
        override fun getColumnClass(arg0: Int): Class<*> {
            return Int::class.java
        }

        /**
         * Part of TableModel, day
         */
        override fun isCellEditable(arg0: Int, arg1: Int): Boolean {
            return false
        }

        /**
         * Part of TableModel, day
         */
        override fun setValueAt(arg0: Any, arg1: Int, arg2: Int) {
        }

        /**
         * Called whenever a change is made to the model value. Notify the
         * internal listeners and update the simple controls. Also notifies the
         * (external) ChangeListeners of the component, since the internal state
         * has changed.
         */
        private fun fireValueChanged() {
            //Update year spinner
            for (cl in spinnerChangeListeners) {
                cl.stateChanged(ChangeEvent(this))
            }

            //Update month label
            internalView.updateMonthLabel()

            //Update day table
            for (tl in tableModelListeners) {
                tl.tableChanged(TableModelEvent(this))
            }
        }

        /**
         * The model has changed and needs to notify the InternalModel.
         */
        override fun stateChanged(e: ChangeEvent) {
            fireValueChanged()
        }

    }

    companion object {

        fun createModel(): DateModel<Calendar> {
            return UtilCalendarModel()
        }

        private fun createModel(value: Calendar): DateModel<Calendar> {
            return UtilCalendarModel(value)
        }

        private fun createModelFromValue(value: Any): DateModel<*> {
            if (value is java.util.Calendar) {
                return UtilCalendarModel(value)
            }
            if (value is java.util.Date) {
                return UtilDateModel(value)
            }
            if (value is java.sql.Date) {
                return SqlDateModel(value)
            }
            throw IllegalArgumentException("No model could be constructed from the initial value object.")
        }

        private val texts = ComponentTextDefaults
        private val icons = ComponentIconDefaults
        private val colors = ComponentColorDefaults
        private val formats = ComponentFormatDefaults
    }

}