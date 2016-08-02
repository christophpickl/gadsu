package at.cpickl.gadsu.view.datepicker

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.view.Images
import java.awt.Color
import java.awt.SystemColor
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.swing.Icon


object ComponentColorDefaults {

    enum class Key {
        FG_MONTH_SELECTOR,
        BG_MONTH_SELECTOR,
        FG_GRID_HEADER,
        BG_GRID_HEADER,
        FG_GRID_THIS_MONTH,
        FG_GRID_OTHER_MONTH,
        FG_GRID_TODAY,
        BG_GRID,
        BG_GRID_NOT_SELECTABLE,
        FG_GRID_SELECTED,
        BG_GRID_SELECTED,
        FG_GRID_TODAY_SELECTED,
        BG_GRID_TODAY_SELECTED,
        FG_TODAY_SELECTOR_ENABLED,
        FG_TODAY_SELECTOR_DISABLED,
        BG_TODAY_SELECTOR,
        POPUP_BORDER
    }

    private val colors = mutableMapOf(
            Key.FG_MONTH_SELECTOR to SystemColor.activeCaptionText,
            Key.BG_MONTH_SELECTOR to SystemColor.activeCaption,
            Key.FG_GRID_HEADER to Color(10, 36, 106),
            Key.BG_GRID_HEADER to Color.LIGHT_GRAY,

            Key.FG_GRID_THIS_MONTH to Color.BLACK,
            Key.FG_GRID_OTHER_MONTH to Color.LIGHT_GRAY,
            Key.FG_GRID_TODAY to Color.RED,
            Key.BG_GRID to Color.WHITE,
            Key.BG_GRID_NOT_SELECTABLE to Color(240, 240, 240),

            Key.FG_GRID_SELECTED to Color.WHITE,
            Key.BG_GRID_SELECTED to Color(10, 36, 106),

            Key.FG_GRID_TODAY_SELECTED to Color.RED,
            Key.BG_GRID_TODAY_SELECTED to Color(10, 36, 106),

            Key.FG_TODAY_SELECTOR_ENABLED to Color.BLACK,
            Key.FG_TODAY_SELECTOR_DISABLED to Color.LIGHT_GRAY,
            Key.BG_TODAY_SELECTOR to Color.WHITE,

            Key.POPUP_BORDER to Color.BLACK
        )

    fun getColor(key: Key) = colors[key]!!

    fun setColor(key: Key, color: Color) {
        colors.put(key, color)
    }

}


object ComponentFormatDefaults {

    enum class Key {
        TODAY_SELECTOR,
        DOW_HEADER,
        MONTH_SELECTOR,
        SELECTED_DATE_FIELD
    }

    private val formats = mutableMapOf(
            Key.TODAY_SELECTOR to SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM),
            Key.DOW_HEADER to SimpleDateFormat("EE"),
            Key.MONTH_SELECTOR to SimpleDateFormat("MMMM"),
            Key.SELECTED_DATE_FIELD to SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
    )

    fun getFormat(key: Key) = formats[key]!!

    fun setFormat(key: Key, format: DateFormat) {
        formats.put(key, format)
    }

}


object ComponentIconDefaults {

    private val CLEAR = "/gadsu/images/datepicker_clear.png"

    enum class Key// TODO

    var clearIcon: Icon? = null
    var nextMonthIconEnabled: Icon? = null
    var nextYearIconEnabled: Icon? = null
    var previousMonthIconEnabled: Icon? = null
    var previousYearIconEnabled: Icon? = null
    var nextMonthIconDisabled: Icon? = null
    var nextYearIconDisabled: Icon? = null
    var previousMonthIconDisabled: Icon? = null
    var previousYearIconDisabled: Icon? = null
    var popupButtonIcon: Icon? = null

    init {
        // TODO consider making all the icons vector images which will scale
        try {
            clearIcon = Images.loadFromClasspath(CLEAR)
            nextMonthIconEnabled = JNextIcon(4, 7, false, true)
            nextYearIconEnabled = JNextIcon(8, 7, true, true)
            previousMonthIconEnabled = JPreviousIcon(4, 7, false, true)
            previousYearIconEnabled = JPreviousIcon(8, 7, true, true)
            nextMonthIconDisabled = JNextIcon(4, 7, false, false)
            nextYearIconDisabled = JNextIcon(8, 7, true, false)
            previousMonthIconDisabled = JPreviousIcon(4, 7, false, false)
            previousYearIconDisabled = JPreviousIcon(8, 7, true, false)
            popupButtonIcon = null
        } catch (e: IOException) {
            throw GadsuException("Oh noes, load icons failed!", e)
        }

    }

}

/**
 * Instantiated with the values which is default for the current locale.
 */
object ComponentTextDefaults {

    enum class Key constructor(val property: String, val kind: String, val index: Int? = null) {
        // General texts
        TODAY("text.today", "general"),
        MONTH("text.month", "general"),
        YEAR("text.year", "general"),
        CLEAR("text.clear", "general"),

        // Months of the year
        JANUARY("text.january", "month", 0),
        FEBRUARY("text.february", "month", 1),
        MARCH("text.march", "month", 2),
        APRIL("text.april", "month", 3),
        MAY("text.may", "month", 4),
        JUNE("text.june", "month", 5),
        JULY("text.july", "month", 6),
        AUGUST("text.august", "month", 7),
        SEPTEMBER("text.september", "month", 8),
        OCTOBER("text.october", "month", 9),
        NOVEMBER("text.november", "month", 10),
        DECEMBER("text.december", "month", 11),

        // Days of the week abbreviated where necessary
        SUN("text.sun", "dow", Calendar.SUNDAY),
        MON("text.mon", "dow", Calendar.MONDAY),
        TUE("text.tue", "dow", Calendar.TUESDAY),
        WED("text.wed", "dow", Calendar.WEDNESDAY),
        THU("text.thu", "dow", Calendar.THURSDAY),
        FRI("text.fri", "dow", Calendar.FRIDAY),
        SAT("text.sat", "dow", Calendar.SATURDAY);

        companion object {
            fun getMonthKey(index: Int) = values().firstOrNull { "month" == it.kind && index == it.index } ?: throw IllegalArgumentException("Invalid index: $index")
            fun getDowKey(index: Int) = values().firstOrNull { "dow" == it.kind && index == it.index } ?: throw IllegalArgumentException("Invalid index: $index")
        }
    }

    private val texts = Properties().apply {
        put("text.today", "Heute")
        put("text.month", "Monat")
        put("text.year", "Jahr")
        put("text.clear", "L\u00f6schen")
    }
        //toProperties(ResourceBundle.getBundle("org.jdatepicker.i18n.Text", Locale.getDefault()))

//    private fun toProperties(resource: ResourceBundle): Properties {
//        val result = Properties()
//        val keys = resource.keys
//        while (keys.hasMoreElements()) {
//            val key = keys.nextElement()
//            result.put(key, resource.getString(key))
//        }
//        return result
//    }

    /**
     * For general texts retrieve from the resource bundles.
     *
     * For months and day of the week use the SimpleDateFormat symbols. In most cases these are the correct ones, but
     * we may want to override it, so if a text is specified then we will not consider the SimpleDateFormat symbols.
     */
    fun getText(key: Key): String {
        val text: String? = texts.getProperty(key.property)
        if (text != null) return text

        return if ("month" == key.kind) {
            val c = Calendar.getInstance()
            c.set(Calendar.MONTH, key.index!!)
            val monthFormat = ComponentFormatDefaults.getFormat(ComponentFormatDefaults.Key.MONTH_SELECTOR)
            monthFormat.format(c.time)
        } else if ("dow" == key.kind) {
            val c = Calendar.getInstance()
            c.set(Calendar.DAY_OF_WEEK, key.index!!)
            val dowFormat = ComponentFormatDefaults.getFormat(ComponentFormatDefaults.Key.DOW_HEADER)
            dowFormat.format(c.time)
        } else {
            throw IllegalArgumentException("Unhandled key: $key")
        }
    }

    fun setText(key: Key, value: String) {
        texts.setProperty(key.property, value)
    }

}
